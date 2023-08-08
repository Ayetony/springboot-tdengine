package com.iot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.constants.OpcConstants;
import com.entity.OpcServer;
import com.entity.OpcTag;
import com.entity.TagValue;
import com.iot.Service.OpcConsumerLocator;
import com.iot.util.OpcUtil;
import com.mapper.mysql.OpcServerMapper;
import com.mapper.mysql.OpcTagMapper;
import com.utils.RedisUtils;
import com.websocket.AvicWebSocketOperator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * @program: springboot-tdengine
 * @ClassName OpcSubscription
 * @author: ayetony miao
 * @create: 2023-08-07 22:01
 **/
@Slf4j
@Component
public class OpcSubscription {

    private final Map<OpcServer, OpcUaClient> clientMap = MapUtil.newConcurrentHashMap();
    private final Map<OpcUaClient, Boolean> clientStatusMap = MapUtil.newConcurrentHashMap();
    private final Set<UaSubscription> failedSubscriptionSet = CollUtil.newLinkedHashSet();
    //kepServer 监控项
    List<UaMonitoredItem> uaMonitoredItemList = CollUtil.newArrayList();
    @Resource
    OpcConsumerLocator opcConsumerLocator;
    @Value("${opc-ua.requestedPublishingInterval:1000.0}")
    private double requestedPublishingInterval;
    @Value("${opc-ua.samplingInterval:1000.0}")
    private double samplingInterval;
    @Value("${opc-ua.queueSize:10}")
    private int queueSize;
    @Resource
    private AvicWebSocketOperator webSocketOperator;
    @Resource
    private OpcServerMapper opcServerMapper;
    @Resource
    private OpcTagMapper opcTagMapper;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 系统启动时加载：启动所有可用的OPCServer、启动订阅
     */
    @PostConstruct
    private void init() {
        // 自动连接开启的OPC服务器
        List<OpcServer> enabledOpcServerList = opcServerMapper.selectAllOpcServers();
        for (OpcServer opcServer : enabledOpcServerList) {
            this.run(opcServer);
            // 通知所有实现类初始化
            opcConsumerLocator.invokeInit(opcServer);
        }
    }

    /**
     * 启动OPC服务器
     *
     * @param opcServer
     * @return
     */
    public Boolean run(OpcServer opcServer) {
        if (opcServer == null) {
            log.warn("Opc UA Server Not Found Exception");
            return false;
        }

        if (this.clientMap.containsKey(opcServer)) {
            return false;
        }

        List<OpcTag> iotDataItemList = this.opcTagMapper.selectList(opcServer.getId(), OpcConstants.NodeType.TAG);

        if (CollUtil.isEmpty(iotDataItemList)) {
            log.warn("Opc Server Tag Empty Exception");
            return false;
        }
        // TODO 把所有点位的code都放在redis里
        try {
            OpcUaClient client = OpcUtil.createClient(opcServer.getUrl(), opcServer.getOpcUserName(), opcServer.getOpcPassword());
            UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> item.setValueConsumer((i, v) -> dataValueConsumer(i, v, opcServer.getId()));
            this.subscribe(client, iotDataItemList, onItemCreated, this.requestedPublishingInterval, this.samplingInterval, this.queueSize);
            this.clientMap.put(opcServer, client);
            opcServer.setEnabled(1);
//            this.opcServerMapper.updateById(opcServer);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 订阅到数据时
     *
     * @param item
     * @param value
     */
    public void dataValueConsumer(UaMonitoredItem item, DataValue value, Long opcServerId) {
        String itemId = (String) item.getReadValueId().getNodeId().getIdentifier();

        String opcCode = itemId;
        TagValue tagValue = OpcUtil.buildOpcValue(opcCode, itemId, value);
        tagValue.setOpcServerId(opcServerId);
        // 是否是重复回调
        if (!repeatCallback(tagValue)) {
            log.info("dataValueConsumer receive tag:{}", tagValue.getValue());
            // TODO 消息写入influxDb
            // TODO 消息写入kafka
            // 消息WebSocket推送
            log.info("----- tag opc ,name = {} value:{}",tagValue.getTagName(),tagValue.asString());
            webSocketOperator.sendAllMessage(OpcConstants.OpcMsgType.TAG, tagValue);
            // 写入Redis
            redisUtils.set(itemId, tagValue);
            // 通知所有实现类
            opcConsumerLocator.invokeCallback(tagValue);
        }

    }


    /**
     * 是否是重复回调
     *
     * @param tagValue tagValue
     * @return true-是，false-否
     */
    private Boolean repeatCallback(TagValue tagValue) {
        String key = tagValue.getOpcServerId() + tagValue.getTagName() + tagValue.getValue();
        // true-存在，false-不存在
        boolean contains = redisUtils.hasKey(key);
        if (!contains) {
            redisUtils.set(key, tagValue.getValue(), 3L);
        }
        return contains;
    }


    /**
     * 订阅消息，requestedPublishingInterval 为1000.0ms
     *
     * @param client
     * @param opcTagList
     * @param onItemCreated
     */
    private void subscribe(OpcUaClient client, List<OpcTag> opcTagList, UaSubscription.ItemCreationCallback onItemCreated) {
        subscribe(client, opcTagList, onItemCreated, this.requestedPublishingInterval, this.samplingInterval, this.queueSize);
    }


    /**
     * 订阅消息
     *
     * @param client
     * @param opcTagList
     * @param onItemCreated
     * @param requestedPublishingInterval
     * @param samplingInterval
     * @param queueSize
     */
    public void subscribe(OpcUaClient client, List<OpcTag> opcTagList, UaSubscription.ItemCreationCallback onItemCreated, double requestedPublishingInterval, double samplingInterval, int queueSize) {
        try {
            // 创建连接
            client.connect().get();
            UaSubscription subscription = client.getSubscriptionManager().createSubscription(requestedPublishingInterval).get();
            client.getSubscriptionManager().addSubscriptionListener(new UaSubscriptionManager.SubscriptionListener() {
                @Override
                public void onPublishFailure(UaException exception) {
                    // 更新连接状态
                    if (exception != null) {
                        updateClientStatus(client, false);
                    }
                    UaSubscriptionManager.SubscriptionListener.super.onPublishFailure(exception);
                }

                @Override
                public void onSubscriptionTransferFailed(UaSubscription subscription, StatusCode statusCode) {
                    UaSubscriptionManager.SubscriptionListener.super.onSubscriptionTransferFailed(subscription, statusCode);

                    if (!failedSubscriptionSet.contains(subscription)) {
                        subscribe(client, opcTagList, onItemCreated);
                        failedSubscriptionSet.add(subscription);
                    }
                }
            });

            // 创建订阅的变量
            List<ReadValueId> readValueIdList = CollUtil.newArrayList();
            for (OpcTag opcTag : opcTagList) {
                NodeId nodeId = new NodeId(2, opcTag.getItemId());
                ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                readValueIdList.add(readValueId);
            }

            List<MonitoredItemCreateRequest> requestList = CollUtil.newArrayList();
            for (ReadValueId readValueId : readValueIdList) {
                // 创建监控的参数
                UInteger clientHandle = subscription.nextClientHandle();
                MonitoringParameters parameters = new MonitoringParameters(clientHandle, samplingInterval, null, uint(queueSize), true);

                // 创建监控项请求
                // 该请求最后用于创建订阅。
                MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                requestList.add(request);
            }

            // 创建监控项，并且注册变量值改变时候的回调函数。
            List<UaMonitoredItem> itemList = subscription.createMonitoredItems(TimestampsToReturn.Both, requestList, onItemCreated).get();
            uaMonitoredItemList.addAll(itemList);

            for (UaMonitoredItem item : itemList) {
                if (item.getStatusCode().isGood()) {
                    // 更新连接状态
                    updateClientStatus(client, true);
                    log.info("item created for nodeId={}", item.getReadValueId().getNodeId());
                } else {
                    log.warn("failed to create item for nodeId={} (status={})", item.getReadValueId().getNodeId(), item.getStatusCode());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void updateClientStatus(OpcUaClient client, boolean status) {
        if (client == null) {
            return;
        }

        Boolean oldStatus = this.clientStatusMap.get(client);
        oldStatus = oldStatus != null ? oldStatus : false;
        if (Boolean.compare(oldStatus, status) != 0) {
            this.clientStatusMap.put(client, status);
            // TODO WebSocket推送状态变化消息
        }
    }

}
