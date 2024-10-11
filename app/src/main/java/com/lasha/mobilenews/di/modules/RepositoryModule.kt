package com.lasha.mobilenews.di.modules

import com.lasha.mobilenews.data.repository.RepositoryImpl
import com.lasha.mobilenews.domain.repository.LocalRepository
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(local: LocalRepository, remote: RemoteRepository): Repository{
        return RepositoryImpl(local, remote)
    }
}