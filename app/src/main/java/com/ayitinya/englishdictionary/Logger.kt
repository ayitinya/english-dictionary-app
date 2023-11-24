package com.ayitinya.englishdictionary

import android.util.Log

object AppLogger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message)
    }

    fun e(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message)
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.i(tag, message)
    }

    fun v(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.v(tag, message)
    }

    fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.w(tag, message)
    }

    fun Log.wtf(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.wtf(tag, message)
    }
}