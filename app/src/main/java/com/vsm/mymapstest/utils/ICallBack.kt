package com.vsm.mymapstest.utils

interface ICallBack {
    fun onSuccess()

    fun onSuccess(result: String?)

    fun onFailed()
}