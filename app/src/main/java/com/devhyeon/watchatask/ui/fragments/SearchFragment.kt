package com.devhyeon.watchatask.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.databinding.FragmentTracklistBinding
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesResponse
import com.devhyeon.watchatask.ui.adapters.TrackListAdapter
import com.devhyeon.watchatask.utils.Status
import com.devhyeon.watchatask.utils.toGone
import com.devhyeon.watchatask.utils.toVisible
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentTracklistBinding? = null
    private val binding get() = _binding!!

    private val iTunesViewModel: ITunesViewModel by viewModel()
    private var mAdapter: TrackListAdapter? = TrackListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracklistBinding.inflate(inflater, container, false)
        binding.rvTrackList.adapter = mAdapter

        addObserver()

        iTunesViewModel.loadSearchData("greenday","song")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addObserver() {
        iTunesObserve()
    }

    private fun iTunesObserve() {
        iTunesViewModel.trackResponse.observe(this@SearchFragment, Observer {
            when (it) {
                is Status.Run -> {
                    binding.loaderView.toVisible()
                    binding.contentsView.toGone()
                    binding.errorView.toGone()
                }
                is Status.Success -> {
                    mAdapter?.mPostList = it.data!!.results
                    binding.loaderView.toGone()
                    binding.contentsView.toVisible()
                    binding.errorView.toGone()
                }
                is Status.Failure -> {
                    binding.loaderView.toGone()
                    binding.contentsView.toGone()
                    binding.errorView.toVisible()
                }
            }
        })
    }
}