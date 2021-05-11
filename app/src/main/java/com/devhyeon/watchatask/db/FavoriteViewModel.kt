package com.devhyeon.watchatask.db

import androidx.lifecycle.*
import com.devhyeon.watchatask.constant.DB_ERROR
import com.devhyeon.watchatask.db.favorite.FavoriteDatabase
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel constructor(private val favoriteDatabase: FavoriteDatabase) : ViewModel() {
    //DB 아이템 조회에 따른 상태 및 결과
    private val _trackAllData = MutableLiveData<Status<List<ITunesTrack>>>()
    val trackAllData : LiveData<Status<List<ITunesTrack>>> get() = _trackAllData

    //DB 아이템 삽입에 따른 상태 및 결과
    private val _trackInsertData = MutableLiveData<Status<Boolean>>()
    val trackInsertData : LiveData<Status<Boolean>> get() = _trackInsertData

    //DB 아이템 삭제에 따른 상태 및 결과
    private val _trackRemoveData = MutableLiveData<Status<Boolean>>()
    val trackRemoveData : LiveData<Status<Boolean>> get() = _trackRemoveData

    /** DataBase 에 저장된 모든 데이터를 가져오는 메소드 */
    fun getAll() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                _trackAllData.postValue(Status.Run())
                favoriteDatabase.favoriteDto().getAll()
            }.onSuccess {
                _trackAllData.postValue(Status.Success(it))
            }.onFailure {
                _trackAllData.postValue(Status.Failure(DB_ERROR, it.message!!))
            }
        }
    }

    /** DataBase 에 아이템을 추가하는 메소드 */
    fun addItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                _trackInsertData.postValue(Status.Run(null))
                favoriteDatabase.favoriteDto().insertAll(iTunesTrack)
            }.onSuccess {
                _trackInsertData.postValue(Status.Success(true))
            }.onFailure {
                _trackInsertData.postValue(Status.Failure(DB_ERROR, it.message!!))
            }
        }
    }

    /** DataBAse 에 아이템을 제거하는 메소드 */
    fun removeItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                _trackRemoveData.postValue(Status.Run(null))
                favoriteDatabase.favoriteDto().delete(iTunesTrack)
            }.onSuccess {
                _trackRemoveData.postValue(Status.Success(true))
            }.onFailure {
                _trackRemoveData.postValue(Status.Failure(DB_ERROR, it.message!!))
            }
        }
    }

}