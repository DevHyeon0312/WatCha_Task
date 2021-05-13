package com.devhyeon.watchatask.viewModel

import androidx.lifecycle.*
import com.devhyeon.watchatask.constant.*
import com.devhyeon.watchatask.db.favorite.FavoriteDatabase
import com.devhyeon.watchatask.network.itunes.ITunesAPI
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.utils.DebugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * API 와 DataBase 를 사용하여 View 에 필요한 Data 가공
 * */
class TrackListViewModel constructor(
    private val iTunesAPI: ITunesAPI,
    private val favoriteDatabase: FavoriteDatabase
    ) : ViewModel() {

    companion object {
        private val TAG = TrackListViewModel::class.java.name
    }

    /** LiveData */
    //API 요청에 따른 상태 및 결과
    private var _trackResponseData = MutableLiveData<List<ITunesTrack>>()
    val trackResponseData : LiveData<List<ITunesTrack>> get() = _trackResponseData

    //DB 아이템 조회에 따른 상태 및 결과
    private val _trackFavoriteData = MutableLiveData<List<ITunesTrack>>()
    val trackFavoriteData : LiveData<List<ITunesTrack>> get() = _trackFavoriteData

    //Search 요청에따른 보여줘야 하는 View 상태
    //검색과 DataBase 를 병합하는 경우에 View 를 그리기 위해 이 LiveData 를 관찰해야 합니다.
    private val _viewSearchStatusData = MutableLiveData<Int>()
    val viewSearchStatusData : LiveData<Int> get() = _viewSearchStatusData

    //DataBase 요청에따른 보여줘야 하는 View 상태
    //단순히 DataBase 만으로 View 를 그리기 위해서는 LiveData 를 관찰해야 합니다.
    private val _viewFavoriteStatusData = MutableLiveData<Int>()
    val viewFavoriteStatusData : LiveData<Int> get() = _viewFavoriteStatusData

    /** Data */
    private val favoriteMap : HashMap<Long, ITunesTrack> = HashMap<Long,ITunesTrack>()

    /** API 를 사용하여 검색 요청에 따른 응답을 페이징으로 처리하기 위한 메소드 */
    fun loadSearchData(owner: LifecycleOwner, term: String, entity: String, limit: Long, offset: Long) {
        if(_trackResponseData.value == null) {
            DebugLog.d(TAG, "_trackFavoriteData.value == null")
            loadDataBaseItem(owner, term, entity, limit, offset)
        } else {
            DebugLog.d(TAG, "_trackFavoriteData.value != null")
            loadSearchDataPagination(owner, term, entity, limit, offset)
        }
    }

    /** 즐겨찾기 데이터 가져오기 */
    fun loadFavoriteData() {
        _trackFavoriteData.postValue(favoriteMap.values.toList())
    }

    /** 즐겨찾기 아이템 추가 */
    fun addFavoriteData(iTunesTrack: ITunesTrack) {
        addDataBaseItem(iTunesTrack)
    }

    /** 즐겨찾기 아이템 제거 */
    fun removeFavoriteData(iTunesTrack: ITunesTrack) {
        removeDataBaseItem(iTunesTrack)
    }

    /** 데이터 복원을 하지 않아서 초기화를 필요로 하는 경우 */
    fun clear() {
        _trackResponseData = MutableLiveData<List<ITunesTrack>>()
    }

    /** API 를 사용하여 검색 요청에 따른 응답을 페이징으로 처리하기 위한 메소드 */
    private fun loadSearchDataPagination(owner: LifecycleOwner, term: String, entity: String, limit: Long, offset: Long) {
        owner.lifecycleScope.launch {
            runCatching {
                DebugLog.d(TAG, "loadSearchDataPagination RUN")
                if(offset == 0L) {
                    _viewSearchStatusData.value = VIEW_INIT_RUN
                } else {
                    _viewSearchStatusData.value = VIEW_PAGING_RUN
                }
                iTunesAPI.loadSearchDataPagination(term, entity, limit, offset)
            }.onSuccess {
                DebugLog.d(TAG, "loadSearchDataPagination SUCCESS" + favoriteMap.size)
                val list : MutableList<ITunesTrack> = mutableListOf()
                for (track in it.results) {
                    if(favoriteMap.contains(track.trackId)) {
                        DebugLog.d(TAG, "loadSearchDataPagination IN : " + track.trackId)
                        track.favorit = true
                    }
                    list.add(track)
                }
                _trackResponseData.value = (list) // //trackMap.values.toList()
                _viewSearchStatusData.value = VIEW_SUCCESS
            }.onFailure {
                DebugLog.d(TAG, "loadSearchDataPagination FAIL")
                _viewSearchStatusData.value = VIEW_ERROR
            }
        }
    }


    /** DataBase 에 저장된 모든 데이터를 가져오는 메소드 */
    private fun loadDataBaseItem(owner: LifecycleOwner, term: String, entity: String, limit: Long, offset: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                DebugLog.d(TAG, "loadDataBaseItem RUN")
                _viewSearchStatusData.postValue(VIEW_INIT_RUN)
                _viewFavoriteStatusData.postValue(VIEW_INIT_RUN)
                favoriteDatabase.favoriteDto().getAll()
            }.onSuccess {
                DebugLog.d(TAG, "loadDataBaseItem SUCCESS")

                for (track in it) {
                    favoriteMap[track.trackId] = track
                }
                _trackFavoriteData.postValue(favoriteMap.values.toList())

                if(favoriteMap.values.isEmpty()) {
                    _viewFavoriteStatusData.postValue(VIEW_EMPTY)
                } else {
                    _viewFavoriteStatusData.postValue(VIEW_SUCCESS)
                }

                loadSearchDataPagination(owner, term, entity, limit, offset)
            }.onFailure {
                DebugLog.d(TAG, "loadDataBaseItem FAIL")
                _viewSearchStatusData.postValue(VIEW_ERROR)
                _viewFavoriteStatusData.postValue(VIEW_ERROR)
            }
        }
    }
    /** DataBase 에 아이템을 추가하는 메소드 */
    private fun addDataBaseItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                favoriteDatabase.favoriteDto().insert(iTunesTrack)
            }.onSuccess {
                favoriteMap[iTunesTrack.trackId] = iTunesTrack
                _trackFavoriteData.postValue(favoriteMap.values.toList())
                _viewSearchStatusData.postValue(VIEW_SUCCESS)
                _viewFavoriteStatusData.postValue(VIEW_SUCCESS)
            }.onFailure {
                _viewSearchStatusData.postValue(VIEW_ERROR)
                _viewFavoriteStatusData.postValue(VIEW_ERROR)
            }
        }
    }

    /** DataBAse 에 아이템을 제거하는 메소드 */
    private fun removeDataBaseItem(iTunesTrack: ITunesTrack) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                favoriteDatabase.favoriteDto().delete(iTunesTrack)
            }.onSuccess {
                favoriteMap.remove(iTunesTrack.trackId)
                _viewSearchStatusData.postValue(VIEW_SUCCESS)
                _viewFavoriteStatusData.postValue(VIEW_SUCCESS)
            }.onFailure {
                _viewSearchStatusData.postValue(VIEW_ERROR)
                _viewFavoriteStatusData.postValue(VIEW_ERROR)
            }
        }
    }
}