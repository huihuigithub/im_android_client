package com.znh.im.client.event

/**
 * Created by znh on 2020/9/29
 *
 * IM消息事件
 */
class IMMsgEvent(//事件类型
    var eventType: String
) {

    //响应结果
    var msg: String? = null

    //异常信息
    var throwable: Throwable? = null

    companion object {
        //IM连接成功
        const val IM_CONNECTION_SUCCESS_TYPE = "im_connection_success"

        //IM认证成功
        const val IM_AUTH_SUCCESS_TYPE = "im_auth_success"

        //接收心跳包
        const val IM_HEARTBEAT_RECEIVE_TYPE = "im_heartbeat_receive"

        //接收消息
        const val IM_MESSAGE_RECEIVE_TYPE = "im_message_receive_type"

        //IM断开连接
        const val IM_DIS_CONNECTION_TYPE = "im_dis_connection_type"

        //IM出现异常
        const val IM_EXCEPTION_TYPE = "im_exception_type"

        //客户端读超时
        const val IM_READ_IDLE = "IM_READ_IDLE"

        //重连
        const val IM_RESTART_CON = "IM_RESTART_CON"
    }
}