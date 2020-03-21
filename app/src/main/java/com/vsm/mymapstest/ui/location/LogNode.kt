package com.vsm.mymapstest.ui.location

interface LogNode {
    fun println(
        priority: Int,
        tag: String?,
        msg: String?,
        tr: Throwable?
    )
}