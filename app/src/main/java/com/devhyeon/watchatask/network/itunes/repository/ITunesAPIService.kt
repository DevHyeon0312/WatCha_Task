package com.devhyeon.watchatask.network.itunes

import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 지원하는 API 서비스
 * */
interface ITunesAPIService {
    @GET("/search")
    suspend fun getSearchTrack(
        @Query("term") term:String,
        @Query("entity") entity: String
    ): ITunesResponse
}