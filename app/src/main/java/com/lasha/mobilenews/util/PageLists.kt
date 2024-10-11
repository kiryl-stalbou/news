package com.lasha.mobilenews.util

import android.content.Context
import com.lasha.mobilenews.R
import com.lasha.mobilenews.ui.models.OnBoardingPageData

class
PageLists(context: Context) {

    val onBoardingList = listOf(
        OnBoardingPageData(R.raw.working_on_a_laptop, context.getString(R.string.title_first)),
        OnBoardingPageData(R.raw.scrolling, context.getString(R.string.title_second)),
        OnBoardingPageData(R.raw.data_share, context.getString(R.string.title_third))
    )
}