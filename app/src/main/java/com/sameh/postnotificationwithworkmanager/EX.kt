package com.sameh.postnotificationwithworkmanager

import android.util.Log

fun String.toLogD(tag: String = "debuggingTag") {
    Log.d(tag, this)
}

fun String.toLogW(tag: String = "debuggingTag") {
    Log.w(tag, this)
}

fun String.toLogE(tag: String = "debuggingTag") {
    Log.e(tag, this)
}