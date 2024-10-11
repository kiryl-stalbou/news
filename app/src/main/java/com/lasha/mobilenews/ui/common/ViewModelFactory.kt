package com.lasha.mobilenews.ui.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lasha.mobilenews.domain.repository.LocalRepository
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.repository.Repository
import com.lasha.mobilenews.ui.articles_filtered.ArticleFilteredViewModel
import com.lasha.mobilenews.ui.articles_main.ArticleMainViewModel
import com.lasha.mobilenews.ui.categories.CategoriesViewModel
import com.lasha.mobilenews.ui.list_interests.ListInterestsViewModel
import com.lasha.mobilenews.ui.my_interests.MyInterestsViewModel
import com.lasha.mobilenews.ui.onboarding_pager.PagerViewModel
import com.lasha.mobilenews.ui.onboarding_splash.SplashScreenViewModel
import com.lasha.mobilenews.ui.profile.ProfileViewModel
import com.lasha.mobilenews.ui.saved_articles.SavedArticlesViewModel
import com.lasha.mobilenews.ui.sign_in.SignInViewModel
import com.lasha.mobilenews.ui.subctegories.SubcategoriesViewModel
import com.lasha.mobilenews.ui.web_view.WebViewViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val uid: String?,
    private val sharedPref: SharedPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val remoteRepository: RemoteRepository,
    private val database: DatabaseReference,
    private val localRepository: LocalRepository,
    private val repository: Repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            PagerViewModel::class.java -> createPagerViewModel()
            SignInViewModel::class.java -> createSignInViewModel()
            SplashScreenViewModel::class.java -> createSplashScreenViewModel()
            ArticleMainViewModel::class.java -> createArticleMainViewModel()
            ListInterestsViewModel::class.java -> createListInterestsViewModel()
            ArticleFilteredViewModel::class.java -> createArticleFilteredViewModel()
            CategoriesViewModel::class.java -> createCategoriesViewModel()
            SubcategoriesViewModel::class.java -> createSubcategoriesViewModel()
            MyInterestsViewModel::class.java -> createMyInterestsViewModel()
            ProfileViewModel::class.java -> createProfileViewModel()
            SavedArticlesViewModel::class.java -> createSavedArticlesViewModel()
            WebViewViewModel::class.java -> createWebViewViewModel()
            else ->
                throw IllegalStateException("unknown viewModel class ${modelClass.canonicalName}")
        } as T

    private fun createPagerViewModel(): PagerViewModel =
        PagerViewModel(
            sharedPref
        )

    private fun createSignInViewModel(): SignInViewModel =
        SignInViewModel(firebaseAuth, database)

    private fun createSplashScreenViewModel(): SplashScreenViewModel =
        SplashScreenViewModel(firebaseAuth, firebaseAuth, database)

    private fun createListInterestsViewModel(): ListInterestsViewModel =
        ListInterestsViewModel(firebaseAuth, remoteRepository, database)

    private fun createArticleMainViewModel(): ArticleMainViewModel =
        ArticleMainViewModel(remoteRepository, database, firebaseAuth, repository)

    private fun createArticleFilteredViewModel(): ArticleFilteredViewModel =
        ArticleFilteredViewModel(remoteRepository, database, firebaseAuth)

    private fun createCategoriesViewModel(): CategoriesViewModel =
        CategoriesViewModel(remoteRepository, firebaseAuth)

    private fun createSubcategoriesViewModel(): SubcategoriesViewModel =
        SubcategoriesViewModel(remoteRepository)

    private fun createMyInterestsViewModel(): MyInterestsViewModel =
        MyInterestsViewModel(firebaseAuth, remoteRepository, database)

    private fun createProfileViewModel(): ProfileViewModel =
        ProfileViewModel(firebaseAuth)

    private fun createSavedArticlesViewModel(): SavedArticlesViewModel =
        SavedArticlesViewModel(firebaseAuth, database, remoteRepository)

    private fun createWebViewViewModel(): WebViewViewModel =
        WebViewViewModel(database, firebaseAuth, remoteRepository)
}