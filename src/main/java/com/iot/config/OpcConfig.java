package com.iot.config;

import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @program: springboot-tdengine
 * @ClassName OpcConfig
 * @author: ayetony miao
 * @create: 2023-08-07 08:32
 **/
@Configuration
public class OpcConfig {

    protected static final Logger log = LogManager.getLogger();

    @Value("${spring.opc-ua.url:}")
    private String url;

    public boolean isOpcUaEnabled() {
        return StrUtil.isNotBlank(this.url);
    }

    public String getOpcUaUrl() {
        return this.url;
    }
}
