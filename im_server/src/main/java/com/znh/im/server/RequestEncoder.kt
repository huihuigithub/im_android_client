package com.znh.im.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class RequestEncoder : MessageToByteEncoder<RequestModel>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, request: RequestModel, buffer: ByteBuf) {
        // 包长度
        buffer.writeInt(request.packageLength)

        // 头长度
        buffer.writeShort(request.headerLength.toInt())

        // 协议版本
        buffer.writeShort(request.protocolVersion.toInt())

        // 操作类型
        buffer.writeInt(request.operation)

        // 序列Id
        buffer.writeInt(request.sequenceId)

        // 数据
        if (request.getDataLength() > 0) {
            buffer.writeBytes(request.data)
        }
    }
}