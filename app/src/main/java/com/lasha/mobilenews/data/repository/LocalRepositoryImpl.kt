package com.lasha.mobilenews.data.repository

import com.lasha.mobilenews.data.db.NewsDao
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel
import com.lasha.mobilenews.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dao: NewsDao
) : LocalRepository {
    override suspend fun getArticlesFromLocalRepository(): List<ArticlesDbModel> {
        return dao.getArticlesRoom()
    }

    override suspend fun updateArticlesIntoLocalRepository(articles: List<ArticlesDbModel>) {
        dao.deleteAll()
        dao.insertAllArticles(articles)
    }

    override suspend fun addNewArticleInLocalBd(newArticle: ArticlesDbModel) {
        dao.insertOneArticle(newArticle)
    }
}