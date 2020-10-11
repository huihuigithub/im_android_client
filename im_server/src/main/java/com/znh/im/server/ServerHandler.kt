package com.znh.im.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * 消息处理类
 */
class ServerHandler : SimpleChannelInboundHandler<ResponseModel?>() {

    /**
     * 接收的消息
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ResponseModel?) {
        when (msg?.operation) {
            1 -> {
                println("form_client_author_msg...${msg.data.toString()}")
                ctx?.channel()?.writeAndFlush(
                    RequestModel(
                        1,
                        "认证成功...".toByteArray()
                    )
                )
            }
            2 -> {
                //todo 双向心跳时此处需要打开
//                ctx?.channel()?.writeAndFlush(RequestModel(2, "服务端发送过来的心跳包...".toByteArray()))
//                println("form_client_heart_msg...${msg.data.toString()}")
            }
            3 -> {
                ctx?.channel()?.writeAndFlush(
                    RequestModel(
                        3,
                        "服务端回复的消息：${msg.data?.let { String(it) }}...".toByteArray()
                    )
                )
                println("form_client_content_msg...${msg.data?.let { String(it) }}")
            }
        }
    }


    /**
     * 心跳包状态监听
     */
    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext?, event: Any?) {
        if (event is IdleStateEvent && event.state() == IdleState.READER_IDLE) {
            ctx?.channel()?.close()
            println("heart_read_idle_close...")
        } else {
            super.userEventTriggered(ctx, event)
        }
    }
}