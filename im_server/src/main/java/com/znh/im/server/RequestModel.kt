package com.znh.im.server

class RequestModel {

    constructor() {}

    constructor(operation: Int, data: ByteArray) {
        this.operation = operation
        this.data = data
    }

    //包长度
    var packageLength: Int
        get() = this.headerLength + getDataLength()
        set(value) {}

    //头长度
    var headerLength: Short = 16

    //协议版本
    var protocolVersion: Short = 1

    //操作类型
    var operation: Int = 0

    //序列Id
    var sequenceId: Int = 1

    //数据
    var data: ByteArray? = null

    /**
     * data数据的长度
     */
    fun getDataLength(): Int {
        return data?.size ?: 0
    }
}