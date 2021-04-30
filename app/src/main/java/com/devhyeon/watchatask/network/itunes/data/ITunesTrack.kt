package com.devhyeon.watchatask.network.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * @param(artistId)         : Long      : 아티스트의 아이디
 * @param(collectionId)     : Long      : 앨범의 아이디
 * @param(trackId)          : Long      : 트랙의 아이디
 * @param(artistName)       : String    : 아티스트의 이름
 * @param(collectionName)   : String    : 앨범의 이름
 * @param(trackName)        : String    : 트랙의 이름
 * @param(artworkUrl100)    : String    : 트랙에 이미지 URL
 * */

data class ITunesTrack(
    @SerializedName("artistId")         val artistId        : Long,
    @SerializedName("collectionId")     val collectionId    : Long,
    @SerializedName("trackId")          val trackId         : Long,
    @SerializedName("artistName")       val artistName      : String,
    @SerializedName("collectionName")   val collectionName  : String,
    @SerializedName("trackName")        val trackName       : String,
    @SerializedName("artworkUrl100")    val artworkUrl100   : String,
    var favorit : Boolean = false
)