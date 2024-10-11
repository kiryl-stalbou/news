package com.lasha.mobilenews.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dataModel.MainCategoryDataModel
import com.lasha.mobilenews.data.dataModel.SubCategoryDataModel
import com.lasha.mobilenews.di.utils.ArticlesReference
import com.lasha.mobilenews.di.utils.CategoriesReference
import com.lasha.mobilenews.di.utils.SubCategoriesReference
import com.lasha.mobilenews.domain.service.RemoteService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteServiceImpl @Inject constructor(
    @ArticlesReference
    private val articlesDatabaseReference: DatabaseReference,
    @CategoriesReference
    private val categoriesDatabaseReference: DatabaseReference,
    @SubCategoriesReference
    private val subCategoriesDatabaseReference: DatabaseReference
) : RemoteService {

    override suspend fun fetchCategories(): List<MainCategoryDataModel> {
        val categories = mutableListOf<MainCategoryDataModel>()
        val querySnapshot = categoriesDatabaseReference.get().await()
        categories.addAll(querySnapshot.children.mapNotNull { data -> data.getValue<MainCategoryDataModel>() })
        return categories
    }

    override suspend fun fetchSubCategories(): List<SubCategoryDataModel> {
        val subCategories = mutableListOf<SubCategoryDataModel>()
        val querySnapshot = subCategoriesDatabaseReference.get().await()
        subCategories.addAll(querySnapshot.children.mapNotNull { data -> data.getValue<SubCategoryDataModel>() })
        return subCategories
    }

    override suspend fun fetchFilteredSubCategories(categoryDataModel: MainCategoryDataModel): List<SubCategoryDataModel> {
        val subCategories = mutableListOf<SubCategoryDataModel>()
        val querySnapshot = subCategoriesDatabaseReference.get().await()
        val subCategoriesFromRemote =
            querySnapshot.children.mapNotNull { data -> data.getValue<SubCategoryDataModel>() }
        for (element in subCategoriesFromRemote) {
            if (categoryDataModel.subCategories.contains(element)) {
                subCategories.add(element)
            }
        }
        return subCategories
    }

    override suspend fun fetchArticles(): List<ArticleDataModel> {
        val articlesList = mutableListOf<ArticleDataModel>()
        val querySnapshot = articlesDatabaseReference.get().await()
        articlesList.addAll(querySnapshot.children.mapNotNull { data -> data.getValue<ArticleDataModel>() })
        return articlesList
    }

    override suspend fun fetchArticlesByCategoryAndSubCategory(
        subCategoryDataModel: SubCategoryDataModel,
        categoryDataModel: MainCategoryDataModel
    ): List<ArticleDataModel> {
        val articlesList = mutableListOf<ArticleDataModel>()
        val querySnapshot = articlesDatabaseReference.get().await()
        val articleListFromRemote =
            querySnapshot.children.mapNotNull { data -> data.getValue<ArticleDataModel>() }
        for (element in articleListFromRemote) {
            if (element.subcategory.contains(subCategoryDataModel.title) && element.category == categoryDataModel.title) {
                articlesList.add(element)
            }
        }
        return articlesList
    }

    override suspend fun onLikeArticleClickAction(
        id: Int,
        likeClicked: Boolean,
        dislikeClicked: Boolean
    ) {
        val querySnapshot =
            articlesDatabaseReference.child(id.toString()).get().await()
        val currentArticleFromFirebase = querySnapshot.getValue<ArticleDataModel>()
        val articleDataToUpload: ArticleDataModel? = when {
            likeClicked && dislikeClicked -> {
                currentArticleFromFirebase?.copy(
                    likes = currentArticleFromFirebase.likes + UPDATE_LIKES,
                    dislikes = currentArticleFromFirebase.dislikes - UPDATE_LIKES
                )
            }

            likeClicked && !dislikeClicked -> {
                currentArticleFromFirebase?.copy(likes = currentArticleFromFirebase.likes + UPDATE_LIKES)
            }

            else -> {
                currentArticleFromFirebase?.copy(likes = currentArticleFromFirebase.likes - UPDATE_LIKES)
            }
        }
        if (articleDataToUpload != null) {
            articlesDatabaseReference.child(id.toString())
                .setValue(articleDataToUpload)
        }
    }

    override suspend fun onDisLikeArticleClickAction(
        id: Int,
        dislikeClicked: Boolean,
        likeClicked: Boolean
    ) {
        val querySnapshot =
            articlesDatabaseReference.child(id.toString()).get().await()
        val currentArticleFromFirebase = querySnapshot.getValue<ArticleDataModel>()
        val articleDataToUpload: ArticleDataModel? = when {
            dislikeClicked && likeClicked -> {
                currentArticleFromFirebase?.copy(
                    dislikes = currentArticleFromFirebase.dislikes + UPDATE_LIKES,
                    likes = currentArticleFromFirebase.likes - UPDATE_LIKES
                )
            }

            dislikeClicked && !likeClicked -> {
                currentArticleFromFirebase?.copy(dislikes = currentArticleFromFirebase.dislikes + UPDATE_LIKES)
            }

            else -> {
                currentArticleFromFirebase?.copy(dislikes = currentArticleFromFirebase.dislikes - UPDATE_LIKES)
            }
        }
        if (articleDataToUpload != null) {
            articlesDatabaseReference.child(id.toString())
                .setValue(articleDataToUpload)
        }
    }

    override suspend fun uploadArticle(newArticleDataModel: ArticleDataModel) {
        articlesDatabaseReference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {

                currentData.value ?: return Transaction.success(currentData)

                val countId = currentData.childrenCount.toString()
                articlesDatabaseReference.child(countId).setValue(newArticleDataModel)

                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) = Unit
        })
    }

    companion object {
        const val UPDATE_LIKES = 1
    }
}