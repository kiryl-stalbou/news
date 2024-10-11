package com.lasha.mobilenews.ui.models

data class OnLikeClick(
    val model: ArticleUiModel,
    val isLikeClicked: Boolean,
    val isDislikeClicked: Boolean
    )