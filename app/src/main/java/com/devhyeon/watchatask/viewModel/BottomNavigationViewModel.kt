package com.devhyeon.watchatask.viewModel

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import com.devhyeon.watchatask.ui.fragments.FavoriteFragment
import com.devhyeon.watchatask.ui.fragments.SearchFragment
import kotlinx.coroutines.launch

/**
 * BottomNavigation 의 선택된 data 에 따라 어떤 fragment 를 보여주어야 하는지 해당 fragment 반환
 * */
class BottomNavigationViewModel: ViewModel() {

    private val _navigationData = MutableLiveData<Fragment>().apply { FRAGMENT_SEARCH }
    val navigationData: LiveData<Fragment> get() = _navigationData

    private val searchFragment by lazy { SearchFragment() }
    private val favoriteFragment by lazy { FavoriteFragment() }

    /** 클릭 이벤트에 따라 보여줘야하는 Fragment 선택 */
    fun clickNavigation(@IdRes id:Int) {
        viewModelScope.launch {
            when(id) {
                R.id.menu_search -> {
                    _navigationData.value = searchFragment
                }
                R.id.menu_star -> {
                    _navigationData.value = favoriteFragment
                }
            }
        }
    }

    /** navigationData 의 값이 할당되기 전에 호출하는 경우 default 값으로 결정 */
    fun initNavigation() {
        if(navigationData.value == null) {
            _navigationData.value = searchFragment
        }
    }
}