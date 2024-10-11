package com.lasha.mobilenews.di

import com.lasha.mobilenews.di.modules.LocalDbModule
import com.lasha.mobilenews.di.modules.MobileNewsModule
import com.lasha.mobilenews.di.modules.RemoteRepositoryModule
import com.lasha.mobilenews.di.modules.RepositoryModule
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.articles_filtered.ArticleFilteredFragment
import com.lasha.mobilenews.ui.articles_main.ArticleMainFragment
import com.lasha.mobilenews.ui.categories.CategoriesFragment
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.list_interests.ListInterestsFragment
import com.lasha.mobilenews.ui.my_interests.MyInterestsFragment
import com.lasha.mobilenews.ui.onboarding_pager.PagerFragment
import com.lasha.mobilenews.ui.onboarding_splash.SplashScreenFragment
import com.lasha.mobilenews.ui.profile.ProfileFragment
import com.lasha.mobilenews.ui.sign_in.SignInFragment
import com.lasha.mobilenews.ui.subctegories.SubcategoriesFragment
import com.lasha.mobilenews.ui.web_view.WebViewFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LocalDbModule::class, MobileNewsModule::class, RemoteRepositoryModule::class, RepositoryModule::class])
interface ApplicationComponent {

    fun inject(target: MainActivity)

    fun inject(target: PagerFragment)

    fun inject(target: SplashScreenFragment)

    fun inject(target: SignInFragment)

    fun inject(target: ListInterestsFragment)

    fun inject(target: ArticleMainFragment)

    fun inject(target: ArticleFilteredFragment)

    fun inject(target: CategoriesFragment)

    fun inject(target: SubcategoriesFragment)

    fun inject(target: BaseFragment)

    fun inject(target: MyInterestsFragment)

    fun inject(target: ProfileFragment)

    fun inject(target: WebViewFragment)
}