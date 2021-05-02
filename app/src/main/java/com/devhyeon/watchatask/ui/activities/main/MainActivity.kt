package com.devhyeon.watchatask.ui.activities.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import com.devhyeon.watchatask.databinding.ActivityMainBinding
import com.devhyeon.watchatask.ui.activities.base.BaseActivity
import com.devhyeon.watchatask.ui.fragments.FavoriteFragment
import com.devhyeon.watchatask.ui.fragments.SearchFragment
import com.devhyeon.watchatask.utils.DebugLog
import com.devhyeon.watchatask.utils.Status
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * bottomNavigationView 클릭에 따라 화면에 보여줄 Fragment 를 교체하며 보여주는 MainActivity
 * 1. Create
 * 2. SearchFragment 가 첫 시작
 * 3. BottomNavigationClick 으로 Fragment 교체
 * */
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    //BottomNavigation Click 에 따라 처리하는 ViewModel
    private val mainViewModel: MainViewModel by viewModel()

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

        addObserver()

        init()
    }

    /** 화면이 회전할 때 동작하는 메소드 */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        DebugLog.d(TAG,"onConfigurationChanged()")
    }

    /** MainActivity 동작에 필요한 부분 초기화  */
    private fun init() {
        binding.bottomNavigationView.run {
            setOnNavigationItemSelectedListener {
                mainViewModel.clickNavigation(it.itemId)
                true
            }
            selectedItemId = R.id.menu_search
        }
    }

    /** 옵저버 추가 */
    private fun addObserver() {
        navigationObserve()
    }

    /** bottomNavigation Click 에 따른 상태 옵저버 */
    private fun navigationObserve() {
        with(mainViewModel) {
            navigationData.observe(this@MainActivity, Observer {
                when(it) {
                    is Status.Run -> {
                        DebugLog.d(TAG, it.data.toString())
                    }
                    is Status.Success -> {
                        DebugLog.d(TAG, it.data.toString())
                        when(it.data) {
                            FRAGMENT_SEARCH -> {
                                DebugLog.d(TAG, "FRAGMENT_SEARCH")
                                changeFragment(searchFragment)
                            }
                            FRAGMENT_FAVORITE -> {
                                DebugLog.d(TAG, "FRAGMENT_FAVORITE")
                                changeFragment(favoriteFragment)
                            }
                        }
                    }
                    is Status.Failure -> {
                        DebugLog.e(TAG, it.errorMessage!!)
                    }
                }
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