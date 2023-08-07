package com.iot;

import com.DemoApp;
import com.service.MysqlService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @program: springboot-tdengine
 * @ClassName SpringbootTest
 * @author: ayetony miao
 * @create: 2023-08-07 21:11
 **/
@SpringBootTest(classes = DemoApp.class)
public class SpringbootTest {

    @Resource
    private MysqlService mysqlService;

    @Test
    public void mysqlTest(){
        System.out.println(mysqlService.queryOpcServers());
    }


}