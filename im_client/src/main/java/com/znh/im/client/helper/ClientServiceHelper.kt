package com.znh.im.client.helper

import android.app.Application
import android.content.Intent
import com.znh.im.client.constance.IMConstance
import com.znh.im.client.event.IMMsgEvent
import com.znh.im.client.model.AuthModel
import com.znh.im.client.model.RequestModel
import com.znh.im.client.model.ResponseModel
import com.znh.im.client.receiver.NetworkChangeReceiver
import com.znh.im.client.service.IMService
import io.netty.channel.ChannelHandlerContext
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by znh on 2020/9/29
 *
 * 处理业务层面的消息逻辑
 */
class ClientServiceHelper private constructor() {
    //网络状态监听
    private val networkBroadcast: NetworkChangeReceiver = NetworkChangeReceiver()

    //Application上下文
    private var mApplication: Application? = null

    //消息通道
    private var ctx: ChannelHandlerContext? = null

    //进入房间需要的认证信息
    var mAuthModel: AuthModel? = null

    //重连定时任务
    private var timer: Timer? = null

    //当前的重连次数
    private var count = 0

    //存储每次重连的间隔时间,单位毫秒
    private var map = HashMap<Int, Long>()

    /**
     * 单例
     */
    companion object {
        val instance: ClientServiceHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ClientServiceHelper() }
    }

    /**
     * 初始化
     *
     * 这里设置的重连策略为1s后进行第一次重连，5秒后进行第二次重连，15秒后进行第三次重连，如果连续3次还没有连接成功就放弃
     * 重连策略可以根据实际情况进行调整
     */
    init {
        map[0] = 1000
        map[1] = 5000
        map[2] = 15000
    }

    /**
     * 初始化操作，需要在Application中调用
     *
     * @param application
     */
    fun init(application: Application?) {
        mApplication = application
    }

    /**
     * 开启连接
     */
    fun startConnection(mAuthModel: AuthModel?) {
        this.mAuthModel = mAuthModel
        val intent = Intent(mApplication, IMService::class.java)
        mApplication?.startService(intent)
    }

    /**
     * 写心跳包数据
     */
    fun writeHeartBeat() {
        try {
            if (mAuthModel == null) {
                return
            }
            val requestModel = RequestModel()
            requestModel.operation = IMConstance.HEARTBEAT_OPERATION_CODE
            this.ctx?.writeAndFlush(requestModel)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    fun sendMsg(msg: String?) {
        val requestModel = RequestModel()
        requestModel.operation = IMConstance.SEND_MSG_OPERATION_CODE
        requestModel.data = msg?.toByteArray() ?: "".toByteArray()
        this.ctx?.channel()?.writeAndFlush(requestModel)
    }

    /**
     * 手动触发断开连接
     */
    fun disConnection() {
        mAuthModel = null

        this.ctx?.channel()?.close()
    }

    /**
     * 连接成功
     *
     * @param ctx
     */
    fun onConnection(ctx: ChannelHandlerContext) {
        this.ctx = ctx
        stopRestartConnection()

        val event = IMMsgEvent(IMMsgEvent.IM_CONNECTION_SUCCESS_TYPE)
        EventBus.getDefault().post(event)

        if (mAuthModel == null) {
            disConnection()
            return
        }

        //进行认证
        val requestModel = RequestModel()
        requestModel.operation = IMConstance.AUTHOR_OPERATION_CODE
        requestModel.data = "uid=${mAuthModel!!.uid},chatId=${mAuthModel!!.chatId}".toByteArray()
        this.ctx?.channel()?.writeAndFlush(requestModel)
    }

    /**
     * 认证成功
     *
     * @param ctx
     */
    fun onAuthorSuccess(ctx: ChannelHandlerContext?) {
        LogIMHelper.e("znh", "认证成功...")
        val event = IMMsgEvent(IMMsgEvent.IM_AUTH_SUCCESS_TYPE)
        EventBus.getDefault().post(event)
    }

    /**
     * 接收心跳包消息
     *
     * @param ctx
     */
    fun onHeartBeatReceiver(ctx: ChannelHandlerContext?, msg: ResponseModel?) {
        val event = IMMsgEvent(IMMsgEvent.IM_HEARTBEAT_RECEIVE_TYPE)
        event.msg = msg?.getDataStr()
        EventBus.getDefault().post(event)
    }

    /**
     * 接收服务端发送的消息
     *
     * @param ctx
     * @param response
     */
    fun onMsgReceiver(ctx: ChannelHandlerContext?, response: ResponseModel?) {
        val sendMsgEvent = IMMsgEvent(IMMsgEvent.IM_MESSAGE_RECEIVE_TYPE)
        sendMsgEvent.msg = if (response == null) "" else response.getDataStr()
        EventBus.getDefault().post(sendMsgEvent)
    }

    /**
     * 捕获异常
     *
     * @param ctx
     * @param cause
     */
    fun onException(ctx: ChannelHandlerContext?, cause: Throwable?) {
        this.ctx?.channel()?.close()
        val event = IMMsgEvent(IMMsgEvent.IM_EXCEPTION_TYPE)
        event.throwable = cause
        EventBus.getDefault().post(event)
    }

    /**
     * 断开连接
     *
     * @param ctx
     */
    fun onDisConnection(ctx: ChannelHandlerContext?) {
        this.ctx = null
        val event = IMMsgEvent(IMMsgEvent.IM_DIS_CONNECTION_TYPE)
        EventBus.getDefault().post(event)
        restartConnection()
    }

    /**
     * 需要重连
     */
    fun restartConnection() {
        //不符合重连条件
        if (!NetHelper.isNetworkAvailable(mApplication)
            || mAuthModel == null
            || count >= map.count()
        ) {
            stopRestartConnection()
            return
        }

        //重连
        stopTimer()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                count++
                startConnection(mAuthModel)
                val event = IMMsgEvent(IMMsgEvent.IM_RESTART_CON)
                event.msg = "正在进行第${count}次重连..."
                EventBus.getDefault().post(event)
            }
        }, map[count]!!)
    }

    /**
     * 取消重连
     */
    private fun stopRestartConnection() {
        stopTimer()
        count = 0
        LogIMHelper.e("znh", "取消重连任务...")
    }

    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}

