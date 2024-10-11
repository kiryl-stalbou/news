package com.lasha.mobilenews.ui.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val remoteRepository: RemoteRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val categories = MutableLiveData<List<MainCategoryUiModel>>()

    val mainCategory = MutableLiveData<MainCategoryUiModel>()

    val userData = MutableLiveData<String>()

    init {
        userData.postValue(
            firebaseAuth.currentUser?.photoUrl.toString()
        )
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categories.postValue(remoteRepository.fetchCategories())
        }
    }

    fun onChipClick(mainCategory: MainCategoryUiModel) {
        this.mainCategory.postValue(mainCategory)
    }
}