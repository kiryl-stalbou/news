package com.lasha.mobilenews.domain.model_mapper

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel

@JvmName("mapToNewsDbFromArticleDataModel")
fun List<ArticleDataModel>.mapToNewsDbModel(): List<ArticlesDbModel> =
    this.map {
        ArticlesDbModel(
            id = it.id,
            articleUrl = it.articleUrl,
            title = it.title,
            pictureUrl = it.pictureUrl,
            category = it.category,
            subCategory = it.subcategory[0],
            isBookmarked = it.bookmarked,
            likes = it.likes,
            dislikes = it.dislikes,
            liked = it.liked,
            disliked = it.disliked
        )
    }

