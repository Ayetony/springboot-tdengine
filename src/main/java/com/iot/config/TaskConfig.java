package com.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @program: springboot-tdengine
 * @ClassName TaskConfig
 * @author: ayetony miao
 * @create: 2023-08-09 11:46
 **/
@Configuration
public class TaskConfig {

    @Bean
    public TaskExecutor otherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

}
