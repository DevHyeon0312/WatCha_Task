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
import com.devhyeon.watchatask.ui.adapters.OnToggleClickListener
import com.devhyeon.watchatask.ui.fragments.base.BaseFragment
import com.devhyeon.watchatask.utils.DebugLog
import com.devhyeon.watchatask.utils.Status
import com.devhyeon.watchatask.utils.toGone
import com.devhyeon.watchatask.utils.toVisible
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * 즐겨찾기한 대상을 보여주는 Fragment
 * 1. DB 에서 즐겨찾기된 아이템 get
 * 2. 결과 출력
 * 3. 즐겨찾기 클릭에 따른 동작이벤트
 * */
class FavoriteFragment : BaseFragment() , OnToggleClickListener {
    companion object {
        private val TAG = FavoriteFragment::class.java.name
    }

    //바인딩
    private var _binding: FragmentFavoritelistBinding? = null
    private val binding get() = _binding!!

    //뷰모델
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    //어댑터
    private var mAdapter: FavoriteListAdapter? = FavoriteListAdapter(this)

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        _binding = FragmentFavoritelistBinding.inflate(inflater, container, false)
    }

    override fun getViewRoot(): View {
        return binding.root
    }

    override fun init() {
        binding.rvTrackList.adapter = mAdapter
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
        favoriteViewModel.trackAllData.removeObservers(viewLifecycleOwner)
    }

    /** 등록해야 하는 리스너 */
    private fun addListener() {
        //토글버튼 클릭 리스너
        mAdapter!!.setOnToggleClickListener(this)

        //새로고침 클릭 이벤트
        binding.btnRefresh.setOnClickListener {
            viewVisibleRun()
            favoriteViewModel.getAll()
        }
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
                    viewVisibleSuccess(it.data)
                }
                is Status.Failure -> {
                    DebugLog.e(TAG,"favoriteObserve()", it.errorMessage!!)
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

    /** SUCCESS DATA 에 따라 보여주는 View */
    private fun viewVisibleSuccess(list : List<ITunesTrack>) {
        if(list.isEmpty()) {
            viewVisibleEmpty()
        } else {
            viewVisibleNotEmpty()
        }
    }
    /** SUCCESS 상태[즐겨찾기 있음] 일때 보여주는 View */
    private fun viewVisibleNotEmpty() {
        binding.loaderView.toGone()
        binding.contentsView.toVisible()
        binding.favoriteView.toGone()
        binding.errorView.toGone()
    }
    /** SUCCESS 상태[즐겨찾기 없음] 일때 보여주는 View */
    private fun viewVisibleEmpty() {
        binding.loaderView.toGone()
        binding.contentsView.toGone()
        binding.favoriteView.toVisible()
        binding.errorView.toGone()
    }

    /** FAIL 상태 일때 보여주는 View */
    private fun viewVisibleFailure() {
        binding.loaderView.toGone()
        binding.contentsView.toGone()
        binding.errorView.toVisible()
    }

    /** 토클 클릭 리스너 */
    override fun onToggleClick(v: View?, track: ITunesTrack) {
        if (track.favorit) {
            favoriteViewModel.addItem(track)
        } else {
            favoriteViewModel.removeItem(track)
        }
    }
}