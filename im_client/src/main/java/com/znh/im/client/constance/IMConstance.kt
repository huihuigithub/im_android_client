package com.znh.im.client.constance

/**
 * Created by znh on 2020/9/29
 *
 * IM相关常量管理
 */
class IMConstance {

    companion object {
        //数据包基本长度
        const val BASE_LENGTH = 4 + 2 + 2 + 4 + 4

        //IM服务器IP地址
        const val IM_IP = "192.168.1.54"

        //IM服务器端口号
        const val IM_PORT = 10101

        //认证operation
        const val AUTHOR_OPERATION_CODE = 1

        //发送心跳operation
        const val HEARTBEAT_OPERATION_CODE = 2

        //发送消息operation
        const val SEND_MSG_OPERATION_CODE = 3

        // todo 双向心跳时此处需要设置读idle时间（秒）
        const val DEFAULT_READ_IDLE_TIME = 0

        // 设置写idle时间（秒）
        const val DEFAULT_WRITE_IDLE_TIME = 10

        // 设置读和写idle时间（秒）
        const val DEFAULT_ALL_IDLE_TIME = 0

        //log是否可用
        const val LOG_ENABLE = true
    }
}