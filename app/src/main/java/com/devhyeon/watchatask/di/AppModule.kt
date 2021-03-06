package com.devhyeon.watchatask.di

import android.content.Context
import com.devhyeon.watchatask.db.favorite.FavoriteDatabase
import com.devhyeon.watchatask.viewModel.BottomNavigationViewModel
import com.devhyeon.watchatask.viewModel.TrackListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel {
        BottomNavigationViewModel()
    }

    single { createDataBase(get()) }

    single { TrackListViewModel(get(), get()) }
}

fun createDataBase(context: Context) : FavoriteDatabase? {
    return FavoriteDatabase.getInstance(context)
}