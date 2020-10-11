package com.znh.im.client.helper

import android.app.Application
import com.znh.im.client.model.AuthModel
import io.netty.util.internal.StringUtil

/**
 * Created by znh on 2020/9/29
 *
 * IM相关操作封装
 */
class IMServiceHelper private constructor() {

    /**
     * 初始化操作，需要在Application中调用
     *
     * @param application
     */
    fun init(application: Application?) {
        ClientServiceHelper.instance.init(application)
    }

    /**
     * 开启连接
     */
    fun startConnection(uid: String?, chatId: String?) {
        if (StringUtil.isNullOrEmpty(uid) || StringUtil.isNullOrEmpty(chatId)) {
            return
        }
        val params = AuthModel(uid, chatId)
        ClientServiceHelper.instance.startConnection(params)
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    fun sendMsg(msg: String?) {
        ClientServiceHelper.instance.sendMsg(msg)
    }

    /**
     * 断开连接
     */
    fun disConnection() {
        ClientServiceHelper.instance.disConnection()
    }

    companion object {
        val instance: IMServiceHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { IMServiceHelper() }
    }
}