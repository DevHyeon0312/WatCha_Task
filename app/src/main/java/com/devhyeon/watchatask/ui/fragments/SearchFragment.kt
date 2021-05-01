package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.databinding.FragmentTracklistBinding
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.utils.Status
import com.devhyeon.watchatask.utils.toGone
import com.devhyeon.watchatask.utils.toVisible
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    //바인딩
    private var _binding: FragmentTracklistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private val iTunesViewModel: ITunesViewModel by viewModel()

    //어댑터
    private var mAdapter: TrackListAdapter? = TrackListAdapter()

    private val TREM  = "greenday"  //검색명(문제 조건에 따라 greenday 고정)
    private val ENTRY = "song"      //종류(문제 조건에 따라 song 고정)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracklistBinding.inflate(inflater, container, false)

        binding.rvTrackList.adapter = mAdapter

        return binding.root
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
        favoriteViewModel.getAll()
    }

    /** View 가 제거될 때 함께 제거 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        iTunesViewModel.trackResponse.removeObservers(viewLifecycleOwner)
        favoriteViewModel.trackChangeData.removeObservers(viewLifecycleOwner)
    }

    /** 등록해야 하는 리스너 */
    private fun addListener() {
        //리스트 아이템 클릭 리스너
        mAdapter!!.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, track: ITunesTrack) {
                if (track.favorit) {
                    favoriteViewModel.addItem(track)
                } else {
                    favoriteViewModel.removeItem(track)
                }
            }
        })
    }

    /** 등록해야 하는 옵저버 */
    private fun addObserver() {
        iTunesObserve()
        favoriteObserve()
    }

    /** API 결과 옵저버 */
    private fun iTunesObserve() {
        //API 요청,응답 에 따라 Run - Success - Fail
        iTunesViewModel.trackResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Status.Run -> { }
                is Status.Success -> {
                    for (track : ITunesTrack in it.data!!.results) {
                        track.favorit = isContainsKey(track)
                    }
                    mAdapter?.addItem(it.data.results)
                    viewVisibleSuccess()
                }
                is Status.Failure -> {
                    viewVisibleFailure()
                }
            }
        })
    }

    var favoriteMap = HashMap<Long, ITunesTrack>()
    /** DB에 저장되어 있는 항목 */
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
        //DB getAll 쿼리문 사용에 따른 Run - Success - Fail
        favoriteViewModel.trackAllData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Status.Run -> {
                    viewVisibleRun()
                }
                is Status.Success -> {
                    createFavoriteMap(it.data!!)
                    iTunesViewModel.loadSearchData(TREM, ENTRY)
                }
                is Status.Failure -> {
                    viewVisibleFailure()
                }
            }
        })
    }

    /** RUN 상태 일때 보여주는 View */
    private fun viewVisibleRun() {
        binding.loaderView.toVisible()
        binding.contentsView.toGone()
        binding.errorView.toGone()
    }

    /** SUCCESS 상태 일때 보여주는 View */
    private fun viewVisibleSuccess() {
        binding.loaderView.toGone()
        binding.contentsView.toVisible()
        binding.errorView.toGone()
    }

    /** FAIL 상태 일때 보여주는 View */
    private fun viewVisibleFailure() {
        binding.loaderView.toGone()
        binding.contentsView.toGone()
        binding.errorView.toVisible()
    }

}