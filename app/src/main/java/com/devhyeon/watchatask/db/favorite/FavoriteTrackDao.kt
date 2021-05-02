package com.devhyeon.watchatask.db.favorite

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack

/**
@ColumnInfo(name = "artistId")         val artistId        : Long,
@ColumnInfo(name = "collectionId")     val collectionId    : Long,
@PrimaryKey
@ColumnInfo(name = "trackId")          val trackId         : Long,
@ColumnInfo(name = "artistName")       val artistName      : String,
@ColumnInfo(name = "collectionName")   val collectionName  : String,
@ColumnInfo(name = "trackName")        val trackName       : String,
@ColumnInfo(name = "artworkUrl100")    val artworkUrl100   : String,
@ColumnInfo(name = "favorite")          var favorite         : Boolean = false
 * */

@Dao
interface FavoriteTrackDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): List<ITunesTrack>

    @Query("SELECT * FROM favorite WHERE trackId IS :id")
    fun loadById(id: Long) : ITunesTrack

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg tracks: ITunesTrack)

    @Delete
    fun delete(track: ITunesTrack)
}