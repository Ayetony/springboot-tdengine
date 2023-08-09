package com.mapper.mysql;

import com.entity.IotData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IotDataMapper {
    List<IotData> selectList(@Param("iotNodeId") Long iotNodeId, @Param("nodeType") Integer nodeType);
}
