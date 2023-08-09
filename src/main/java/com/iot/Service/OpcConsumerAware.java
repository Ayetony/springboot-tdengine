package com.iot.Service;

import com.entity.IotNode;
import com.entity.TagValue;

/**
 * @author shijianxun
 * @date 2023/04/12 14:00
 */
public interface OpcConsumerAware {

    /**
     * 初始化
     * @param iotNode
     */
    void init(IotNode iotNode);

    /**
     * OPC订阅的回调方法
     * @param tagValue
     */
    void callback(TagValue tagValue);
}
