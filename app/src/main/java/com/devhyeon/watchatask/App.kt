package com.devhyeon.watchatask

import android.app.Application
import com.devhyeon.watchatask.di.AppModule
import com.devhyeon.watchatask.di.ITunesModule
import com.devhyeon.watchatask.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    AppModule,
                    ITunesModule,
                    NetworkModule
                )
            )
        }
    }
}