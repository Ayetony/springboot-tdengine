package com.dto;

/**
 * @program: springboot-tdengine
 * @ClassName SendSocketDTO
 * @author: ayetony miao
 * @create: 2023-08-07 21:58
 **/
import lombok.Data;

/**
 * websocket发送实体类
 */
@Data
public class SendSocketDTO {
    /**
     * 工序任务id
     */
    private Long operationTaskId;

    /**
     * 起始设备id
     */
    private Long startEquipmentId;

    /**
     * 目标设备id
     */
    private Long targetEquipmentId;

    /**
     *状态
     */
    private Integer state;

    /**
     *状态描述
     */
    private String stateDescription;

    /**
     * 运输内容类别  0-空托盘，1-零件，2-子板，3-夹具
     */
    private Integer transportContentCategory;
}
