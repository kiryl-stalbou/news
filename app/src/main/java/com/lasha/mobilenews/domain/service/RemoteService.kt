package com.lasha.mobilenews.domain.service

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dataModel.MainCategoryDataModel
import com.lasha.mobilenews.data.dataModel.SubCategoryDataModel

interface RemoteService {

    suspend fun fetchCategories(): List<MainCategoryDataModel>

    suspend fun fetchSubCategories(): List<SubCategoryDataModel>

    suspend fun fetchFilteredSubCategories(categoryDataModel: MainCategoryDataModel): List<SubCategoryDataModel>

    suspend fun fetchArticles(): List<ArticleDataModel>

    suspend fun fetchArticlesByCategoryAndSubCategory(
        subCategoryDataModel: SubCategoryDataModel,
        categoryDataModel: MainCategoryDataModel
    ): List<ArticleDataModel>

    suspend fun onLikeArticleClickAction(id: Int, likeClicked: Boolean, dislikeClicked: Boolean)

    suspend fun onDisLikeArticleClickAction(id: Int, dislikeClicked: Boolean, likeClicked: Boolean)

    suspend fun uploadArticle(article: ArticleDataModel)
}