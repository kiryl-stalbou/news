package com.lasha.mobilenews.util

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.ItemCategoryChipBinding
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable

fun Fragment.navigate(id: Int, bundle: Bundle? = null) =
    NavHostFragment.findNavController(this).navigate(id, bundle)

fun Fragment.navigate(navDirections: NavDirections) =
    NavHostFragment.findNavController(this).navigate(navDirections)

fun <T> MutableLiveData<List<T>>.addItem(item: T) {
    val updatedItems = this.value ?: mutableListOf()
    (updatedItems as MutableList).add(item)
    this.value = updatedItems
}

fun <T> MutableLiveData<List<T>>.removeItem(item: T) {
    val updatedItems = this.value ?: mutableListOf()
    (updatedItems as MutableList).remove(item)
    this.value = updatedItems
}

fun List<ArticleUiModel>.filterArticles(query: String): List<ArticleUiModel> {
    val updatedItems = this
    return updatedItems.filter {
        it.title.contains(query, ignoreCase = true)
    }
}fun MutableLiveData<List<ArticleUiModel>>.filterArticles(query: String): List<ArticleUiModel> {
    val updatedItems = this.value ?: emptyList()
    return updatedItems.filter {
        it.title.contains(query, ignoreCase = true)
    }
}

fun filterArticles(
    filters: List<SubCategoryUiModel>,
    articlesData: List<ArticleUiModel>
): MutableList<ArticleUiModel> {
    val filteredList = mutableListOf<ArticleUiModel>()
    for (element in filters) {
        articlesData.filter { it.subcategory.first() == element.title }
            .let {
                it.forEach { item ->
                    if (!filteredList.contains(item)) {
                        filteredList.add(item)
                    }
                }
            }
    }
    return filteredList
}

fun addChips(
    categoriesList: List<CategoryUiModel>,
    viewGroup: ViewGroup,
    layoutInflater: LayoutInflater,
    onChipClickListener: (CategoryUiModel) -> Unit
) {
    if (categoriesList.isNotEmpty()) {
        for (element in categoriesList) {
            viewGroup.addView(
                createChip(
                    element,
                    layoutInflater
                ) { onChipClickListener.invoke(element) }
            )
        }
    }
}

fun createChip(
    category: CategoryUiModel,
    inflater: LayoutInflater,
    onChipClickListener: (CategoryUiModel) -> Unit
): Chip {
    val field = ItemCategoryChipBinding
        .inflate(inflater)
    val categoryChip = field.categoryChip
    categoryChip.text = category.title
    categoryChip.id = category.id
    categoryChip.setOnClickListener {
        onChipClickListener.invoke(category)
    }
    if (category is SubCategoryUiModel) {
        if (category.isFavorite) {
            categoryChip.isChecked = true
        }
    }
    return categoryChip
}

fun ImageView.loadUrlImage(url: String) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .into(this)
}

fun ImageView.loadUrlProfileImage(url: String) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .error(R.drawable.ic_user_default)
        .into(this)
}

fun fetchCombinedArticles(
    remoteItems: List<ArticleUiModel>,
    remoteSavedItems: List<ArticleUiModel>,
): List<ArticleUiModel> {
    val combinedArticles = mutableListOf<ArticleUiModel>()
    combinedArticles.addAll(remoteItems)
    for (remoteItemIndex in remoteItems.indices) {
        for (remoteSavedItem in remoteSavedItems) {
            if (remoteSavedItem.id == remoteItems[remoteItemIndex].id) {
                combinedArticles[remoteItemIndex] =
                    remoteItems[remoteItemIndex].copy(
                        bookmarked = remoteSavedItem.bookmarked,
                        liked = remoteSavedItem.liked,
                        disliked = remoteSavedItem.disliked
                    )
            }
        }
    }
    return combinedArticles
}

fun getCurrentFragmentId(activity: MainActivity): Int {
    val navController: NavController =
        Navigation.findNavController(activity, R.id.nav_host_fragment)
    return navController.currentDestination?.id ?: 0
}

inline fun <reified T : Serializable> Bundle.getSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

suspend fun handleOnLikeClicked(
    likeClick: OnLikeClick,
    uid: String?,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    if (!uid.isNullOrEmpty() && likeClick.isLikeClicked) {
        saveLikedDataToRemote(likeClick, uid, databaseReference, remoteRepository)
    } else if (!uid.isNullOrEmpty() && !likeClick.isLikeClicked) {
        removeLikedDataFromRemote(likeClick, uid, databaseReference, remoteRepository)
    }
}

private suspend fun removeLikedDataFromRemote(
    likeClick: OnLikeClick,
    uid: String,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    val articleToSave = likeClick.model.copy(liked = false)
    databaseReference.child(uid).child(ARTICLES).child(likeClick.model.id.toString())
        .setValue(articleToSave)
    remoteRepository.onLikeArticleClickAction(
        likeClick.model.id,
        likeClick.isLikeClicked,
        likeClick.isDislikeClicked
    )
}

private suspend fun saveLikedDataToRemote(
    likeClick: OnLikeClick,
    uid: String,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    val articleToSave = likeClick.model.copy(liked = true, disliked = false)
    databaseReference.child(uid).child(ARTICLES).child(likeClick.model.id.toString())
        .setValue(articleToSave)
    remoteRepository.onLikeArticleClickAction(
        likeClick.model.id,
        likeClick.isLikeClicked,
        likeClick.isDislikeClicked
    )
}

suspend fun handleOnDislikeClicked(
    dislikeClick: OnLikeClick,
    uid: String?,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    if (!uid.isNullOrEmpty() && dislikeClick.isDislikeClicked) {
        saveDislikedDataToRemote(dislikeClick, uid, databaseReference, remoteRepository)
    } else if (!uid.isNullOrEmpty() && !dislikeClick.isDislikeClicked) {
        removeDislikedDataFromRemote(dislikeClick, uid, databaseReference, remoteRepository)
    }
}

private suspend fun saveDislikedDataToRemote(
    dislikeClick: OnLikeClick,
    uid: String,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    val articleToSave = dislikeClick.model.copy(disliked = true, liked = false)
    databaseReference.child(uid).child(ARTICLES).child(dislikeClick.model.id.toString())
        .setValue(articleToSave)
    remoteRepository.onDisLikeArticleClickAction(
        dislikeClick.model.id,
        dislikeClick.isDislikeClicked,
        dislikeClick.isLikeClicked
    )
}

private suspend fun removeDislikedDataFromRemote(
    dislikeClick: OnLikeClick,
    uid: String,
    databaseReference: DatabaseReference,
    remoteRepository: RemoteRepository
) {
    val articleToSave = dislikeClick.model.copy(disliked = false)
    databaseReference.child(uid).child(ARTICLES).child(dislikeClick.model.id.toString())
        .setValue(articleToSave)
    remoteRepository.onDisLikeArticleClickAction(
        dislikeClick.model.id,
        dislikeClick.isDislikeClicked,
        dislikeClick.isLikeClicked
    )
}

suspend fun handleOnBookmarkClick(
    articleData: OnBookmarkClick,
    uid: String?,
    databaseReference: DatabaseReference
) {
    withContext(Dispatchers.IO) {
        if (!uid.isNullOrEmpty() && articleData.isBookmarked) {
            saveBookmarkedDataToRemote(articleData, uid, databaseReference)
        } else if (!uid.isNullOrEmpty() && !articleData.isBookmarked) {
            removeBookmarkedDataFromRemote(articleData, uid, databaseReference)
        }
    }
}

private fun removeBookmarkedDataFromRemote(
    articleData: OnBookmarkClick,
    uid: String,
    databaseReference: DatabaseReference
) {
    databaseReference.child(uid).child(ARTICLES).child(articleData.model.id.toString())
        .removeValue()
}

private fun saveBookmarkedDataToRemote(
    articleData: OnBookmarkClick,
    uid: String,
    databaseReference: DatabaseReference
) {
    val articleToSave = articleData.model.copy(bookmarked = true)
    databaseReference.child(uid).child(ARTICLES).child(articleData.model.id.toString())
        .setValue(articleToSave)
}

fun getUid(firebaseAuth: FirebaseAuth): String {
    return if (!firebaseAuth.uid.isNullOrEmpty()) {
        firebaseAuth.uid!!
    } else {
        ""
    }
}