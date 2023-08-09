package com.mapper.tdengine;

import com.entity.OpcValue;
import com.entity.Weather;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface OpcValueMapper {
    int insert(OpcValue opcValue);

    int batchInsert(List<OpcValue> OpcValueList);

    List<Weather> select(@Param("limit") Long limit, @Param("offset")Long offset);

    int delByTs(Date ts);
}
