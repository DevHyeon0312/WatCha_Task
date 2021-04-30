package com.devhyeon.watchatask.di

import com.devhyeon.watchatask.network.ITunesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel {
        ITunesViewModel(get())
    }
}