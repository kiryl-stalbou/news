package com.lasha.mobilenews.ui.list_interests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.util.SUB_CATEGORIES
import com.lasha.mobilenews.util.addItem
import com.lasha.mobilenews.util.getUid
import com.lasha.mobilenews.util.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListInterestsViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val remoteRepository: RemoteRepository,
    private val database: DatabaseReference
) : ViewModel() {

    val subCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val listSavedSubCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val isDataToFirebaseSaved = MutableLiveData<Boolean>()

    init {
        fetchSubCategories()
    }

    private fun fetchSubCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            subCategories.postValue(remoteRepository.fetchSubCategories())
        }
    }

    fun saveDataToFirebase() {
        val uid = getUid(firebaseAuth)
        if (uid.isNotEmpty()) {
            database.child(uid).child(SUB_CATEGORIES).setValue(
                listSavedSubCategories.value
            ).addOnSuccessListener {
                isDataToFirebaseSaved.postValue(true)
            }
                .addOnFailureListener {
                    isDataToFirebaseSaved.postValue(false)
                }
        }
    }

    fun onChipClick(subCategory: SubCategoryUiModel) {
        if (!subCategory.isFavorite) {
            subCategory.isFavorite = true
            listSavedSubCategories.addItem(subCategory)
        } else {
            subCategory.isFavorite = false
            listSavedSubCategories.removeItem(subCategory)
        }
    }
}