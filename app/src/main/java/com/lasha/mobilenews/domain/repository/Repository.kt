package com.lasha.mobilenews.domain.repository

import com.lasha.mobilenews.ui.models.ArticleUiModel

interface Repository {
    suspend fun getArticlesFromRepository(): List<ArticleUiModel>

    suspend fun addNewArticlesToRepository()
}