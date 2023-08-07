package com.websocket;

import com.alibaba.fastjson.JSON;
import com.constants.OpcConstants;
import com.dto.SendSocketDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ShiJianxun
 * @date 2023/5/10
 * @apiNote
 */
@Component
public class AvicWebSocketOperator {

    @Resource
    WebSocket webSocket;

    public void sendAllLogisticsMessage(Long startEquipmentId, Long targetEquipmentId, Integer Category) {
        SendSocketDTO socketDTO = new SendSocketDTO();
        //立体库起始设备不清楚，暂时设置为 0
        socketDTO.setStartEquipmentId(startEquipmentId);
        socketDTO.setTargetEquipmentId(targetEquipmentId);
        socketDTO.setTransportContentCategory(Category);
        sendAllMessage(OpcConstants.OpcMsgType.LOGISTICS, JSON.toJSON(socketDTO));
    }

    public void sendAllMessage(String type, Object message) {
        WebSocketMessage webSocketMessage = new WebSocketMessage(type, message);
        webSocket.sendAllMessage(JSON.toJSONString(webSocketMessage));
    }

    public void sendMessageByBusinessId(String businessId, String type, Object message) {
        WebSocketMessage webSocketMessage = new WebSocketMessage(type, message);
        webSocket.sendOneMessage(businessId, JSON.toJSONString(webSocketMessage));
    }
}
