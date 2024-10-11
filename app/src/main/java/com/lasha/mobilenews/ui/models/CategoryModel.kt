package com.lasha.mobilenews.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

open class CategoryUiModel(
    open val id: Int = 0,
    open val title: String = "",
)

@Parcelize
data class MainCategoryUiModel(
    override val id: Int = 0,
    override val title: String = "",
    val subCategories: List<SubCategoryUiModel> = emptyList()
) : CategoryUiModel(), Parcelable

@Parcelize
data class SubCategoryUiModel(
    override val id: Int = 0,
    override val title: String = "",
    var isFavorite: Boolean = false
) : CategoryUiModel(), Parcelable