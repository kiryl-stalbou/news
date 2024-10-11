package com.lasha.mobilenews.domain.repository

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel

interface RemoteRepository {

    suspend fun fetchCategories(): List<MainCategoryUiModel>

    suspend fun fetchSubCategories(): List<SubCategoryUiModel>

    suspend fun fetchFilteredSubCategories(categoryUiModel: MainCategoryUiModel): List<SubCategoryUiModel>

    suspend fun fetchArticles(): List<ArticlesDbModel>

    suspend fun fetchArticlesByCategoryAndSubCategory(
        subCategoryUiModel: SubCategoryUiModel,
        categoryUiModel: MainCategoryUiModel
    ): List<ArticleUiModel>

    suspend fun onLikeArticleClickAction(id: Int, likeClicked: Boolean, dislikeClicked: Boolean)

    suspend fun onDisLikeArticleClickAction(id: Int, dislikeClicked: Boolean, likeClicked: Boolean)

    suspend fun uploadArticle(article: ArticleDataModel)
}