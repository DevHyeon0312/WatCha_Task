package com.devhyeon.watchatask.db.favorite

import android.content.Context
import androidx.room.*
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack

@Database(entities = arrayOf(ITunesTrack::class), version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDto(): FavoriteTrackDao

    companion object {
        private var INSTANCE: FavoriteDatabase? = null

        fun getInstance(context: Context): FavoriteDatabase? {
            if (INSTANCE == null) {
                synchronized(FavoriteDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        FavoriteDatabase::class.java, "favoriteTrack.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}