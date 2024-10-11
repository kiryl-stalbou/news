package com.lasha.mobilenews.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleUiModel(
    val id: Int = 0,
    val articleUrl: String = "",
    val title: String = "",
    val pictureUrl: String = "",
    val category: String = "",
    val subcategory: List<String> = emptyList(),
    val bookmarked: Boolean = false,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val liked: Boolean = false,
    val disliked: Boolean = false
) : Parcelable