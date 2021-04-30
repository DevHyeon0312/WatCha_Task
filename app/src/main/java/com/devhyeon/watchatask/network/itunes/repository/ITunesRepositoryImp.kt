package com.devhyeon.watchatask.network.itunes.repository

import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.network.itunes.ITunesAPIService

class ITunesRepositoryImp(private val iTunesAPIService: ITunesAPIService) : ITunesRepository{
    override suspend fun getSearchTrack(term: String, entity: String): ITunesResponse {
        return iTunesAPIService.getSearchTrack(term, entity)
    }
}