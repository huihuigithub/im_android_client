package com.znh.im_android_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.znh.im.client.event.IMMsgEvent
import com.znh.im.client.event.IMMsgEvent.Companion.IM_AUTH_SUCCESS_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_CONNECTION_SUCCESS_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_DIS_CONNECTION_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_EXCEPTION_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_HEARTBEAT_RECEIVE_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_MESSAGE_RECEIVE_TYPE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_READ_IDLE
import com.znh.im.client.event.IMMsgEvent.Companion.IM_RESTART_CON
import com.znh.im.client.helper.IMServiceHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    private var str = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EventBus.getDefault().register(this)

        IMServiceHelper.instance.init(application)

        tv_conn.setOnClickListener {
            str = ""
            IMServiceHelper.instance.startConnection("u_123456", "c_123456")
        }

        tv_send.setOnClickListener {
            IMServiceHelper.instance.sendMsg("hello")
        }

        tv_dis_conn.setOnClickListener {
            IMServiceHelper.instance.disConnection()
        }
    }

    @Subscribe
    fun onEvent(event: IMMsgEvent) {
        when (event.eventType) {
            IM_CONNECTION_SUCCESS_TYPE -> dealMsg("连接成功...")

            IM_AUTH_SUCCESS_TYPE -> dealMsg("认证成功...")

            IM_HEARTBEAT_RECEIVE_TYPE -> dealMsg("收到心跳包...")

            IM_MESSAGE_RECEIVE_TYPE -> event.msg?.let { dealMsg(it) }

            IM_DIS_CONNECTION_TYPE -> dealMsg("断开连接...")

            IM_EXCEPTION_TYPE -> dealMsg("捕获到异常...")

            IM_READ_IDLE -> event.msg?.let { dealMsg(it) }

            IM_RESTART_CON -> event.msg?.let { dealMsg(it) }
        }
    }

    private fun dealMsg(msg: String) {
        str = str + "\n" + msg

        runOnUiThread {
            tv_content_msg.text = str
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}