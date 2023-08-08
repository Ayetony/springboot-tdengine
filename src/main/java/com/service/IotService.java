package com.service;

import com.entity.IotData;
import com.entity.IotNode;
import com.entity.OpcServer;
import com.mapper.mysql.IotDataMapper;
import com.mapper.mysql.IotNodeMapper;
import com.mapper.mysql.OpcServerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(transactionManager = "mysqlTransactionManager")
public class IotService {
    @Resource
    private IotDataMapper iotDataMapper;

    @Resource
    private IotNodeMapper iotNodeMapper;

    public List<IotData> queryIotData(){
        return iotDataMapper.selectAllIotData();
    }

    public List<IotNode> queryIotNode(){
        return iotNodeMapper.selectAllIotNode();
    }

}
