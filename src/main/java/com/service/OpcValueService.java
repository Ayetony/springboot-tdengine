package com.service;

import com.entity.OpcValue;
import com.mapper.tdengine.OpcValueMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OpcValueService {

    @Resource
    private OpcValueMapper opcValueMapper;

    public void save(OpcValue opcValue){
        opcValueMapper.insert(opcValue);
    }
}
