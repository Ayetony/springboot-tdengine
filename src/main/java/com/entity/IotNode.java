package com.entity;

import lombok.Data;

@Data
public class IotNode {
    /**
     * Dbid
     */
    private Integer dbid;

    /**
     * version
     */
    private Integer version;

    /**
     * 数据源编号
     */
    private String dataSourceNumber;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * ip地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 启用状态
     */
    private Integer enabled;

    /**
     * 扩展字段
     */
    private String extendedFields;

    /**
     * 备注
     */
    private String note;
}
