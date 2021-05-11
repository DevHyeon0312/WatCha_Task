package com.devhyeon.watchatask.network

import androidx.lifecycle.*
import com.devhyeon.watchatask.constant.API_ERROR
import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.network.itunes.ITunesAPI
import com.devhyeon.watchatask.utils.DebugLog
import com.devhyeon.watchatask.utils.Status
import kotlinx.coroutines.*

class ITunesViewModel constructor(private val iTunesAPI: ITunesAPI) : ViewModel() {
    //API 요청에 따른 상태 및 결과
    private val _trackResponse = MutableLiveData<Status<ITunesResponse>>()
    val trackResponse : LiveData<Status<ITunesResponse>> get() = _trackResponse

    /** API 를 사용하여 검색 요청에 따른 응답을 처리하는 메소드 */
    fun loadSearchData(owner: LifecycleOwner, term: String, entity: String) {
        owner.lifecycleScope.launch {
            runCatching {
                _trackResponse.value = Status.Run(null)
                iTunesAPI.loadSearchData(term, entity)
            }.onSuccess {
                _trackResponse.value = Status.Success(it)
            }.onFailure {
                _trackResponse.value = Status.Failure(API_ERROR, it.message!!)
                DebugLog.e(TAG,it.message!!)
            }
        }
    }


    /** API 를 사용하여 검색 요청에 따른 응답을 페이징으로 처리하기 위한 메소드 */
    fun loadSearchDataPagination(owner: LifecycleOwner, term: String, entity: String, limit: Long, offset: Long) {
        owner.lifecycleScope.launch {
            runCatching {
                _trackResponse.value = Status.Run(null)
                iTunesAPI.loadSearchDataPagination(term, entity, limit, offset)
            }.onSuccess {
                _trackResponse.value = Status.Success(it)
            }.onFailure {
                _trackResponse.value = Status.Failure(API_ERROR, it.message!!)
            }
        }
    }

    companion object {
        private val TAG = ITunesViewModel::class.java.name
    }
}