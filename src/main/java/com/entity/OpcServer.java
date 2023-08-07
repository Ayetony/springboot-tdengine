package com.entity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: springboot-tdengine
 * @ClassName OpcServer
 * @author: ayetony miao
 * @create: 2023-08-07 21:14
 **/
@Data
@EqualsAndHashCode()
public class OpcServer {

    /**
     * ID
     */
    private Long id;

    /**
     * 服务器编号
     */
    private String code;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 服务类型
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer serverType;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 主机IP
     */
    private String hostIp;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 路径
     */
    private String path;

    /**
     * 用户名
     */
    private String opcUserName;

    /**
     * 密码
     */
    private String opcPassword;

    /**
     * 启用状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer enabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已删除
     */
    private String delFlag;

    public String getUrl() {
        if (StrUtil.isNotBlank(this.getProtocol()) && StrUtil.isNotBlank(this.getHostIp())) {
            String url = StrUtil.join("",
                    this.getProtocol(),
                    "://",
                    this.getHostIp(),
                    this.getPort() != null ? ":" : "",
                    this.getPort() != null ? this.getPort() : "",
                    this.getPath() != null ? "/" : "",
                    this.getPath() != null ? this.getPath() : ""
            );
            return url;
        } else {
            return null;
        }
    }

}
