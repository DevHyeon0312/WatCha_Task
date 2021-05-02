package com.devhyeon.watchatask.utils

import android.util.Log
import com.devhyeon.watchatask.BuildConfig

object DebugLog {
    fun d(TAG:String?, message:String) {
        if(BuildConfig.DEBUG) {
            Log.d("$TAG -> ", message)
        }
    }
    fun e(TAG:String?, message:String) {
        if(BuildConfig.DEBUG) {
            Log.e("$TAG -> ", message)
        }
    }
    fun d(TAG:String?, funcName:String?, message:String) {
        if(BuildConfig.DEBUG) {
            Log.d("$TAG [$funcName] -> ", message)
        }
    }
    fun e(TAG:String?, funcName:String?, message:String) {
        if(BuildConfig.DEBUG) {
            Log.e("$TAG [$funcName] -> ", message)
        }
    }
}