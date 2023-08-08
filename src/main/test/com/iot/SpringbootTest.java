package com.iot;

import com.DemoApp;
import com.service.IotService;
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

    @Resource
    private IotService iotService;


    @Test
    public void mysqlTest(){
        System.out.println(mysqlService.queryOpcServers());
    }

    @Test
    public void mysqlTest1(){
        System.out.println(iotService.queryIotData());
    }

    @Test
    public void mysqlTest2(){
        System.out.println(iotService.queryIotNode());
    }
}
