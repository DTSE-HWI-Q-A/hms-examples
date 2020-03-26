package com.vsm.mymapstest.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId


fun getToken(context: Context): String {
    val TAG = "getToken"
    Log.i(TAG, "get token: begin")
    var mToken = ""
    // get token
    object : Thread() {
        override fun run() {
            Log.i(TAG, "get token: run")
            try {
                val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                Log.i(TAG, "appId: $appId")
                var pushtoken = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
                if (!TextUtils.isEmpty(pushtoken)) {
                    Log.i(TAG, "get token:$pushtoken")
                    Toast.makeText(context, "get token:$pushtoken", Toast.LENGTH_LONG).show()
                    mToken = pushtoken
                    //showLog(pushtoken)
                }
            } catch (e: Exception) {
                Log.i(TAG, "getToken failed, $e")
            }
        }
    }.start()
    return mToken
}


