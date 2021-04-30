package com.devhyeon.watchatask.di

import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.network.itunes.ITunesAPI
import com.devhyeon.watchatask.network.itunes.repository.ITunesRepository
import com.devhyeon.watchatask.network.itunes.repository.ITunesRepositoryImp
import com.devhyeon.watchatask.network.itunes.ITunesAPIService
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit


/** iTunes API 관련 모듈 */
val ITunesModule = module {
    single { createITunesService(get()) }

    single { createITunesRepository(get()) }

    single { ITunesAPI(get()) }
}

/** Service 생성 */
fun createITunesService(retrofit: Retrofit): ITunesAPIService {
    return retrofit.create(ITunesAPIService::class.java)
}

/** iTunesRepository 구현 */
fun createITunesRepository(apiService: ITunesAPIService): ITunesRepository {
    return ITunesRepositoryImp(apiService)
}