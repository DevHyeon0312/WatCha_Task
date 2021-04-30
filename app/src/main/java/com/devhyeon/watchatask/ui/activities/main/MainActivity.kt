package com.devhyeon.watchatask.ui.activities.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import com.devhyeon.watchatask.databinding.ActivityMainBinding
import com.devhyeon.watchatask.network.ITunesViewModel
import com.devhyeon.watchatask.ui.activities.base.BaseActivity
import com.devhyeon.watchatask.ui.fragments.FavoriteFragment
import com.devhyeon.watchatask.ui.fragments.SearchFragment
import com.devhyeon.watchatask.utils.Status
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModel()

    private val searchFragment by lazy { SearchFragment() }
    private val favoriteFragment by lazy { FavoriteFragment() }

    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewRoot(): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addObserver()

        binding.bottomNavigationView.run {
            setOnNavigationItemSelectedListener {
                mainViewModel.clickNavigation(it.itemId)
                true
            }
            selectedItemId = R.id.menu_search
        }
    }


    private fun addObserver() {
        navigationObserve()
    }

    private fun navigationObserve() {
        with(mainViewModel) {
            navigationData.observe(this@MainActivity, Observer {
                when(it) {
                    is Status.Run -> {}
                    is Status.Success -> {
                        when(it.data) {
                            FRAGMENT_SEARCH -> {
                                changeFragment(searchFragment)
                            }
                            FRAGMENT_FAVORITE -> {
                                changeFragment(favoriteFragment)
                            }
                        }
                    }
                    is Status.Failure -> {}
                }
            })
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()
    }
}