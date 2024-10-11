package com.lasha.mobilenews.ui.web_view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.util.ARTICLES
import com.lasha.mobilenews.util.fetchCombinedArticles
import com.lasha.mobilenews.util.getUid
import com.lasha.mobilenews.util.handleOnBookmarkClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebViewViewModel(
    private val dataSource: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    val isRemoteHasError = MutableLiveData<Boolean>()

    val selectedArticle = MutableLiveData<ArticleUiModel>()

    fun getArticle(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val databaseArticles = remoteRepository.fetchArticles()
            val uid = getUid(firebaseAuth)
            if (uid.isNotEmpty()) {
                dataSource.child(uid).child(ARTICLES).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val savedArticles = task.result.children.mapNotNull { data ->
                            data.getValue<ArticleUiModel>()
                        }
                        val combinedArticles = fetchCombinedArticles(
                            databaseArticles.mapToUiModel(),
                            savedArticles
                        )
                        for (element in combinedArticles) {
                            if (element.id == articleId) {
                                selectedArticle.postValue(element)
                            }
                        }
                    } else {
                        isRemoteHasError.postValue(true)
                    }
                }
            }
        }
    }

    fun onBookmarkClick(articleData: OnBookmarkClick) {
        viewModelScope.launch(Dispatchers.IO) {
            handleOnBookmarkClick(articleData, getUid(firebaseAuth), dataSource)
        }
    }
}