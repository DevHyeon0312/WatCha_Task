package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.databinding.FragmentFavoritelistBinding
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.ui.adapters.FavoriteListAdapter
import com.devhyeon.watchatask.utils.Status
import com.devhyeon.watchatask.utils.toGone
import com.devhyeon.watchatask.utils.toVisible
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    //바인딩
    private var _binding: FragmentFavoritelistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    //어댑터
    private var mAdapter: FavoriteListAdapter? = FavoriteListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritelistBinding.inflate(inflater, container, false)

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
        viewVisibleRun()
        favoriteViewModel.getAll()
    }

    /** View 가 제거될 때 함께 제거 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        favoriteViewModel.trackChangeData.removeObservers(viewLifecycleOwner)
    }

    /** 등록해야 하는 리스너 */
    private fun addListener() {
        //리스트 아이템 클릭 리스너
        mAdapter!!.setOnItemClickListener(object : FavoriteListAdapter.OnItemClickListener {
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
        favoriteObserve()
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
                    addFavoriteItem(it.data!!)
                    viewVisibleSuccess()
                }
                is Status.Failure -> {
                    viewVisibleFailure()
                }
            }
        })
    }

    /** 즐겨찾기 항목 추가 */
    private fun addFavoriteItem(list : List<ITunesTrack>) {
        mAdapter?.mPostList = list
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