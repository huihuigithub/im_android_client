package com.znh.im.server

class ResponseModel {

    //包长度
    var packageLength = 0

    //头长度
    var headerLength: Short = 0

    //协议版本
    var protocolVersion: Short = 0

    //操作类型
    var operation = 0

    //序列Id
    var sequenceId = 0

    //数据内容
    var data: ByteArray? = null

    /**
     * 将byte数组转换为字符串并返回
     */
    fun getDataStr(): String? {
        return data?.let { String(it) } ?: ""
    }
}