package com.devhyeon.watchatask.network.itunes

import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 지원하는 API 서비스
 * */
interface ITunesAPIService {

    /**
     *  @param(term)    : 검색어
     *  @param(entity)  : 원하는 결과종류
     *  @sample search?term=greenday&entity=song
     *  */
    @GET("/search")
    suspend fun getSearchTrack(
        @Query("term")   term       : String,
        @Query("entity") entity     : String
    ): ITunesResponse


    /**
     *  @param(term)    : 검색어
     *  @param(entity)  : 원하는 결과종류
     *  @param(limit)   : 조회당 개수
     *  @param(offset)  : 조회당 개수로 나눌 때 해당 페이지
     *  @sample search?term=greenday&entity=song&limit=5&offset=0
     * */
    @GET("/search")
    suspend fun getSearchTrackPage(
        @Query("term")      term    : String,
        @Query("entity")    entity  : String,
        @Query("limit")     limit   : Long,
        @Query("offset")    offset  : Long,
    ): ITunesResponse
}