package com.devhyeon.watchatask.network.itunes.repository

import com.devhyeon.watchatask.network.itunes.data.ITunesResponse

interface ITunesRepository {
    suspend fun getSearchTrack(term:String, entity: String) : ITunesResponse

    suspend fun getSearchTrackPage(term:String, entity: String, limit: Long, offset: Long) : ITunesResponse
}