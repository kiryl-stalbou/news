package com.lasha.mobilenews.ui.saved_articles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.ui.models.OnLikeClick
import com.lasha.mobilenews.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedArticlesViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    val articlesData = MutableLiveData<List<ArticleUiModel>>()

    val isDataFetchFailed = MutableLiveData<Unit>()

    val isRemoteHasError = MutableLiveData<Boolean>()

    val removedArticle = MutableLiveData<ArticleUiModel>()

    val visibilityOfProgress = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            visibilityOfProgress.postValue(true)
            fetchSavedArticles()
            visibilityOfProgress.postValue(false)
        }
    }

    private suspend fun fetchSavedArticles() {
        val uid = getUid(firebaseAuth)
        if (uid.isNotEmpty()) {
            val remoteArticlesData = remoteRepository.fetchArticles()
            databaseReference.child(uid).child(ARTICLES).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch { }
                    val savedArticlesData = task.result.children.mapNotNull { data ->
                        data.getValue<ArticleUiModel>()
                    }
                    val combinedArticles =
                        fetchCombinedArticles(remoteArticlesData.mapToUiModel(), savedArticlesData)
                    for (element in combinedArticles) {
                        if (element.bookmarked) {
                           articlesData.addItem(element)
                        }
                    }
                } else {
                    isDataFetchFailed.postValue(Unit)
                }
            }
        }
    }

    fun onBookmarkClick(articleData: OnBookmarkClick) {
        viewModelScope.launch(Dispatchers.IO) {
            handleOnBookmarkClick(articleData, getUid(firebaseAuth), databaseReference)
            if (!articleData.isBookmarked) {
                removedArticle.postValue(articleData.model)
            }
        }
    }

    fun onLikeClicked(likeClick: OnLikeClick) {
        viewModelScope.launch(Dispatchers.IO) {
            handleOnLikeClicked(likeClick, getUid(firebaseAuth), databaseReference, remoteRepository)
        }
    }

    fun onDislikeClicked(dislikeClick: OnLikeClick) {
        viewModelScope.launch {
            handleOnDislikeClicked(dislikeClick, getUid(firebaseAuth), databaseReference, remoteRepository)
        }
    }
}