package com.devhyeon.watchatask.ui.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Fragment Class 에서 상속받는 추상클래스
 * */
abstract class BaseFragment : Fragment() {
    //viewBinding
    protected abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    protected abstract fun getViewRoot() : View
    //데이터 등 기타 초기화
    protected abstract fun init()
    //리스너, 옵저버
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