package com.iot.Service;

import com.entity.OpcServer;
import com.entity.TagValue;

/**
 * @author shijianxun
 * @date 2023/04/12 14:00
 */
public interface OpcConsumerAware {

    /**
     * 初始化
     * @param opcServer
     */
    void init(OpcServer opcServer);

    /**
     * OPC订阅的回调方法
     * @param tagValue
     */
    void callback(TagValue tagValue);
}
