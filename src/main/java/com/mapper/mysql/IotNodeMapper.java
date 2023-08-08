package com.mapper.mysql;

import com.entity.IotData;
import com.entity.IotNode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IotNodeMapper {
    List<IotNode> selectAllIotNode();
}
