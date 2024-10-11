package com.lasha.mobilenews.ui.articles_main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.domain.model_mapper.mapToUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.repository.Repository
import com.lasha.mobilenews.ui.models.ArticleMainUiState
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.ui.models.OnLikeClick
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.util.addItem
import com.lasha.mobilenews.util.filterArticles
import com.lasha.mobilenews.util.getUid
import com.lasha.mobilenews.util.handleOnBookmarkClick
import com.lasha.mobilenews.util.handleOnDislikeClicked
import com.lasha.mobilenews.util.handleOnLikeClicked
import com.lasha.mobilenews.util.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val UPDATED = "Articles updated"
const val NOT_UPDATED = "No updates available"

class ArticleMainViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataSource: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
    private val repository: Repository
) : ViewModel() {

    val mainScreenUiState = MutableLiveData<ArticleMainUiState>()

    private var searchText: String = ""

    private var articlesData = listOf<ArticleUiModel>()

    private var articlesDefaultData = listOf<ArticleUiModel>()

    private var filteredArticlesData = listOf<ArticleUiModel>()

    val subCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val isRemoteHasError = MutableLiveData<Boolean>()

    val isDownloading = MutableLiveData<Boolean>()

    val visibilityOfProgress = MutableLiveData<Boolean>()

    private var isChipChecked = true

    private var isSearchCategoriesUsed = false

    private var isOnlySearchUsed = true

    val listCheckedSubCategories = MutableLiveData<List<SubCategoryUiModel>>()

    private var articlesDefaultSearch = listOf<ArticleUiModel>()

    private var searchJob: Job? = null

    private var pressedChips = listOf<SubCategoryUiModel>()

    val userData = MutableLiveData<String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            visibilityOfProgress.postValue(true)
            fetchArticles()
            fetchSubCategories()
            userData.postValue(
                firebaseAuth.currentUser?.photoUrl.toString()
            )
            visibilityOfProgress.postValue(false)
        }
    }

    fun refresh(view: View) {
        val oldData = articlesData

        viewModelScope.launch(Dispatchers.IO) {
            articlesDefaultData = downloadRemoteRepository()

            val resultOfCompare = compareData(oldData, articlesDefaultData)

            showMessage(view, resultOfCompare)

            articlesData = articlesDefaultData

            if (pressedChips.isNotEmpty()) {
                fetchFilteredArticles(pressedChips)
            } else {
                setupMainUiState(articlesData)
            }
        }
    }

    private fun showMessage(view: View, m: String) {
        Snackbar.make(view, m, Snackbar.LENGTH_SHORT).show()
    }

    private fun compareData(oldData: List<ArticleUiModel>, newData: List<ArticleUiModel>): String {
        for (i in oldData.indices) {
            if (oldData[i] != (newData[i])) return UPDATED
        }
        return NOT_UPDATED
    }

    fun saveScreenState(list: List<ArticleUiModel>) {
        mainScreenUiState.postValue(
            ArticleMainUiState(
                pressedChips,
                searchText,
                list
            )
        )
    }

    fun clearSearchData() {
        searchText = ""
        setDefaultArticles()
    }

    private fun fetchArticlesByQuery(query: String) {
        visibilityOfProgress.postValue(true)
        articlesData = articlesDefaultData.filterArticles(query)
        setupMainUiState(articlesData)
        visibilityOfProgress.postValue(false)
    }

    fun onSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_PERIOD)
            fetchArticlesByQuery(query)
            searchText = query
            //TODO Добавить логику получения артиклов из бд
        }
    }

    private fun setDefaultArticles() {
        when {
            (isOnlySearchUsed) -> {
                articlesData = articlesDefaultData
            }

            (isChipChecked && filteredArticlesData.isNotEmpty() && isSearchCategoriesUsed) -> {
                articlesData = filteredArticlesData
            }

            !isChipChecked -> {
                articlesData = articlesDefaultData
            }

            !isSearchCategoriesUsed -> {
                articlesData = articlesDefaultSearch
                isSearchCategoriesUsed = true
            }
        }
        setupMainUiState(articlesData)
    }

    private fun setupMainUiState(list: List<ArticleUiModel>) {
        mainScreenUiState.postValue(
            ArticleMainUiState(
                pressedChips,
                "",
                list
            )
        )
    }

    fun fetchFilteredArticles(filters: List<SubCategoryUiModel>) {
        articlesDefaultSearch = filterArticles(filters, articlesDefaultData)
        isOnlySearchUsed = false
        if (isSearchCategoriesUsed) {
            filteredArticlesData = filterArticles(filters, articlesDefaultData)
            setupMainUiState(filteredArticlesData)
            isSearchCategoriesUsed = false
        } else {
            filteredArticlesData = filterArticles(filters, articlesData)
            setupMainUiState(filteredArticlesData)
        }
        pressedChips = filters
    }

    private fun fetchSubCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            subCategories.postValue(remoteRepository.fetchSubCategories())
        }
    }

    private suspend fun downloadRemoteRepository(): List<ArticleUiModel> {
        isDownloading.postValue(true)
        delay(DEBOUNCE_PERIOD)
        return remoteRepository.fetchArticles().mapToUiModel()
            .also { isDownloading.postValue(false) }
    }

    private suspend fun fetchArticles() {
        articlesData = repository.getArticlesFromRepository()
        articlesDefaultData = articlesData

        setupMainUiState(articlesData)
    }

    fun onChipClick(subCategory: SubCategoryUiModel) {
        val listCheckedChips = mutableListOf<SubCategoryUiModel>()
        listCheckedSubCategories.value?.let { listCheckedChips.addAll(it) }
        when {
            (((listCheckedSubCategories.value?.size ?: ON_NULL) > ON_NULL)
                    && listCheckedChips.contains(subCategory)) -> {
                listCheckedSubCategories.removeItem(subCategory)
                if (listCheckedSubCategories.value.isNullOrEmpty()) {
                    isChipChecked = false
                    setDefaultArticles()
                }
            }

            (!listCheckedChips.contains(subCategory)) -> {
                isChipChecked = true
                listCheckedSubCategories.addItem(subCategory)
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

    fun addNewArticle(newArticle: ArticleDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.uploadArticle(newArticle)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob = null
    }

    companion object {
        const val ON_NULL = 0
        private const val DEBOUNCE_PERIOD: Long = 1000
    }
}
