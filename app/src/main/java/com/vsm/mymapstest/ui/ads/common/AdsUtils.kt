package com.vsm.mymapstest.ui.ads.common

import android.content.Context
import android.util.Log
import com.huawei.hms.ads.identifier.AdvertisingIdClient
import java.io.IOException


fun getOaid(context: Context?, callback: OaidCallback?) {

    val TAG = "OaidSdkUtil"
    if (null == context || null == callback) {
        Log.e(TAG, "invalid input param")
        return
    }
    try {
        val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
        if (null != info) {
            callback.onSuccuss(info.id, info.isLimitAdTrackingEnabled)
        } else {
            callback.onFail("oaid is null")
        }
    } catch (e: IOException) {
        Log.e(TAG, "getAdvertisingIdInfo IOException")
        callback.onFail("getAdvertisingIdInfo IOException")
    }
}
