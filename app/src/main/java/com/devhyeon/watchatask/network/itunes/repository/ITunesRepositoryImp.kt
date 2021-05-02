package com.devhyeon.watchatask.network.itunes.repository

import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.network.itunes.ITunesAPIService

class ITunesRepositoryImp(private val iTunesAPIService: ITunesAPIService) : ITunesRepository{
    override suspend fun getSearchTrack(term: String, entity: String): ITunesResponse {
        return iTunesAPIService.getSearchTrack(term, entity)
    }

    override suspend fun getSearchTrackPage(term: String, entity: String, limit: Long, offset: Long): ITunesResponse {
        return iTunesAPIService.getSearchTrackPage(term, entity, limit, offset)
    }
}