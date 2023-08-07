package com.iot.Service;

/**
 * @program: springboot-tdengine
 * @ClassName OpcConsumerLocator
 * @author: ayetony miao
 * @create: 2023-08-07 22:17
 **/
import com.entity.OpcServer;
import com.entity.TagValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shijianxun
 * @date 2023/04/12 14:00
 */
@Slf4j
@Component
public class OpcConsumerLocator implements ApplicationContextAware {

    private static final String METHOD_INIT = "init";
    private static final String METHOD_CALLBACK = "callback";

    /**
     * 用于保存接口实现类名及对应的类
     */
    private Map<String, OpcConsumerAware> map = new HashMap<>();

    /**
     * 根据应用上下文获取相应的接口的所有实现类
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        map = applicationContext.getBeansOfType(OpcConsumerAware.class);
    }

    /**
     * 获取所有实现类的集合
     *
     * @return
     */
    public Map<String, OpcConsumerAware> getMap() {
        return map;
    }

    /**
     * 获取对应实现类
     *
     * @param key
     * @return
     */
    public OpcConsumerAware getService(String key) {
        return map.get(key);
    }

    public void invokeInit(OpcServer opcServer) {
        this.invoke(METHOD_INIT, opcServer);
    }

    public void invokeCallback(TagValue tagValue) {
        this.invoke(METHOD_CALLBACK, tagValue);
    }

    private void invoke(String methodName, Object sourceObject) {
        Map<String, OpcConsumerAware> callbackListenerMap = this.getMap();
        callbackListenerMap.forEach((key, value) -> {
            try {
                // TODO: 2023/4/12 待删除此测试日志打印语句
                log.debug("OpcConsumerAware的实现类{}的{}方法调用", value, methodName);
                switch (methodName) {
                    case METHOD_INIT:
                        value.init((OpcServer) sourceObject);
                        break;
                    case METHOD_CALLBACK:
                        value.callback((TagValue) sourceObject);
                        break;
                    default:
                }
            } catch (Exception e) {
                log.error("OpcConsumerAware的实现类{}的{}方法调用异常[实现类={}]", value, methodName, value.getClass());
                e.printStackTrace();
            }
        });
    }

}
