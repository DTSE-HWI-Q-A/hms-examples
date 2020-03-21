package com.vsm.mymapstest.ui.location

import android.util.Log

class LogCatWrapper : LogNode {
    private var mNext: LogNode? = null

    fun getNext(): LogNode? {
        return mNext
    }

    fun setNext(node: LogNode?) {
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
            useMsg += """
                
                ${Log.getStackTraceString(tr)}
                """.trimIndent()
        }
        Log.println(priority, tag, useMsg)
        if (mNext != null) {
            mNext!!.println(priority, tag, msg, tr)
        }
    }
}