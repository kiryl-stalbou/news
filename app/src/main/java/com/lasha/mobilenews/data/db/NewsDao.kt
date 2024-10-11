package com.lasha.mobilenews.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel

@Dao
interface NewsDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getArticlesRoom(): List<ArticlesDbModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllArticles(data: List<ArticlesDbModel>)

    @Insert()
    suspend fun insertOneArticle(article: ArticlesDbModel)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
}