package com.lasha.mobilenews.data.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

open class CategoryDataModel(
    open val id: Int = 0,
    open val title: String = "",
)

@Parcelize
data class MainCategoryDataModel(
    override val id: Int = 0,
    override val title: String = "",
    val subCategories: List<SubCategoryDataModel> = emptyList()
) : CategoryDataModel(), Parcelable

@Parcelize
data class SubCategoryDataModel(
    override val id: Int = 0,
    override val title: String = "",
    var isFavorite: Boolean = false
) : CategoryDataModel(), Parcelable