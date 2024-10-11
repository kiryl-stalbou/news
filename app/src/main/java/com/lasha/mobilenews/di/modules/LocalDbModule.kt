package com.lasha.mobilenews.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.lasha.mobilenews.data.db.ArticleDatabase
import com.lasha.mobilenews.data.db.NewsDao
import com.lasha.mobilenews.data.repository.LocalRepositoryImpl
import com.lasha.mobilenews.domain.repository.LocalRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalDbModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideLocalRepository(dao: NewsDao): LocalRepository {
        return LocalRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideArticleDatabase(): ArticleDatabase {
        return ArticleDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNewsDao(articleDatabase: ArticleDatabase): NewsDao {
        return articleDatabase.newsDao()
    }

    @Provides
    @Singleton
    fun provideSharedPref(): SharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        const val SHARED_PREFS_NAME = "isFirstLaunch"
    }
}