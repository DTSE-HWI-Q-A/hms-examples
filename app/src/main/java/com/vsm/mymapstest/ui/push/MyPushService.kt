package com.vsm.mymapstest.ui.push

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.vsm.mymapstest.MyApp
import com.vsm.mymapstest.R


class MyPushService : HmsMessageService() {
    private val TAG = this::class.java.simpleName
    val Context.myApp: MyApp get() = applicationContext as MyApp

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "receive token:$token")
        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token)
        }
    }

    private fun refreshedTokenToServer(token: String) {
        Log.i(TAG, "sending token to server. token:$token")
        val sharedPreference = getSharedPreferences(
            myApp.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        var editor = sharedPreference.edit()
        editor.putString(myApp.getString(R.string.preference_token_key), token)
        editor.commit()
    }
}
