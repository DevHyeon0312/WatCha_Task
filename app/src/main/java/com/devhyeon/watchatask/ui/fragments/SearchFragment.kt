package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyeon.watchatask.databinding.FragmentTracklistBinding
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.OnToggleClickListener
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.ui.fragments.base.BaseFragment
import com.devhyeon.watchatask.utils.DebugLog
import com.devhyeon.watchatask.utils.Status
import com.devhyeon.watchatask.utils.toGone
import com.devhyeon.watchatask.utils.toVisible
import org.koin.android.viewmodel.ext.android.viewModel

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
        mAdapter?.mPostList!!.clear()
        OFFSET = 0
    }

    /** View 생성이 완료되면 옵저버, 리스너 등록 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObserver()
        addListener()
    }

    /** Resume 상태에 진입하면, 데이터 수신 */
    override fun onResume() {
        super.onResume()
        iTunesViewModel.loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
        OFFSET+=(LIMIT+1)
    }

    /** View 가 제거될 때 함께 제거 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        iTunesViewModel.trackResponse.removeObservers(viewLifecycleOwner)
        favoriteViewModel.trackAllData.removeObservers(viewLifecycleOwner)
    }

    /** 등록해야 하는 리스너 */
    private fun addListener() {
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
                    iTunesViewModel.loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
                    OFFSET+=(LIMIT+1)
                }
            }
        })

        //새로고침 클릭 이벤트
        binding.btnRefresh.setOnClickListener {
            OFFSET = 0
            iTunesViewModel.loadSearchDataPagination(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
            OFFSET+=(LIMIT+1)
        }
    }

    /** 등록해야 하는 옵저버 */
    private fun addObserver() {
        iTunesObserve()
        favoriteObserve()
    }

    /** DB에 저장되어 있는 항목 */
    var favoriteMap = HashMap<Long, ITunesTrack>()
    private fun createFavoriteMap(list : List<ITunesTrack>) {
        favoriteMap.clear()
        for (track : ITunesTrack in list) {
            favoriteMap.put(track.trackId,track)
        }
    }

    /** favoriteMap 에 해당 아이템이 있는지 확인 */
    private fun isContainsKey(track : ITunesTrack) : Boolean {
        return favoriteMap.containsKey(track.trackId)
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

    /** 즐겨찾기 안한 항목 추가 */
    private fun addApiItem(list : List<ITunesTrack>?) {
        if(list != null) {
            for (track : ITunesTrack in list) {
                if(!isContainsKey(track)) {
                    track.favorit = false
                    mAdapter?.addItem(track)
                } else {
                    track.favorit = true
                    mAdapter?.addItem(track)
                }
            }
        }
    }



    /** RUN 상태 일때 보여주는 View */
    private fun viewVisibleRun() {
        if(!isScrolled) {
            viewVisibleIsNotScroll()
        } else {
            viewVisibleIsScroll()
        }
    }
    /** 스크롤인 경우에 보여주는 View */
    private fun viewVisibleIsScroll() {
        binding.rvTrackLoader.toVisible()
    }
    /** 스크롤이 아닌 경우에 보여주는 View */
    private fun viewVisibleIsNotScroll() {
        binding.loaderView.toVisible()
        binding.contentsView.toGone()
        binding.errorView.toGone()
    }

    /** SUCCESS 상태 일때 보여주는 View */
    private fun viewVisibleSuccess() {
        binding.loaderView.toGone()
        binding.contentsView.toVisible()
        binding.errorView.toGone()
        binding.rvTrackLoader.toGone()
    }

    /** FAIL 상태 일때 보여주는 View */
    private fun viewVisibleFailure() {
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
}