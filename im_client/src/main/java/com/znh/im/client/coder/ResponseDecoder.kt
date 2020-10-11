package com.znh.im.client.coder

import com.znh.im.client.constance.IMConstance
import com.znh.im.client.model.ResponseModel
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * Created by znh on 2020/9/29
 *
 * 响应数据解码器
 */
class ResponseDecoder : ByteToMessageDecoder() {

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, list: MutableList<Any>) {
        if (buffer.readableBytes() >= IMConstance.BASE_LENGTH) {
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
            val length = packageLength - IMConstance.BASE_LENGTH

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