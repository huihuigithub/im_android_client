package com.znh.im.client.model

import com.znh.im.client.constance.IMConstance

/**
 * Created by znh on 2020/9/29
 *
 * 请求数据封装类
 */
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
    var headerLength: Short = IMConstance.BASE_LENGTH.toShort()

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