package com.lasha.mobilenews.domain.model_mapper

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dataModel.MainCategoryDataModel
import com.lasha.mobilenews.data.dataModel.SubCategoryDataModel
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel

@JvmName("mapToUiArticleFromDataModel")
fun List<ArticleUiModel>.mapToArticleDataModel(): List<ArticleDataModel> =
    this.map {
        ArticleDataModel(
            id = it.id,
            articleUrl = it.articleUrl,
            title = it.title,
            pictureUrl = it.pictureUrl,
            category = it.category,
            subcategory = it.subcategory,
            bookmarked =it.bookmarked,
            likes = it.likes,
            dislikes = it.dislikes,
            liked = it.liked,
            disliked = it.disliked
        )
    }
fun MainCategoryUiModel.mapToMainCategoryDataModel(): MainCategoryDataModel =
    MainCategoryDataModel(
        id = this.id,
        title = this.title,
        subCategories = this.subCategories.mapToDataModel()
    )

fun List<SubCategoryUiModel>.mapToDataModel(): List<SubCategoryDataModel> =
    this.map { resultDataModel ->
        SubCategoryDataModel(
            id = resultDataModel.id,
            title = resultDataModel.title,
            isFavorite = resultDataModel.isFavorite
        )
    }

fun SubCategoryUiModel.mapToDataModel(): SubCategoryDataModel =
    SubCategoryDataModel(
        id = this.id,
        title = this.title,
        isFavorite = this.isFavorite
    )