package com.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OpcValue {
    /**
     * 源时间
     */
    private Date sourceTime;
    /**
     * 服务器时间
     */
    private Date serverTime;

    /**
     * 点位值
     */
    private String tagValue;
}
