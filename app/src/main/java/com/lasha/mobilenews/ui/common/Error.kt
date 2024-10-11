package com.lasha.mobilenews.ui.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Error : Parcelable {

    NO_INTERNET_CONNECTION,

    SERVER_IS_NOT_RESPONDING,

    TIME_OUT,

    ERROR_WITH_AUTHENTICATION,

    ERROR_WITH_REALTIME_DB,

    NOTHING_FOUND
}


