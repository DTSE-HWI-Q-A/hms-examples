package com.vsm.mymapstest.ui.ads.common

interface OaidCallback {
    fun onSuccuss(oaid: String?, isOaidTrackLimited: Boolean)

    fun onFail(errMsg: String?)
}