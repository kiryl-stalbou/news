package com.lasha.mobilenews.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lasha.mobilenews.data.dbModel.ArticlesDbModel
import javax.inject.Singleton

const val TABLE_NAME = "News_database"

@Database(
    entities = [ArticlesDbModel::class],
    version = 1
)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        @Singleton
        fun getDatabase(context: Context): ArticleDatabase =
            Room.databaseBuilder(
                context,
                ArticleDatabase::class.java,
                TABLE_NAME,
            ).fallbackToDestructiveMigration().build()
    }
}