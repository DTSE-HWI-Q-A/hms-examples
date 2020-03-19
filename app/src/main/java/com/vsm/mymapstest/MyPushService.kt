package com.vsm.mymapstest

import android.util.Log
import com.huawei.hms.push.HmsMessageService

class MyPushService : HmsMessageService() {
    private val TAG = this::class.java.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "receive token:$token")
    }
}