package com.devhyeon.watchatask.network.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * @param(resultCount) : Long        : 결과 개수
 * @param(results)     : List<Track> : 결과 데이터
 * */

data class ITunesResponse(
    @SerializedName("resultCount")  val resultCount : Long,
    @SerializedName("results") var results     : List<ITunesTrack>
)