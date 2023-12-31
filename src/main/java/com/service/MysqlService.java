package com.service;

import com.entity.OpcServer;
import com.mapper.mysql.OpcServerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: springboot-tdengine
 * @ClassName MysqlService
 * @author: ayetony miao
 * @create: 2023-08-07 21:18
 **/
@Service
@Transactional(transactionManager = "mysqlTransactionManager")
public class MysqlService {
    @Resource
    private OpcServerMapper opcServerMapper;

    public List<OpcServer> queryOpcServers(){
        return opcServerMapper.selectAllOpcServers();
    }
}
