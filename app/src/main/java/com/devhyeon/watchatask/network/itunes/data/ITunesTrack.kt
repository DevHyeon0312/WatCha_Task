package com.devhyeon.watchatask.network.itunes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "favorite")
data class ITunesTrack(
    @ColumnInfo(name = "artistId") @SerializedName("artistId")         val artistId        : Long,
    @ColumnInfo(name = "collectionId") @SerializedName("collectionId")     val collectionId    : Long,
    @PrimaryKey @SerializedName("trackId")          val trackId         : Long,
    @ColumnInfo(name = "artistName") @SerializedName("artistName")       val artistName      : String,
    @ColumnInfo(name = "collectionName") @SerializedName("collectionName")   val collectionName  : String,
    @ColumnInfo(name = "trackName") @SerializedName("trackName")        val trackName       : String,
    @ColumnInfo(name = "artworkUrl100") @SerializedName("artworkUrl100")    val artworkUrl100   : String,
    var favorit : Boolean = false
)