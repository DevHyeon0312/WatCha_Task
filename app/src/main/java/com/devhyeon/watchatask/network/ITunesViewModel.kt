package com.devhyeon.watchatask.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.network.itunes.ITunesAPI
import kotlinx.coroutines.launch

class ITunesViewModel constructor(private val iTunesAPI: ITunesAPI) : ViewModel(){

    private val _trackResponse = MutableLiveData<ITunesResponse>()
    val trackResponse : LiveData<ITunesResponse> get() = _trackResponse

    fun loadSearchData(term: String, entity: String) {
        viewModelScope.launch {
            var result : ITunesResponse? = null
            runCatching {
                Log.d(TAG,"getSearch() RUN")
                result = iTunesAPI.loadSearchData(term, entity)
            }.onSuccess {
                println("SUCCESS")
                Log.d(TAG,"getSearch() SUCCESS")
                Log.d(TAG, result.toString())
            }.onFailure {
                Log.d(TAG,"getSearch() FAIL")
                Log.d(TAG,"${it.message}")
            }
        }
    }

    companion object {
        private val TAG = ITunesViewModel::class.java.name
    }
}