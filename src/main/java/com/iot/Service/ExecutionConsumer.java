package com.iot.Service;

import com.entity.OpcServer;
import com.entity.TagValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author shijianxun
 */
@Service
@Slf4j
@Lazy(value = false)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ExecutionConsumer implements OpcConsumerAware {

    @Override
    public void init(OpcServer opcServer) {

    }

    @Override
    public void callback(TagValue tagValue) {
        log.info(tagValue.toString());
    }

}
