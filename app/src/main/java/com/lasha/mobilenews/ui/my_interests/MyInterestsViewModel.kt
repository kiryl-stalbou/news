package com.lasha.mobilenews.ui.my_interests

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.util.SUB_CATEGORIES
import com.lasha.mobilenews.util.addItem
import com.lasha.mobilenews.util.getUid
import com.lasha.mobilenews.util.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyInterestsViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val remoteRepository: RemoteRepository,
    private val database: DatabaseReference
) : ViewModel() {

    val subCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val listSavedSubCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val resultSubcategoryList = MutableLiveData<List<SubCategoryUiModel>>()

    val isDataToFirebaseSaved = MutableLiveData<Boolean>()

    val isDataFailed = MutableLiveData<Unit>()

    private val firebaseListOfDataObserver =
        Observer<List<SubCategoryUiModel>> { subCategories ->
            viewModelScope.launch { readFirebaseData(subCategories) }
        }

    init {
        fetchSubCategories()
        subCategories.observeForever(firebaseListOfDataObserver)
    }

    private fun fetchSubCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            subCategories.postValue(remoteRepository.fetchSubCategories())
        }
    }

    private fun readFirebaseData(subCategories: List<SubCategoryUiModel>) {
        val uid = getUid(firebaseAuth)
        if (uid.isNotEmpty()) {
            database.child(uid).child(SUB_CATEGORIES).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userFavoriteSubcategories = (task.result.children.mapNotNull { data ->
                        data.getValue<SubCategoryUiModel>()
                    })
                    listSavedSubCategories.postValue(userFavoriteSubcategories)
                    resultSubcategoryList.postValue(
                        filterListOfSubCategories(
                            userFavoriteSubcategories,
                            subCategories
                        )
                    )
                } else {
                    isDataFailed.postValue(Unit)
                }
            }
        }

    }

    private fun filterListOfSubCategories(
        userFavoriteSubcategories: List<SubCategoryUiModel>,
        subCategories: List<SubCategoryUiModel>
    ): List<SubCategoryUiModel> {
        val favouriteIds = userFavoriteSubcategories.map { it.id }
        val nonFavoriteSubcategories = subCategories.filter {
            it.id !in favouriteIds
        }
        return userFavoriteSubcategories.plus(nonFavoriteSubcategories)
    }

    fun saveDataToFirebase() {
        viewModelScope.launch {
            getUid(firebaseAuth).let {
                database.child(it).child(SUB_CATEGORIES).setValue(
                    listSavedSubCategories.value
                ).addOnSuccessListener {
                    isDataToFirebaseSaved.postValue(true)
                }
                    .addOnFailureListener {
                        isDataToFirebaseSaved.postValue(false)
                    }
            }

        }
    }

    fun onChipClick(subCategory: SubCategoryUiModel) {
        if (subCategory.isFavorite) {
            subCategory.isFavorite = false
            listSavedSubCategories.removeItem(subCategory)
        } else {
            subCategory.isFavorite = true
            listSavedSubCategories.addItem(subCategory)
        }
    }

    override fun onCleared() {
        super.onCleared()
        subCategories.removeObserver(firebaseListOfDataObserver)
    }
}