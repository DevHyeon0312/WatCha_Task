package com.devhyeon.watchatask.di

import android.content.Context
import com.devhyeon.watchatask.db.favorite.FavoriteDatabase
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.viewModel.BottomNavigationViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel {
        ITunesViewModel(get())
    }
    viewModel {
        BottomNavigationViewModel()
    }

    single { createDataBase(get()) }

    viewModel {
        FavoriteViewModel(get())
    }
}

fun createDataBase(context: Context) : FavoriteDatabase? {
    return FavoriteDatabase.getInstance(context)
}