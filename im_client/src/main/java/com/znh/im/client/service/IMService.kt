package com.znh.im.client.service

import android.app.IntentService
import android.content.Intent
import androidx.annotation.Nullable
import com.znh.im.client.coder.RequestEncoder
import com.znh.im.client.coder.ResponseDecoder
import com.znh.im.client.constance.IMConstance
import com.znh.im.client.helper.ClientCoderHandler
import com.znh.im.client.helper.ClientServiceHelper
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler

/**
 * Created by znh on 2020/9/29
 *
 * 运行IM长连接的服务
 */
class IMService : IntentService("IMService") {

    //是否正在运行的标志
    private var isRunning = false

    override fun onHandleIntent(@Nullable intent: Intent?) {
        if (isRunning) {
            return
        }
        isRunning = true
        startConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    /**
     * 开始连接
     */
    private fun startConnection() {
        // 创建服务类
        val bootstrap = Bootstrap()

        // 创建worker
        val worker: EventLoopGroup = NioEventLoopGroup()
        try {
            // 设置线程池
            bootstrap.group(worker)

            // 设置socket工厂
            bootstrap.channel(NioSocketChannel::class.java)

            //设置连接超时时间
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)

            // 设置管道
            bootstrap.handler(object : ChannelInitializer<Channel>() {
                @Throws(Exception::class)
                override fun initChannel(channel: Channel) {
                    val pipeline = channel.pipeline()
                    // 添加读写空闲时间限制
                    pipeline.addLast(
                        IdleStateHandler(
                            IMConstance.DEFAULT_READ_IDLE_TIME,
                            IMConstance.DEFAULT_WRITE_IDLE_TIME,
                            IMConstance.DEFAULT_ALL_IDLE_TIME
                        )
                    )
                    // 使用自定义的编解码器
                    pipeline.addLast(ResponseDecoder())
                    pipeline.addLast(RequestEncoder())
                    pipeline.addLast(ClientCoderHandler())
                }
            })

            //连接服务端
            val future: ChannelFuture =
                bootstrap.connect(IMConstance.IM_IP, IMConstance.IM_PORT).sync()
            future.channel().closeFuture().sync()
        } catch (e: Exception) {
            e.printStackTrace()
            ClientServiceHelper.instance.restartConnection()
        } finally {
            worker.shutdownGracefully()
        }
    }
}