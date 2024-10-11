package com.lasha.mobilenews.ui.onboarding_pager

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PagerViewModel(private val sharedPref: SharedPreferences) :
    ViewModel() {

    val isFirstLaunch = MutableLiveData<Boolean>()

    fun checkIsFirstTimeOpened() {
        val isNotFirst = sharedPref.getBoolean(SHARED_PREFS_NAME, false)
        if (isNotFirst == IS_FIRST_LAUNCH) {
            isFirstLaunch.value = true
        } else {
            isFirstLaunch.value = false
            val editor = sharedPref.edit()
            editor.putBoolean(SHARED_PREFS_NAME, IS_FIRST_LAUNCH)
            editor.apply()
        }
    }

    companion object {
        private const val IS_FIRST_LAUNCH = true
        private const val SHARED_PREFS_NAME = "isFirstLaunch"
    }
}