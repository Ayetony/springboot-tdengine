package com.service;

import com.entity.OpcTest;
import com.mapper.mysql.MysqlMapper;
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
    private MysqlMapper mysqlMapper;

    public List<OpcTest> queryOpcServers(){
        return mysqlMapper.selectAllOpcServers();
    }
}
