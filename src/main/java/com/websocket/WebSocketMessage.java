package com.websocket;

import lombok.Data;

/**
 * @author ShiJianxun
 * @date 2023/5/10
 * @apiNote
 */
@Data
public class WebSocketMessage {
    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    private String type;
    private Object data;
}
