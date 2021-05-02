package com.devhyeon.watchatask.ui.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devhyeon.watchatask.databinding.FragmentFavoritelistBinding

abstract class BaseFragment : Fragment() {
    protected abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    protected abstract fun getViewRoot() : View
    protected abstract fun init()
    protected abstract fun addListener()
    protected abstract fun addObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewBinding(inflater, container, savedInstanceState)
        init()
        return getViewRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
        addObserver()
    }
}