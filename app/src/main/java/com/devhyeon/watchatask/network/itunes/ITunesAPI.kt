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

    /**
     *  @param(term)    : 검색어
     *  @param(entity)  : 원하는 결과종류
     *  @param(limit)   : 조회당 개수
     *  @param(offset)  : 시작지점에서 떨어진 개수 (ofsset이 0부터 시작하면, 다음 offset = offset + limit + 1  )
     *  @sample loadSearchDataPagination("greenday","song",30,0)
     * */
    suspend fun loadSearchDataPagination(term:String, entity: String, limit: Long, offset: Long) : ITunesResponse {
        return iTunesRepository.getSearchTrackPage(term, entity, limit, offset)
    }
}