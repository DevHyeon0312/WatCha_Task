package com.devhyeon.watchatask.ui.activities.main

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){
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
}