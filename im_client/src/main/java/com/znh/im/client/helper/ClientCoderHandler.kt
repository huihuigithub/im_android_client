package com.znh.im.client.helper

import com.znh.im.client.constance.IMConstance
import com.znh.im.client.event.IMMsgEvent
import com.znh.im.client.model.ResponseModel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by znh on 2020/9/29
 *
 * 客户端消息状态处理，使用自定义编解码
 *
 * 心跳方案有两种(推荐方案一)
 *    方案一：单向心跳，如果客户端一定时间内没有进行写操作，就向服务端发送一个心跳包，服务端不进行回复， 一定时间内服
 *           务端没有收到客户端发送的数据包，就会清理掉这个连接。这种方式的优点是不仅可以节约流量还能有效的减轻服务
 *           端的压力，缺点是客户端不能通过心跳去监听该连接是否存活，客户端可以通过channelInactive方法的回调去监
 *           听连接的断开(在一些特殊情况下不如心跳监听可靠)
 *
 *    方案二：双向心跳，如果客户端一定时间内没有进行写操作，就向服务端发送一个心跳包，服务端要回复对应的心跳包，同样一
 *           定时间内服务端没有收到客户端发送的数据包，就会清理掉这个连接。如果客户端在一定时间内没有收到服务端的数据包
 *           也会关闭掉该连接。这种方式心跳是双向的，任何一端在自己指定的时间内没有读取到过对方的数据包就会主动关闭掉
 *           这个连接，这样可以使客户端拥有高效的连接存活监听能力，缺点是费流量，服务端压力大
 */
class ClientCoderHandler : SimpleChannelInboundHandler<ResponseModel>() {

    //记录超过idle时间没有读取到服务端数据包的次数，同时向服务端发送一个心跳包探测一下
    //如果连续超过3次，那么客户端就主动断开这个连接
    private var readIdleCount = 0

    /**
     * 接收到服务端发送的消息
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ResponseModel?) {
        readIdleCount = 0

        when (msg?.operation) {
            //认证成功消息
            IMConstance.AUTHOR_OPERATION_CODE -> ClientServiceHelper.instance.onAuthorSuccess(ctx)

            //心跳包消息
            IMConstance.HEARTBEAT_OPERATION_CODE -> ClientServiceHelper.instance.onHeartBeatReceiver(
                ctx,
                msg
            )

            //内容消息
            IMConstance.SEND_MSG_OPERATION_CODE -> ClientServiceHelper.instance.onMsgReceiver(
                ctx,
                msg
            )
        }
    }

    /**
     * 心跳包状态监听
     */
    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext?, event: Any?) {
        if (event is IdleStateEvent) {

            //方案一
            if (event.state() == IdleState.WRITER_IDLE) {
                ClientServiceHelper.instance.writeHeartBeat()
            }

            //todo 方案二(需要设置读空闲时间，IMConstance/DEFAULT_READ_IDLE_TIME = 16)
//            if (event.state() == IdleState.READER_IDLE) {
//
//                //测试用
//                val msgEvent = IMMsgEvent(IMMsgEvent.IM_READ_IDLE)
//                msgEvent.msg = "客户端读取超时第${readIdleCount + 1}次"
//                EventBus.getDefault().post(msgEvent)
//
//                readIdleCount++
//                if (readIdleCount > 3) {
//                    ctx?.channel()?.close()
//                } else {
//                    ClientServiceHelper.instance.writeHeartBeat()
//                }
//            } else if (event.state() == IdleState.WRITER_IDLE) {
//                ClientServiceHelper.instance.writeHeartBeat()
//            }
        } else {
            super.userEventTriggered(ctx, event)
        }
    }

    /**
     * 客户端连接成功
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        ClientServiceHelper.instance.onConnection(ctx)
    }

    /**
     * 客户端断开
     */
    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        ClientServiceHelper.instance.onDisConnection(ctx)
    }

    /**
     * 捕获异常
     */
    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        ClientServiceHelper.instance.onException(ctx, cause)
    }
}