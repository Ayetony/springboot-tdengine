package com.mapper.mysql;

import com.entity.OpcTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: springboot-tdengine
 * @ClassName OpcTagMapper
 * @author: ayetony miao
 * @create: 2023-08-07 22:47
 **/
@Component
public interface OpcTagMapper {

    List<OpcTag> selectList(@Param("opcSererId") Long opcServerId, @Param("nodeType") Integer nodeType);

}
