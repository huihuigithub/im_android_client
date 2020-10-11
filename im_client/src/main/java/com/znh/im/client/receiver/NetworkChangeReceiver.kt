package com.znh.im.client.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by znh on 2020/9/29
 *
 * 网络连接状态监听
 */
class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        var wifiState: NetworkInfo.State? = null
//        var mobileState: NetworkInfo.State? = null
//        val cm =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state
//        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state
//        if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
//            // 手机网络连接成功
//            //ClientServiceHelper.getInstance().restartConnection();
//        } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
//            // 无线网络连接成功
//            //ClientServiceHelper.getInstance().restartConnection();
//        }
    }
}