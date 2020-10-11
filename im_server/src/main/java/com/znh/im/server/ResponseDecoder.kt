package com.znh.im.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ResponseDecoder : ByteToMessageDecoder() {

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, list: MutableList<Any>) {
        if (buffer.readableBytes() >= 16) {
            // 标记初始读游标位置
            buffer.markReaderIndex()

            // 包长度
            val packageLength = buffer.readInt()

            // 头长度
            val headerLength = buffer.readShort()

            // 协议版本
            val protocolVersion = buffer.readShort()

            // 操作类型
            val operation = buffer.readInt()

            // 序列Id
            val sequenceId = buffer.readInt()

            // 读取数据长度
            val length = packageLength - 16

            // 数据包还没到齐,等待剩下的数据包到来
            if (buffer.readableBytes() < length) {
                buffer.resetReaderIndex()
                return
            }

            // 读数据部分
            val data = ByteArray(length)
            buffer.readBytes(data)

            // 解析出消息对象，继续往下面的handler传递
            val response = ResponseModel()
            response.packageLength = packageLength
            response.headerLength = headerLength
            response.protocolVersion = protocolVersion
            response.operation = operation
            response.sequenceId = sequenceId
            response.data = data
            list.add(response)
        }
    }
}