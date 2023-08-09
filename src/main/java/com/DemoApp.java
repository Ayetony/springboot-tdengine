package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CuiXi
 * @version 1.0
 * @Description:
 * @date 2021/3/11 14:15
 */
@SpringBootApplication
@MapperScan("com.mapper")
public class DemoApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoApp.class,args);
    }
}
