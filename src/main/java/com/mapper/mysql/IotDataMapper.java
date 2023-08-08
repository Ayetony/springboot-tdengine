package com.mapper.mysql;

import com.entity.IotData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IotDataMapper {
    List<IotData> selectAllIotData();
}
