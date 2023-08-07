
package com.iot.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.avic.mcp.iot.entity.OpcTag;
import com.avic.mcp.iot.entity.TagValue;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.BaseVariableTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * @author sjx84110@163.com
 * @date 2018/05/08 09:55
 */
@Slf4j
public class OpcUtil {

    public static OpcUaClient createClient(
            String endpointUrl,
            String username,
            String password) throws Exception {
        List<EndpointDescription> endpointList = DiscoveryClient.getEndpoints(endpointUrl).get();

        SecurityPolicy securityPolicy = SecurityPolicy.None;

        EndpointDescription endpoint = endpointList.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
                .findFirst()
                .orElseThrow(() -> new Exception("NO Desired Endpoints Returned"));

        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("Unable to Create Security Dir: " + securityTempDir);
        }
        com.avic.mcp.iot.util.KeyStoreLoader keyStoreLoader = new com.avic.mcp.iot.util.KeyStoreLoader().load(securityTempDir);
        IdentityProvider identityProvider;
        if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password)) {
            identityProvider = new UsernameProvider(username, password);
        } else {
            identityProvider = new AnonymousProvider();
        }

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("AVIC CAPDI OPC UA Client"))
                .setApplicationUri("urn:eclipse:milo:avic:client")
                .setCertificate(keyStoreLoader.getClientCertificate())
                .setKeyPair(keyStoreLoader.getClientKeyPair())
                .setEndpoint(endpoint)
                .setIdentityProvider(identityProvider)
                .setRequestTimeout(uint(10000))
                .setKeepAliveFailuresAllowed(Unsigned.uint(30_000))
                .setKeepAliveInterval(Unsigned.uint(10_000))
                .build();
        OpcUaClient client = OpcUaClient.create(config);

        log.info("OPC UA Endpoint Connected: {} [{}/{}]", endpoint.getEndpointUrl(), securityPolicy, endpoint.getSecurityMode());

        return client;
    }

    public static TagValue read(OpcUaClient client, OpcTag opcTag) {
        Map<String, TagValue> resultMap = read(client, CollUtil.newArrayList(opcTag));
        return resultMap.get(opcTag.getItemId());
    }

    public static Map<String, TagValue> read(OpcUaClient client, List<OpcTag> opcTagList) {
        if (client == null
                || CollUtil.isEmpty(opcTagList)) {
            return Maps.newLinkedHashMap();
        }

        try {
            client.connect().get();

            List<NodeId> nodeIdList = CollUtil.newArrayList();
            for (OpcTag opcTag : opcTagList) {
                NodeId nodeId = new NodeId(2, opcTag.getItemId());
                nodeIdList.add(nodeId);
            }

            CompletableFuture<List<DataValue>> future = client.readValues(0.0, TimestampsToReturn.Both, nodeIdList);
            List<DataValue> dataValueList = future.get();

            Map<String, TagValue> resultMap = Maps.newLinkedHashMap();
            for (int i = 0; i < opcTagList.size(); i++) {
                OpcTag opcTag = opcTagList.get(i);
                DataValue dataValue = dataValueList.size() > i
                        ? dataValueList.get(i)
                        : null;
                if (opcTag == null
                        || StrUtil.isBlank(opcTag.getItemId())
                        || dataValue == null) {
                    continue;
                }

                TagValue tagValue = buildOpcValue(opcTag.getTagName(), opcTag.getItemId(), dataValue);
                resultMap.put(opcTag.getItemId(), tagValue);
            }
            client.disconnect();
            return resultMap;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (client != null) {
                client.disconnect();
            }
            return Maps.newLinkedHashMap();
        }
    }

    public static boolean write(OpcUaClient client, TagValue tagValue) {
        return write(client, CollUtil.newArrayList(tagValue));
    }

    public static boolean write(OpcUaClient client, List<TagValue> opcValueList) {
        if (client == null
                || CollUtil.isEmpty(opcValueList)) {
            return false;
        }

        try {
            client.connect().get();

            List<NodeId> nodeIdList = CollUtil.newArrayList();
            List<DataValue> dataValueList = CollUtil.newArrayList();
            for (TagValue tagValue : opcValueList) {
                String itemId = tagValue.getItemId();
                if (itemId == null) {
                    continue;
                }

                NodeId nodeId = new NodeId(2, itemId);
                nodeIdList.add(nodeId);

                DateTime time = tagValue.getSourceTime() != null
                        ? new DateTime(tagValue.getSourceTime().getTime())
                        : null;

                UaNode node = client.getAddressSpace().getNode(nodeId);
                Object newValue = convertOpcValue(node, tagValue.getValue());

                if (newValue == null) {
                    return false;
                }
                Variant variant = new Variant(newValue);

                dataValueList.add(new DataValue(variant, null, null));
            }

            CompletableFuture<List<StatusCode>> future = client.writeValues(nodeIdList, dataValueList);
            List<StatusCode> statusCodes = future.get();

            for (int i = 0; i < statusCodes.size(); i++) {
                if (!statusCodes.get(i).isGood()) {
                    log.error("[{}]写值失败", nodeIdList.get(i));
                    client.disconnect();
                    return false;
                }
            }
            client.disconnect();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (client != null) {
                client.disconnect();
            }
            return false;
        }
    }

    public static TagValue buildOpcValue(
            String code,
            String itemId,
            DataValue dataValue) {
        if (StrUtil.isBlank(itemId)
                || dataValue == null) {
            return null;
        }

        Object valueObject = dataValue.getValue().getValue();

        // 将数据类型转换为字符串,如: [0, 0]
        if (valueObject != null) {
            if (valueObject.getClass().isArray()) {
                List<Object> valueList = CollUtil.newArrayList();
                Object[] valueObjects = new Object[Array.getLength(valueObject)];
                for (int i = 0; i < valueObjects.length; i++) {
                    valueObjects[i] = Array.get(valueObject, i);
                    valueList.add(Array.get(valueObject, i));
                }
                valueObject = valueList;
            }
        }

        TagValue tagValue = new TagValue();
        tagValue.setTagName(code);
        tagValue.setItemId(itemId);
        tagValue.setValue(dataValue.getValue() != null ? valueObject : null);
        tagValue.setSourceTime(dataValue.getSourceTime() != null ? dataValue.getSourceTime().getJavaDate() : null);
        tagValue.setServerTime(dataValue.getServerTime() != null ? dataValue.getServerTime().getJavaDate() : null);
        tagValue.setRemark(dataValue.getStatusCode() != null && !dataValue.getStatusCode().isGood() ? dataValue.getStatusCode().toString() : null);

        return tagValue;
    }

    /**
     * 根据点位的类型转换待写入的数据
     *
     * @param node
     * @param value
     * @return
     */
    public static Object convertOpcValue(UaNode node, Object value) {
        Object newValue = null;
        try {
            NodeId dataType = ((BaseVariableTypeNode) node).getDataType();
            Class<?> backingClass = TypeUtil.getBackingClass(dataType);
            int typeId = Integer.parseInt(String.valueOf(dataType.getIdentifier().toString()));
            if (typeId == BuiltinDataType.UInt16.getTypeId() || typeId == BuiltinDataType.UInt32.getTypeId() || typeId == BuiltinDataType.UInt64.getTypeId()) {
                Method method = backingClass.getMethod("valueOf", String.class);
                newValue = method.invoke(null, String.valueOf(value));
            } else {
                Constructor<?> constructor = backingClass.getConstructor(String.class);
                newValue = constructor.newInstance(value);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return newValue;
    }

    /**
     * 比hutool多了一个空字符串按照空数组转换并返回
     *
     * @param json
     * @return
     */
    public static JSONArray convertOpcJsonArray(String json) {
        if (StrUtil.isBlank(json)) {
            return new JSONArray("[]");
        }
        try {
            return new JSONArray(json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JSONArray("[]");
        }
    }


    /**
     * Given an address resolve it to as many unique addresses or hostnames as can be found.
     *
     * @param address the address to resolve.
     * @return the addresses and hostnames that were resolved from {@code address}.
     */
    public static Set<String> getHostnames(String address) {
        return getHostnames(address, true);
    }

    /**
     * Given an address resolve it to as many unique addresses or hostnames as can be found.
     *
     * @param address         the address to resolve.
     * @param includeLoopback if {@code true} loopback addresses will be included in the returned set.
     * @return the addresses and hostnames that were resolved from {@code address}.
     */
    public static Set<String> getHostnames(String address, boolean includeLoopback) {
        Set<String> hostnames = CollUtil.newLinkedHashSet();

        try {
            InetAddress inetAddress = InetAddress.getByName(address);

            if (inetAddress.isAnyLocalAddress()) {
                try {
                    Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

                    for (NetworkInterface ni : Collections.list(nis)) {
                        Collections.list(ni.getInetAddresses()).forEach(ia -> {
                            if (ia instanceof Inet4Address) {
                                if (includeLoopback || !ia.isLoopbackAddress()) {
                                    hostnames.add(ia.getHostName());
                                    hostnames.add(ia.getHostAddress());
                                    hostnames.add(ia.getCanonicalHostName());
                                }
                            }
                        });
                    }
                } catch (SocketException e) {
                    log.warn("Failed to NetworkInterfaces for bind address: {}", address, e);
                }
            } else {
                if (includeLoopback || !inetAddress.isLoopbackAddress()) {
                    hostnames.add(inetAddress.getHostName());
                    hostnames.add(inetAddress.getHostAddress());
                    hostnames.add(inetAddress.getCanonicalHostName());
                }
            }
        } catch (UnknownHostException e) {
            log.warn("Failed to get InetAddress for bind address: {}", address, e);
        }

        return hostnames;
    }

}
