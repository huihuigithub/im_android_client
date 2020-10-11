package com.znh.im.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.timeout.IdleStateHandler

/**
 * netty服务端
 */
object ServerMain {

    @JvmStatic
    fun main(args: Array<String>) {

        //服务类
        val bootstrap = ServerBootstrap()

        //boss和worker
        val boss: EventLoopGroup = NioEventLoopGroup()
        val worker: EventLoopGroup = NioEventLoopGroup()
        try {
            //设置线程池
            bootstrap.group(boss, worker)

            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel::class.java)

            //设置管道工厂
            bootstrap.childHandler(object : ChannelInitializer<Channel?>() {
                override fun initChannel(channel: Channel?) {
                    val pipeline = channel?.pipeline()
                    // 添加读写空闲时间限制
                    pipeline?.addLast(IdleStateHandler(30, 0, 0))
                    channel?.pipeline()?.addLast(RequestEncoder())
                    channel?.pipeline()?.addLast(ResponseDecoder())
                    channel?.pipeline()?.addLast(ServerHandler())
                }
            })

            //监听客户端连接
            val future = bootstrap.bind(10101)
            println("start")
            future.channel().closeFuture().sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //释放资源
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }
}