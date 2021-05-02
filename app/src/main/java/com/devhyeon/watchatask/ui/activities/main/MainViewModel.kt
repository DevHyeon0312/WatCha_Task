package com.devhyeon.watchatask.ui.activities.main

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.constant.FRAGMENT_FAVORITE
import com.devhyeon.watchatask.constant.FRAGMENT_SEARCH
import com.devhyeon.watchatask.constant.NAVIGATION_CLICK_ERROR
import com.devhyeon.watchatask.utils.Status
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){
    private val _navigationData = MutableLiveData<Status<Int>>()
    val navigationData: LiveData<Status<Int>> get() = _navigationData


    /** 클릭 이벤트에 따라 보여줘야하는 Fragment 선택 */
    fun clickNavigation(@IdRes id:Int) {
        var result : Int = 0
        viewModelScope.launch {
            runCatching {
                _navigationData.value = Status.Run(result)
                when(id) {
                    R.id.menu_search -> {
                        result = FRAGMENT_SEARCH
                    }
                    R.id.menu_star -> {
                        result = FRAGMENT_FAVORITE
                    }
                }
            }.onSuccess {
                _navigationData.value = Status.Success(result)
            }.onFailure {
                _navigationData.value = Status.Failure(NAVIGATION_CLICK_ERROR, it.message!!)
            }
        }
    }
}