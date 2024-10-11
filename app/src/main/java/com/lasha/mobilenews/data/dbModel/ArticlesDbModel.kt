package com.lasha.mobilenews.data.dbModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lasha.mobilenews.data.db.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class ArticlesDbModel(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "article_url")
    val articleUrl: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "picture_url")
    val pictureUrl: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "sub_category")
    val subCategory: String,
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "dislikes")
    val dislikes: Int,
    @ColumnInfo(name = "liked")
    val liked: Boolean,
    @ColumnInfo(name = "disliked")
    val disliked: Boolean
)