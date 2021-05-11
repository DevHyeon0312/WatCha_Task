package com.devhyeon.watchatask.ui.activities.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import com.devhyeon.watchatask.databinding.ActivityMainBinding
import com.devhyeon.watchatask.ui.activities.base.BaseBindingActivity
import com.devhyeon.watchatask.ui.fragments.FavoriteFragment
import com.devhyeon.watchatask.ui.fragments.SearchFragment
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

    //화면에 보여줄 Fragment
    private val searchFragment by lazy { SearchFragment() }
    private val favoriteFragment by lazy { FavoriteFragment() }

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
        //viewModel 에서 유지되고 있는 데이터가 없는 경우 : ex. 최초 생성
        if(bottomNavigationViewModel.navigationData.value == null) {
            bottomNavigationViewModel.clickNavigation(R.id.menu_search)
        }
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
                it?.let { fragmentId -> selectFragment(fragmentId) }
            })
        }
    }

    /** 프래그먼트 결정 */
    private fun selectFragment(fragmentId: Int) {
        when(fragmentId) {
            FRAGMENT_SEARCH -> {
                changeFragment(searchFragment)
            }
            FRAGMENT_FAVORITE -> {
                changeFragment(favoriteFragment)
            }
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