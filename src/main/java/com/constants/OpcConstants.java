package com.constants;

/**
 * @author shijianxun
 * @date 2023/04/19 12:26
 */
public interface OpcConstants {

    interface WebSocketBusinessId{
        String OPC = "OPC";
    }

    interface OpcMsgType{
        String TAG = "TAG";
        String SERVER_STATUS = "SERVER_STATUS";
        String LOGISTICS = "LOGISTICS";
        String TAG_STATUS = "TAG_STATUS";
    }

    /**
     * nodeType 未知 -1，0-根节点Project，1-通道，2-设备，3-分组，4-点位变量
     */
    interface NodeType{
        int UNKNOWN = -1;
        // 根节点Project
        int PROJECT = 0;
        // 通道
        int CHANNEL = 1;
        // 设备
        int DEVICE = 2;
        // 分组
        int GROUP = 3;
        // 点位变量
        int TAG = 4;
    }

    /**
     * 是否叶子节点 叶子节点1，非叶子节点0
     */
    interface LEAF{
        // 叶子节点
        int YES = 1;
        // 非叶子节点
        int NO = 0;
    }

    /**
     * 遍历的动作
     *  browse_channel---浏览通道
     *  browse_device---浏览设备
     *  browse_group---浏览分组
     */
    interface BROWSE_DIRECTION{
        String CHANNEL = "browse_channel";
        String DEVICE = "browse_device";
        String GROUP = "browse_group";
        String NONE = "none";
    }

    /**
     * 遍历节点的路径后缀模板
     */
    interface URL_PATH_SUFFIX{
        String TAG = "/{0}/tags";
        String GROUP = "/{0}/tag_groups";
        String DEVICE = "{0}/devices";
    }

    /**
     * OPC 配置项
     */
    interface CONFIG{

        String OPC_USER_NAME = "Administrator";
        String OPC_PASSWORD = "";
        String OPC_COMMON_ALL_TYPES_NAME = "common.ALLTYPES_NAME";
        String OPC_COMMON_ALL_TYPES_DESCRIPTION = "common.ALLTYPES_DESCRIPTION";
        String OPC_COMMON_TAG_DATA_TYPE = "servermain.TAG_DATA_TYPE";
        String OPC_COMMON_TAG_ADDRESS = "servermain.TAG_ADDRESS";
        String OPC_COMMON_TAG_SCAN_RATE_MILLISECONDS = "servermain.TAG_SCAN_RATE_MILLISECONDS";
        String OPC_COMMON_TAG_READ_WRITE_ACCESS = "servermain.TAG_READ_WRITE_ACCESS";
        String OPC_CONFIG_CHANNEL_PATH = "/config/v1/project/channels/";
        String OPC_CONFIG_PROTOCOL = "http";
        int OPC_CONFIG_HTTP_PORT = 57412;
    }

    interface SERVER_TYPE{
        int OPC_SERVER = 0;
        int MQTT_SERVER = 1;
        int BACNET_SERVER = 2;
    }

}
