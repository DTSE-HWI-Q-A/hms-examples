package com.vsm.mymapstest.utils.logger

class LogCatWrapper : Log.LogNode {

    private var mNext: Log.LogNode? = null

    fun getNext(): Log.LogNode? {
        return mNext
    }

    fun setNext(node: Log.LogNode?) {
        mNext = node
    }

    override fun println(
        priority: Int,
        tag: String?,
        msg: String?,
        tr: Throwable?
    ) {
        var useMsg = msg
        if (useMsg == null) {
            useMsg = ""
        }
        if (tr != null) {
            useMsg += "\n" + android.util.Log.getStackTraceString(tr)
        }
        android.util.Log.println(priority, tag, useMsg)
        mNext?.println(priority, tag, msg, tr)
    }
}