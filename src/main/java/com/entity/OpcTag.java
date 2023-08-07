package com.entity;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.List;

/**
 * @program: springboot-tdengine
 * @ClassName OpcTag
 * @author: ayetony miao
 * @create: 2023-08-07 22:10
 **/
@Data
public class OpcTag {

    /**
     * ID
     */
    private Long id;

    /**
     * 父节点ID
     */
    private Long parentId;

    /**
     * OPC服务器ID
     */
    private Long opcServerId;

    /**
     * 标记（最后一个点的后面部分）
     */
    private String tagName;

    /**
     * 父节点标记
     */
    private String parentTagName;

    /**
     * 地址(kepware)
     */
    private String tagAddress;

    /**
     * 点ID（下发/读取的全路径值）
     */
    private String itemId;

    /**
     * 数据类型(kepware)
     */
    private Integer dataType;

    /**
     * 说明
     */
    private String description;

    /**
     * 节点类型(通道、设备、分组、变量）
     */
    private Integer nodeType;

    /**
     * 扫描周期（毫秒）
     */
    private Long scanRate;

    /**
     * 读写控制（0只读，1读写）
     */
    private Integer readWriteAccess;

    /**
     * 是否叶子节点
     */
    private Integer leafFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已删除
     */
    private String delFlag;

    private List<OpcTag> children;

    /**
     * 当前节点被遍历出时使用的url，可能是设备下直接的点，也可能是tag_group的分组
     */
    private String iterationByUrl;

    private String iterationDirection;

    public void addChildren(OpcTag childOpcTag) {
        if (getChildren() != null) {
            this.getChildren().add(childOpcTag);
        } else {
            this.setChildren(CollUtil.newArrayList(childOpcTag));
        }
    }

    public Boolean hasChild(){
        return (this.getChildren() != null && this.getChildren().size() > 0);
    }


}
