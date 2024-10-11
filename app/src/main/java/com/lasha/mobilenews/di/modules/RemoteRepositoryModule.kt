package com.lasha.mobilenews.di.modules

import com.lasha.mobilenews.data.remote.RemoteServiceImpl
import com.lasha.mobilenews.data.repository.RemoteRepositoryImpl
import com.lasha.mobilenews.domain.repository.RemoteRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteRepositoryModule {
    @Provides
    @Singleton
    fun provideRemoteRepository(remoteService: RemoteServiceImpl): RemoteRepository{
        return RemoteRepositoryImpl(remoteService)
    }
}