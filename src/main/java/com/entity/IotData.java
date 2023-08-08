package com.entity;

import lombok.Data;

@Data
public class IotData {

    /**
     * dbid
     */
    private Integer dbid;

    /**
     * 乐观锁版本
     */
    private Integer version;

    /**
     * 点位编号
     */
    private String pointNumber;

    /**
     * 点位名称
     */
    private String pointName;

    /**
     * 点位数据采集唯一标签名
     */
    private String tagName;

    /**
     * 所属点位类型
     */
    private Integer pointType;

    /**
     * 所属硬件资源
     */
    private Integer hardwareResource;

    /**
     * 所属数据源
     */
    private Integer dataSource;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 计量单位
     */
    private String unc;

    /**
     * 启用状态
     */
    private Integer enabled;

    /**
     * 读写权限
     */
    private Integer readWriteAccess;

    /**
     * 扩展字段
     */
    private String extendedFields;

    /**
     * 备注
     */
    private String note;
}
