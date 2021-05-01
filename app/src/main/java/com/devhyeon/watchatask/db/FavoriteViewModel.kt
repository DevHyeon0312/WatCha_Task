package com.devhyeon.watchatask.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devhyeon.watchatask.db.favorite.FavoriteDatabase
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel constructor(private val favoriteDatabase: FavoriteDatabase) : ViewModel() {
    private val _trackAllData = MutableLiveData<Status<List<ITunesTrack>>>()
    val trackAllData : LiveData<Status<List<ITunesTrack>>> get() = _trackAllData

    private val _trackInsertData = MutableLiveData<Status<Boolean>>()
    val trackInsertData : LiveData<Status<Boolean>> get() = _trackInsertData

    private val _trackRemoveData = MutableLiveData<Status<Boolean>>()
    val trackRemoveData : LiveData<Status<Boolean>> get() = _trackRemoveData

    private val _trackFindData = MutableLiveData<Status<ITunesTrack>>()
    val trackFindData : LiveData<Status<ITunesTrack>> get() = _trackFindData

    private val _trackChangeData = MutableLiveData<Status<List<ITunesTrack>>>()
    val trackChangeData : LiveData<Status<List<ITunesTrack>>> get() = _trackChangeData

    /** DataBase 에 저장된 모든 데이터를 가져오는 메소드 */
    fun getAll() {
        CoroutineScope(Dispatchers.IO).launch {
            var tracks : List<ITunesTrack>? = null
            runCatching {
                _trackAllData.postValue(Status.Run())
                tracks = favoriteDatabase.favoriteDto().getAll()
            }.onSuccess {
                _trackAllData.postValue(Status.Success(tracks!!))
            }.onFailure {
                _trackAllData.postValue(Status.Failure(-3,it.message!!))
            }
        }
    }

    /** DataBase 에 아이템을 추가하는 메소드 */
    fun addItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                _trackInsertData.postValue(Status.Run())
                favoriteDatabase.favoriteDto().insertAll(iTunesTrack)
            }.onSuccess {
                _trackInsertData.postValue(Status.Success(true))
            }.onFailure {
                _trackInsertData.postValue(Status.Failure(-3,it.message!!))
            }
        }
    }

    /** DataBAse 에 아이템을 제거하는 메소드 */
    fun removeItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                _trackRemoveData.postValue(Status.Run())
                favoriteDatabase.favoriteDto().delete(iTunesTrack)
            }.onSuccess {
                _trackRemoveData.postValue(Status.Success(true))
            }.onFailure {
                _trackRemoveData.postValue(Status.Failure(-3,it.message!!))
            }
        }
    }
}