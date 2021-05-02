package com.devhyeon.watchatask.ui.adapters

import android.view.View
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack

interface OnToggleClickListener {
    fun onToggleClick(v: View?, track: ITunesTrack)
}