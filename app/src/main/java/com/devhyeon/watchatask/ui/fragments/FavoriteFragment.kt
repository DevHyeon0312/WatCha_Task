package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.databinding.FragmentFavoritelistBinding
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.OnToggleClickListener
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.ui.fragments.base.BaseBindingFragment
import com.devhyeon.watchatask.viewModel.TrackListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * 즐겨찾기한 대상을 보여주는 Fragment
 * 1. DB 에서 즐겨찾기된 아이템 get
 * 2. 결과 출력
 * 3. 즐겨찾기 클릭에 따른 동작이벤트
 * */
class FavoriteFragment : BaseBindingFragment() , OnToggleClickListener {
    companion object {
        private val TAG = FavoriteFragment::class.java.name
    }

    //바인딩
    private var _binding: FragmentFavoritelistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val trackListViewModel: TrackListViewModel by viewModel()

    //어댑터
    private var adapter: TrackListAdapter? = TrackListAdapter(this)

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        _binding = FragmentFavoritelistBinding.inflate(inflater, container, false)
    }

    override fun getViewRoot(): View {
        return binding.root
    }

    override fun init() {
        binding.rvTrackList.adapter = adapter
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
        trackListViewModel.loadFavoriteData()
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
        trackListViewModel.trackFavoriteData.observe(viewLifecycleOwner, Observer {
            adapter?.setList(it)
        })

        //dataBinding 으로 view status 에 따른 visible 처리
        trackListViewModel.viewFavoriteStatusData.observe(viewLifecycleOwner, Observer {
            binding.viewModel = trackListViewModel
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