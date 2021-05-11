package com.devhyeon.watchatask.ui.activities.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity Class 에서 상속받는 추상클래스
 * Activity 는 viewBinding 을 사용할 예정이므로,
 * Activity 에 View 를 적용할 때, viewBinding 을 반드시 사용하도록 추상메소드를 작성하였습니다.
 * */
abstract class BaseBindingActivity : AppCompatActivity() {
    protected abstract fun initViewBinding()
    protected abstract fun getViewRoot() : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setContentView(getViewRoot())
    }
}