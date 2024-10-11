package com.lasha.mobilenews.ui.subctegories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.domain.repository.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubcategoriesViewModel(
    private val remoteRepository: RemoteRepository,
) : ViewModel() {

    val subCategories = MutableLiveData<List<SubCategoryUiModel>>()

    val clickedChip = MutableLiveData<SubCategoryUiModel>()

    fun fetchSubCategories(category: MainCategoryUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            subCategories.postValue(remoteRepository.fetchFilteredSubCategories(category))
        }
    }

    fun onChipClick(subCategory: SubCategoryUiModel) {
        clickedChip.postValue(subCategory)
    }
}