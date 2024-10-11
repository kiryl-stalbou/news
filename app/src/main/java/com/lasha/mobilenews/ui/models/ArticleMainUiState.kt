package com.lasha.mobilenews.ui.models

data class ArticleMainUiState(
    val pressedChips: List<SubCategoryUiModel>,
    val searchText: String,
    val articleList: List<ArticleUiModel>
)
