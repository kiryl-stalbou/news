package com.lasha.mobilenews.domain.repository

import com.lasha.mobilenews.data.dbModel.ArticlesDbModel

interface LocalRepository {

    suspend fun getArticlesFromLocalRepository(): List<ArticlesDbModel>

    suspend fun updateArticlesIntoLocalRepository(articles: List<ArticlesDbModel>)

    suspend fun addNewArticleInLocalBd(newArticle: ArticlesDbModel)
}