package com.devhyeon.watchatask.network.itunes

import com.devhyeon.watchatask.network.itunes.repository.ITunesRepository
import com.devhyeon.watchatask.network.itunes.data.ITunesResponse

class ITunesAPI(private val iTunesRepository: ITunesRepository) {

    /**
     *  @param(term)    : 검색어
     *  @param(entity)  : 원하는 결과종류
     *  @sample loadSearchData("greenday","song")
     * */
    suspend fun loadSearchData(term:String, entity: String) : ITunesResponse {
        return iTunesRepository.getSearchTrack(term, entity)
    }

}