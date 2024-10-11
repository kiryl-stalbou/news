package com.lasha.mobilenews.ui

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.lasha.mobilenews.R
import com.lasha.mobilenews.data.DownloadWorker
import com.lasha.mobilenews.databinding.ActivityMainBinding
import com.lasha.mobilenews.di.common.BaseActivity
import com.lasha.mobilenews.util.getCurrentFragmentId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleDeepLink(this)
        setupBottomNavigation()
        component.inject(this)
        initWorker()
    }

    private fun initWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadRequest = PeriodicWorkRequestBuilder<DownloadWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(downloadRequest)
    }

    private fun setupBottomNavigation() {
        binding.run {
            bottomNavigationView.isVisible = false
            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                setupBottomNavigationActions(menuItem)
            }
        }
    }

    fun getNav(): BottomNavigationView = binding.bottomNavigationView

    private fun setupBottomNavigationActions(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.newsItem -> {
            if (getCurrentFragmentId(this) == R.id.categoriesFragment) {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_categoriesFragment_to_articlesMainFragment)
                true
            } else false
        }

        R.id.categoriesItem -> {
            if (getCurrentFragmentId(this) == R.id.articlesMainFragment) {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_articlesMainFragment_to_categoriesFragment)
                true
            } else false
        }

        else -> false
    }

    private fun handleDeepLink(activity: MainActivity) {
        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(activity) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                    deepLink?.let { uri ->
                        val path = uri.toString()
                            .substring(deepLink.toString().lastIndexOf(SLASH) + SIZE_PLUS_ONE)

                        if (uri.toString().contains(WEB_VIEW)) {
                            val args = bundleOf(ID_TEXT to path.toInt())
                            val pendingIntent = NavDeepLinkBuilder(activity)
                                .setGraph(R.navigation.nav_graph)
                                .setDestination(R.id.webViewFragment)
                                .setArguments(args)
                                .createPendingIntent()
                            pendingIntent.send()
                        }
                    }
                }
        }
    }

    companion object {
        const val WEB_VIEW = "webView"
        const val ID_TEXT = "id"
        const val SLASH = "/"
        const val SIZE_PLUS_ONE = 1
    }
}