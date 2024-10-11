package com.lasha.mobilenews.data.repository

import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.domain.repository.LocalRepository
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.repository.Repository
import com.lasha.mobilenews.ui.models.ArticleUiModel

class RepositoryImpl(
    private val local: LocalRepository,
    private val remote: RemoteRepository
) : Repository {
    private var articlesRepo = listOf<ArticleUiModel>()

    override suspend fun getArticlesFromRepository(): List<ArticleUiModel> {
        val localArticles = local.getArticlesFromLocalRepository()
        val remoteArticles = remote.fetchArticles()

        local.updateArticlesIntoLocalRepository(remoteArticles)
        setArticlesRepo(localArticles.mapToUiModel())
        return articlesRepo
    }

    override suspend fun addNewArticlesToRepository() {

    }

    private fun setArticlesRepo(articles: List<ArticleUiModel>) {
        articlesRepo = articles
    }
}