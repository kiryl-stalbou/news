package com.lasha.mobilenews.di.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lasha.mobilenews.MainApplication
import com.lasha.mobilenews.di.ApplicationComponent

abstract class BaseActivity : AppCompatActivity() {
    lateinit var component: ApplicationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = (applicationContext as MainApplication).appComponent
    }
}