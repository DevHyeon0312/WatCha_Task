package com.devhyeon.watchatask.ui.activities.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.databinding.ActivityMainBinding
import com.devhyeon.watchatask.ui.activities.base.BaseBindingActivity
import com.devhyeon.watchatask.viewModel.BottomNavigationViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * bottomNavigationView 클릭에 따라 화면에 보여줄 Fragment 를 교체하며 보여주는 MainActivity
 * 1. Create
 * 2. SearchFragment 가 첫 시작
 * 3. BottomNavigationClick 으로 Fragment 교체
 * */
class MainActivity : BaseBindingActivity() {
    private lateinit var binding: ActivityMainBinding

    //BottomNavigation Click 에 따라 처리하는 ViewModel
    private val bottomNavigationViewModel: BottomNavigationViewModel by viewModel()

    //Binding
    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    //Layout 설정
    override fun getViewRoot(): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObserver()   //옵저버 추가
        addListener()   //리스너 추가
        init()          //데이터 초기화
    }

    /** MainActivity 동작에 필요한 부분 초기화  */
    private fun init() {
        //viewModel 에 현재 보여주어야 하는 데이터에 대한 요청 [만약, observer 가 동작하는 상황에는, 내부적으로 동작하지 않음]
        bottomNavigationViewModel.initNavigation()
    }

    /** 리스너 추가 */
    private fun addListener() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            bottomNavigationViewModel.clickNavigation(it.itemId)
            true
        }
    }

    /** 옵저버 추가 */
    private fun addObserver() {
        navigationObserve()
    }

    /** navigationData 에 따른 상태 옵저버 */
    private fun navigationObserve() {
        with(bottomNavigationViewModel) {
            navigationData.observe(this@MainActivity, Observer {
                it?.let { fragment -> changeFragment(fragment) }
            })
        }
    }

    /** 프래그먼트 교체 */
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()
    }

    companion object {
        private val TAG = MainActivity::class.java.name
    }
}