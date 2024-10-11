package com.lasha.mobilenews.domain.model_mapper

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel
import com.lasha.mobilenews.data.dataModel.MainCategoryDataModel
import com.lasha.mobilenews.data.dataModel.SubCategoryDataModel
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel

@JvmName("mapToUiArticleFromDbModel")
fun List<ArticlesDbModel>.mapToUiModel(): List<ArticleUiModel> =
    this.map {
        ArticleUiModel(
            id = it.id,
            articleUrl = it.articleUrl,
            title = it.title,
            pictureUrl = it.pictureUrl,
            category = it.category,
            subcategory = listOf(it.subCategory),
            bookmarked =it.isBookmarked,
            likes = it.likes,
            dislikes = it.dislikes,
            liked = it.liked,
            disliked = it.disliked
        )
    }

fun List<ArticleDataModel>.mapToUiModel(): List<ArticleUiModel> =
    this.map {
        ArticleUiModel(
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

@JvmName("mapToUiMainCategoryDataModel")
fun List<MainCategoryDataModel>.mapToUiModel(): List<MainCategoryUiModel> =
    this.map { resultDataModel ->
        MainCategoryUiModel(
            id = resultDataModel.id,
            title = resultDataModel.title,
            subCategories = resultDataModel.subCategories.mapToSubCategoryUiModel()
        )
    }

fun List<SubCategoryDataModel>.mapToSubCategoryUiModel(): List<SubCategoryUiModel> =
    this.map { resultDataModel ->
        SubCategoryUiModel(
            id = resultDataModel.id,
            title = resultDataModel.title,
            isFavorite = resultDataModel.isFavorite
        )
    }