package com.mapper.mysql;

import com.entity.OpcTest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author CuiXi
 * @version 1.0
 * @Description:
 * @date 2021/3/11 14:30
 */
@Component
public interface MysqlMapper {
    List<OpcTest> selectAllOpcServers();
}
