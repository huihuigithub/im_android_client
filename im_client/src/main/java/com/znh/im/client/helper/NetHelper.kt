package com.znh.im.client.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by znh on 2020/9/29
 *
 * 网络工具类
 */
class NetHelper {

    companion object {
        /**
         * 获取网络连接状态
         *
         * @param context
         * @return true:有网  false：没网
         */
        fun isNetworkAvailable(context: Context?): Boolean {
            return try {
                val manager: ConnectivityManager? =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo: NetworkInfo? = manager?.activeNetworkInfo as NetworkInfo
                netInfo?.isAvailable ?: false
            } catch (e: Exception) {
                true
            }
        }
    }
}