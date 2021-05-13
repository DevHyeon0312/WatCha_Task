package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.databinding.FragmentTracklistBinding
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.OnToggleClickListener
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.ui.fragments.base.BaseBindingFragment
import com.devhyeon.watchatask.utils.*
import com.devhyeon.watchatask.viewModel.TrackListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * 검색조회를 보여주는 Fragment
 * 1. 검색 API run
 * 2. DB 에서 즐겨찾기된 아이템 get
 * 3. 결과 출력
 * 4. 즐겨찾기 클릭에 따른 동작이벤트
 * */
class SearchFragment : BaseBindingFragment() , OnToggleClickListener {
    companion object {
        private val TAG = SearchFragment::class.java.name
    }

    //바인딩
    private var _binding: FragmentTracklistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val trackListViewModel: TrackListViewModel by viewModel()

    //어댑터
    private var adapter: TrackListAdapter? = TrackListAdapter(this)

    //API 파라미터 값
    private val TREM  = "greenday"  //검색명(문제 조건에 따라 greenday 고정)
    private val ENTRY = "song"      //종류(문제 조건에 따라 song 고정)
    private val LIMIT: Long = 50    //한번 조회할 때 가져올 개수
    private var OFFSET: Long = 0   //시작위치

    /** binding */
    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        _binding = FragmentTracklistBinding.inflate(inflater, container, false)
    }
    override fun getViewRoot(): View {
        return binding.root
    }

    /** init */
    override fun init() {
        binding.rvTrackList.adapter = adapter
        adapter?.clear()
        OFFSET = 0
        trackListViewModel.clear()
    }


    /** View 생성이 완료되면, */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
        addObserver()
    }

    /** Resume 상태에 진입하면, 데이터 수신 */
    override fun onResume() {
        super.onResume()
        trackListViewModel.loadSearchData(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
    }

    /** View 가 제거될 때 함께 제거 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** 등록해야 하는 리스너 */
    private fun addListener() {
        //토글버튼 클릭 리스너
        adapter!!.setOnToggleClickListener(this)
    }

    /** 등록해야 하는 옵저버 */
    private fun addObserver() {
        //recyclerView update
        trackListViewModel.trackResponseData.observe(viewLifecycleOwner, Observer {
            for (item in it) {
                adapter?.addItem(item)
            }
            adapter?.scrollStateRun()
        })

        //dataBinding 으로 view status 에 따른 visible 처리
        trackListViewModel.viewSearchStatusData.observe(viewLifecycleOwner, Observer {
            binding.viewModel = trackListViewModel
        })

        //스크롤
        adapter?.scrollState?.observe(this@SearchFragment, Observer {
            when(it) {
                is Status.Run -> {}
                is Status.Success -> {
                    if(it.data!!) {
                        OFFSET += (LIMIT+1)
                        trackListViewModel.loadSearchData(viewLifecycleOwner,TREM, ENTRY,LIMIT,OFFSET)
                    }
                }
                is Status.Failure -> {}
            }
        })
    }

    /** 토클 클릭 리스너 */
    override fun onToggleClick(v: View?, track: ITunesTrack) {
        if (track.favorit) {
            trackListViewModel.addFavoriteData(track)
        } else {
            trackListViewModel.removeFavoriteData(track)
        }
    }
}