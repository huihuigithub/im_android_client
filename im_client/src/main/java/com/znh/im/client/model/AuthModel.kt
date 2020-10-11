package com.znh.im.client.model

/**
 * Created by znh on 2020/9/29
 *
 * 建立长连接需要的认证信息封装
 */
class AuthModel {

    //用户id
    var uid: String? = null

    //聊天室的房间id
    var chatId: String? = null

    constructor() {}
    constructor(uid: String?, chatId: String?) {
        this.uid = uid
        this.chatId = chatId
    }

    override fun toString(): String {
        return "AuthModel(uid=$uid, chatId=$chatId)"
    }
}