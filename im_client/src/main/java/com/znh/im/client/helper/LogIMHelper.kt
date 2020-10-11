package com.znh.im.client.helper

import android.util.Log
import com.znh.im.client.constance.IMConstance

/**
 * Created by znh on 2020/9/29
 *
 * 打印log控制
 */
object LogIMHelper {
    fun e(key: String, value: String) {
        if (IMConstance.LOG_ENABLE) {
            Log.e(key, value)
        }
    }
}