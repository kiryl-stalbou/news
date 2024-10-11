package com.lasha.mobilenews.ui.articles_filtered

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.models.*
import com.lasha.mobilenews.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArticleFilteredViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataSource: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val articlesDefaultData = MutableLiveData<List<ArticleUiModel>>()

    val articlesData = MutableLiveData<List<ArticleUiModel>>()

    val isRemoteHasError = MutableLiveData<Boolean>()

    val visibilityOfProgress = MutableLiveData<Boolean>()

    private var searchJob: Job? = null

    fun onSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_PERIOD)
            fetchArticlesByQuery(query)
            //TODO Добавить логику получения артиклов из бд
        }
    }

    fun setDefaultArticles() {
        articlesData.value = articlesDefaultData.value
    }

    private fun fetchArticlesByQuery(query: String) {
        visibilityOfProgress.postValue(true)
        articlesData.postValue(articlesDefaultData.filterArticles(query))
        visibilityOfProgress.postValue(false)
    }

    fun fetchFilteredArticles(
        subCategory: SubCategoryUiModel,
        category: MainCategoryUiModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            visibilityOfProgress.postValue(true)
            articlesDefaultData.postValue(
                remoteRepository.fetchArticlesByCategoryAndSubCategory(
                    subCategory,
                    category
                )
            )
            articlesData.postValue(
                remoteRepository.fetchArticlesByCategoryAndSubCategory(
                    subCategory,
                    category
                )
            )
            val allArticles = remoteRepository.fetchArticles().mapToUiModel()
            val articlesToPost = mutableListOf<ArticleUiModel>()
            for (element in allArticles) {
                if (element.subcategory.contains(subCategory.title) && element.category == category.title) {
                    articlesToPost.add(element)
                }
            }
            fetchArticles(articlesToPost)
            visibilityOfProgress.postValue(false)
        }
    }

    private fun fetchArticles(filteredArticles: List<ArticleUiModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = getUid(firebaseAuth)
            if (uid.isNotEmpty()) {
                dataSource.child(uid).child(ARTICLES).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val savedArticlesData = (task.result.children.mapNotNull { data ->
                            data.getValue<ArticleUiModel>()
                        })
                        articlesDefaultData.postValue(
                            fetchCombinedArticles(
                                filteredArticles,
                                savedArticlesData
                            )
                        )
                        articlesData.postValue(
                            fetchCombinedArticles(
                                filteredArticles,
                                savedArticlesData
                            )
                        )
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

    fun onLikeClicked(likeClick: OnLikeClick) {
        viewModelScope.launch(Dispatchers.IO) {
            handleOnLikeClicked(likeClick, getUid(firebaseAuth), dataSource, remoteRepository)
        }
    }

    fun onDislikeClicked(dislikeClick: OnLikeClick) {
        viewModelScope.launch {
            handleOnDislikeClicked(dislikeClick, getUid(firebaseAuth), dataSource, remoteRepository)
        }
    }

    companion object {
        private const val DEBOUNCE_PERIOD: Long = 1000
    }
}