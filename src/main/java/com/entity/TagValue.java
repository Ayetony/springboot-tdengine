
package com.entity;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shijianxun
 * @date 2023/04/12 14:00
 */
@Slf4j
@Data
public class TagValue extends OpcTag implements Serializable {

    /**
     * 源时间
     */
    private Date sourceTime;
    /**
     * 服务器时间
     */
    private Date serverTime;

    private Object value;
    /**
     * 备注
     */
    private String remark;


    public String asString() {
        return this.value != null
                ? String.valueOf(this.value)
                : null;
    }

    public BigDecimal asDecimal() {
        return this.value != null && NumberUtil.isNumber(asString())
                ? new BigDecimal(this.value.toString())
                : null;
    }

    public Integer asInt() {
        BigDecimal decimal = this.asDecimal();
        return decimal != null
                ? decimal.intValue()
                : null;
    }

    public Double asDouble() {
        BigDecimal decimal = this.asDecimal();
        return decimal != null
                ? decimal.doubleValue()
                : null;
    }

}
