package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyeon.watchatask.constant.TIME_OUT
import com.devhyeon.watchatask.constant.VIEW_STATUS_FAIL
import com.devhyeon.watchatask.constant.VIEW_STATUS_RUN
import com.devhyeon.watchatask.constant.VIEW_STATUS_SUCCESS
import com.devhyeon.watchatask.databinding.FragmentTracklistBinding
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.OnToggleClickListener
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.ui.fragments.base.BaseFragment
import com.devhyeon.watchatask.utils.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.HashMap

/**
 * 검색조회를 보여주는 Fragment
 * 1. 검색 API run
 * 2. DB 에서 즐겨찾기된 아이템 get
 * 3. 결과 출력
 * 4. 즐겨찾기 클릭에 따른 동작이벤트
 * */
class SearchFragment : BaseFragment() , OnToggleClickListener {
    companion object {
        private val TAG = SearchFragment::class.java.name
    }

    //바인딩
    private var _binding: FragmentTracklistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private val iTunesViewModel: ITunesViewModel by viewModel()

    //어댑터
    private var mAdapter: TrackListAdapter? = TrackListAdapter(this)

    private val TREM  = "greenday"  //검색명(문제 조건에 따라 greenday 고정)
    private val ENTRY = "song"      //종류(문제 조건에 따라 song 고정)
    private val LIMIT: Long = 50    //한번 조회할 때 가져올 개수
    private var OFFSET: Long = 0   //시작위치
    private val SCROLL_TOP_DOWN = 1 //스크롤 방향
    private var isScrolled = false  //스크롤여부


    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        _binding = FragmentTracklistBinding.inflate(inflater, container, false)
    }

    override fun getViewRoot(): View {
        return binding.root
    }

    override fun init() {
        binding.rvTrackList.adapter = mAdapter
        clearData()
    }

    /** 데이터 초기화 */
    private fun clearData() {
        mAdapter?.mPostList!!.clear()
        OFFSET = 0
    }

    /** 등록해야 하는 리스너 */
    override fun addListener() {
        //토글버튼 클릭 리스너
        mAdapter!!.setOnToggleClickListener(this)

        //스크롤 리스너
        binding.rvTrackList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //마지막으로 보여준 아이템 위치
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                //아이템 총 개수
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 중간을 넘어간 경우
                if (!binding.rvTrackList.canScrollVertically(SCROLL_TOP_DOWN) && lastVisibleItemPosition == itemTotalCount) {
                    isScrolled = true
                    loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
                }
            }
        })

        //새로고침 클릭 이벤트
        binding.btnRefresh.setOnClickListener {
            clearData()
            loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
        }
    }

    /** 등록해야 하는 옵저버 */
    override fun addObserver() {
        iTunesObserve()
        favoriteObserve()
    }

    /** Resume 상태에 진입하면, 데이터 수신 */
    override fun onResume() {
        super.onResume()
        loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
    }

    /** View 가 제거될 때 함께 제거 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        iTunesViewModel.trackResponse.removeObservers(viewLifecycleOwner)
        favoriteViewModel.trackAllData.removeObservers(viewLifecycleOwner)
    }

    /** Pagination 으로 검색 API 요청 */
    private fun loadSearchDataPagination(owner: LifecycleOwner, term: String, entity: String, limit: Long, offset: Long) {
        iTunesViewModel.loadSearchDataPagination(owner,term, entity,limit,offset)
        OFFSET+=(LIMIT+1)
    }

    /** 기본 검색 API 요청 */
    private fun loadSearchData(owner: LifecycleOwner, term: String, entity: String) {
        iTunesViewModel.loadSearchData(owner, term, entity)
    }

    /** DB에 저장되어 있는 항목 */
    var favoriteMap = HashMap<Long, ITunesTrack>()
    private fun createFavoriteMap(list : List<ITunesTrack>) {
        favoriteMap.clear()
        for (track : ITunesTrack in list) {
            favoriteMap.put(track.trackId,track)
        }
    }

    /** 즐겨찾기한 아이템인지 확인 */
    private fun isFavoriteItem(track : ITunesTrack) : Boolean {
        return favoriteMap.containsKey(track.trackId)
    }

    /** API 응답결과 아이템 추가 */
    private fun addApiItem(list : List<ITunesTrack>?) {
        if(list != null) {
            //즐겨찾기 DB에 있는지 판단하여 구분하여 addItem
            for (track : ITunesTrack in list) {
                if(isFavoriteItem(track)) {
                    addFavoriteItem(track)
                } else {
                    addOriginalItem(track)
                }
            }
        }
    }
    /** 즐겨찾기한 아이템 추가 */
    private fun addFavoriteItem(track : ITunesTrack) {
        track.favorit = true
        mAdapter?.addItem(track)
    }
    /** 즐겨찾기하지 않은 아이템 추가 */
    private fun addOriginalItem(track : ITunesTrack) {
        track.favorit = false
        mAdapter?.addItem(track)
    }

    /** DB 결과 옵저버 */
    private fun favoriteObserve() {
        var isRunState = false
        //DB getAll 쿼리문 사용에 따른 Run - Success - Fail
        favoriteViewModel.trackAllData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Status.Run -> {
                    isRunState = true
                    viewVisibleRun()
                    readTimeOut()
                }
                is Status.Success -> {
                    if(isRunState) {
                        //DB 조회결과에 따른 HashMap 생성
                        createFavoriteMap(it.data!!)
                        //API 결과를 삽입
                        addApiItem(apiData)

                        viewVisibleSuccess()
                    }
                }
                is Status.Failure -> {
                    DebugLog.e(TAG,"favoriteObserve()", it.errorMessage!!)
                    viewVisibleFailure()
                }
            }
        })
    }

    /** API 결과 옵저버 */
    var apiData : List<ITunesTrack>? = null
    private fun iTunesObserve() {
        var isRunState = false
        //API 요청,응답 에 따라 Run - Success - Fail
        iTunesViewModel.trackResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Status.Run -> {
                    isRunState = true
                    viewVisibleRun()
                    readTimeOut()
                }
                is Status.Success -> {
                    if(isRunState) {
                        //API 결과 저장
                        apiData = it.data!!.results
                        //DB 조회 시작
                        favoriteViewModel.getAll()
                    }
                }
                is Status.Failure -> {
                    DebugLog.e(TAG,"iTunesObserve()", it.errorMessage!!)
                    viewVisibleFailure()
                }
            }
        })
    }


    /** RUN 상태 일때 보여주는 View */
    private fun viewVisibleRun() {
        viewStatus = VIEW_STATUS_RUN
        if(!isScrolled) {
            viewVisibleIsNotScroll()
        } else {
            viewVisibleIsScroll()
        }
    }
    /** RUN 상태 스크롤인 경우에 보여주는 View */
    private fun viewVisibleIsScroll() {
        binding.rvTrackLoader.toVisible()
    }
    /** RUN 상태 스크롤이 아닌 경우에 보여주는 View */
    private fun viewVisibleIsNotScroll() {
        binding.loaderView.toVisible()
        binding.contentsView.toGone()
        binding.errorView.toGone()
    }

    /** SUCCESS 상태 일때 보여주는 View */
    private fun viewVisibleSuccess() {
        viewStatus = VIEW_STATUS_SUCCESS
        binding.loaderView.toGone()
        binding.contentsView.toVisible()
        binding.errorView.toGone()
        binding.rvTrackLoader.toGone()
    }

    /** FAIL 상태 일때 보여주는 View */
    private fun viewVisibleFailure() {
        viewStatus = VIEW_STATUS_FAIL
        binding.loaderView.toGone()
        binding.contentsView.toGone()
        binding.errorView.toVisible()
        binding.rvTrackLoader.toGone()
    }

    /** 토클버튼 클릭 리스너 */
    override fun onToggleClick(v: View?, track: ITunesTrack) {
        if (track.favorit ) {
            favoriteViewModel.addItem(track)
        } else {
            favoriteViewModel.removeItem(track)
        }
    }

    /** 동시성이슈로 혹시나 RUN 상태에서 진행이 멈춘경우에 체크하기 위함 */
    var viewStatus = VIEW_STATUS_SUCCESS
    private fun readTimeOut() {
        viewLifecycleOwner.lifecycle.let {
            //TIME_OUT 이후에 동작
            Handler().postDelayed({
                //view 가 null 이 아닐 때 Fragment LifeCycle 이 RESUMED 상태일 때, viewStatus 가 RUN 상태를 유지하고 있다면,
                if(!isNullView(view) && isViewStateResume(viewLifecycleOwner.lifecycle.currentState) && isViewStatusRun(viewStatus)) {
                    viewVisibleFailure()
                }
            }, TIME_OUT)
        }
    }
}