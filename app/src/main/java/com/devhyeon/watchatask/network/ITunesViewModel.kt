package com.devhyeon.watchatask.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.constant.API_ERROR
import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.network.itunes.ITunesAPI
import com.devhyeon.watchatask.utils.Status
import kotlinx.coroutines.launch

class ITunesViewModel constructor(private val iTunesAPI: ITunesAPI) : ViewModel(){

    private val _trackResponse = MutableLiveData<Status<ITunesResponse>>()
    val trackResponse : LiveData<Status<ITunesResponse>> get() = _trackResponse

    /**
     * API 를 사용하여 검색 요청에 따른 응답을 처리하는 메소드
     * */
    fun loadSearchData(term: String, entity: String) {
        viewModelScope.launch {
            var result : ITunesResponse? = null
            runCatching {
                _trackResponse.value = Status.Run()
                result = iTunesAPI.loadSearchData(term, entity)
            }.onSuccess {
                _trackResponse.value = Status.Success(result!!)
            }.onFailure {
                _trackResponse.value = Status.Failure(API_ERROR, it.message!!)
            }
        }
    }

    companion object {
        private val TAG = ITunesViewModel::class.java.name
    }
}