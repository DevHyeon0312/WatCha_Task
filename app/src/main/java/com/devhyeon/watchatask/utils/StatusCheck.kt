package com.devhyeon.watchatask.utils

import android.view.View
import androidx.lifecycle.Lifecycle
import com.devhyeon.watchatask.constant.VIEW_STATUS_RUN

fun isNullView(view: View?) : Boolean {
    return (view == null)
}

fun isViewStateResume(state: Lifecycle.State) : Boolean {
    return state == Lifecycle.State.RESUMED
}

fun isViewStatusRun(status: Int) : Boolean {
    return status == VIEW_STATUS_RUN
}