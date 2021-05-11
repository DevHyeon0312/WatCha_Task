package com.devhyeon.watchatask.viewModel

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import kotlinx.coroutines.launch

/**
 * BottomNavigation 에서 선택된 Item 을 LiveData 를 통해 관찰 할 수 있습니다.
 *
 * */
class BottomNavigationViewModel : ViewModel(){
    private val _navigationData = MutableLiveData<Int>().apply { FRAGMENT_SEARCH }
    val navigationData: LiveData<Int> get() = _navigationData

    /** 클릭 이벤트에 따라 보여줘야하는 Fragment 선택 */
    fun clickNavigation(@IdRes id:Int) {
        viewModelScope.launch {
            when(id) {
                R.id.menu_search -> {
                    _navigationData.value = FRAGMENT_SEARCH
                }
                R.id.menu_star -> {
                    _navigationData.value = FRAGMENT_FAVORITE
                }
            }
        }
    }

    /** navigationData 의 값이 할당되기 전에 호출하는 경우 default 값으로 결정 */
    fun initNavigation() {
        if(navigationData.value == null) {
            _navigationData.value = FRAGMENT_SEARCH
        }
    }
}