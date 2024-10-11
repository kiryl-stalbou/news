package com.lasha.mobilenews.data.dbModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

open class CategoryDbModel(
    open val id: Int = 0,
    open val title: String = "",
)

@Parcelize
data class MainCategoryDbModel(
    override val id: Int = 0,
    override val title: String = "",
    val subCategories: List<SubCategoryDbModel> = emptyList()
) : CategoryDbModel(), Parcelable

@Parcelize
data class SubCategoryDbModel(
    override val id: Int = 0,
    override val title: String = "",
    var isFavorite: Boolean = false
) : CategoryDbModel(), Parcelable