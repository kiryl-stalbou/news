package com.lasha.mobilenews

import android.app.Application
import com.lasha.mobilenews.di.DaggerApplicationComponent
import com.lasha.mobilenews.di.modules.LocalDbModule
import com.lasha.mobilenews.di.modules.MobileNewsModule

class MainApplication : Application() {

    val appComponent = DaggerApplicationComponent
        .builder()
        .mobileNewsModule(MobileNewsModule(this))
        .localDbModule(LocalDbModule(this))
        .build()
}