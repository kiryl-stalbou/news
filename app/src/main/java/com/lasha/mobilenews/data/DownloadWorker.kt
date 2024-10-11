package com.lasha.mobilenews.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lasha.mobilenews.domain.repository.LocalRepository
import com.lasha.mobilenews.domain.repository.RemoteRepository
import javax.inject.Inject

class DownloadWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @Inject
    lateinit var local: LocalRepository

    @Inject
    lateinit var remote: RemoteRepository

    override suspend fun doWork(): Result {
        dailyUpdate()
        return Result.success()
    }

    private suspend fun dailyUpdate() {
        val remoteData = remote.fetchArticles()
        local.updateArticlesIntoLocalRepository(remoteData)
    }
}