package com.lasha.mobilenews.data.repository

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel
import com.lasha.mobilenews.domain.model_mapper.mapToDataModel
import com.lasha.mobilenews.domain.model_mapper.mapToMainCategoryDataModel
import com.lasha.mobilenews.domain.model_mapper.mapToNewsDbModel
import com.lasha.mobilenews.domain.model_mapper.mapToSubCategoryUiModel
import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.service.RemoteService
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val remoteService: RemoteService
) : RemoteRepository {

    override suspend fun fetchCategories(): List<MainCategoryUiModel> {
        return remoteService.fetchCategories().mapToUiModel()
    }

    override suspend fun fetchSubCategories(): List<SubCategoryUiModel> {
        return remoteService.fetchSubCategories().mapToSubCategoryUiModel()
    }

    override suspend fun fetchFilteredSubCategories(categoryUiModel: MainCategoryUiModel): List<SubCategoryUiModel> {
        return remoteService.fetchFilteredSubCategories(categoryUiModel.mapToMainCategoryDataModel())
            .mapToSubCategoryUiModel()
    }

    override suspend fun fetchArticles(): List<ArticlesDbModel> {
        return remoteService.fetchArticles().mapToNewsDbModel()
    }

    override suspend fun fetchArticlesByCategoryAndSubCategory(
        subCategoryUiModel: SubCategoryUiModel,
        categoryUiModel: MainCategoryUiModel
    ): List<ArticleUiModel> {
        return remoteService.fetchArticlesByCategoryAndSubCategory(
            subCategoryUiModel.mapToDataModel(),
            categoryUiModel.mapToMainCategoryDataModel()
        ).mapToUiModel()
    }

    override suspend fun onLikeArticleClickAction(
        id: Int,
        likeClicked: Boolean,
        dislikeClicked: Boolean
    ) {
        remoteService.onLikeArticleClickAction(id, likeClicked, dislikeClicked)
    }

    override suspend fun onDisLikeArticleClickAction(
        id: Int,
        dislikeClicked: Boolean,
        likeClicked: Boolean
    ) {
        remoteService.onDisLikeArticleClickAction(id, dislikeClicked, likeClicked)
    }

    override suspend fun uploadArticle(newArticleDataModel: ArticleDataModel) {
        remoteService.uploadArticle(newArticleDataModel)
    }
}