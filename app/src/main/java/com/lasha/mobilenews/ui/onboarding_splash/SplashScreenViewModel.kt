package com.lasha.mobilenews.ui.onboarding_splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.util.SUB_CATEGORIES
import com.lasha.mobilenews.util.getUid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) : ViewModel() {

    private val isUserRegistered = MutableLiveData<Boolean>()

    val listOfDataFromFirebase = MutableLiveData<MutableList<SubCategoryUiModel>>()

    val isNavigateToViewPagerNeeded = MutableLiveData<Unit>()

    val isDataFailed = MutableLiveData<Unit>()

    private val firebaseDataObserver = Observer<Boolean> { isUserRegistered ->
        if (isUserRegistered) {
            viewModelScope.launch { readFirebaseData() }
        } else {
            isNavigateToViewPagerNeeded.value = Unit
        }
    }

    init {
        isUserRegistered.observeForever(firebaseDataObserver)
    }

    fun splashDelay() = viewModelScope.launch {
        delay(1000L)
        isUserRegistered.postValue(auth.currentUser != null)
    }

    private suspend fun readFirebaseData() {
        withContext(viewModelScope.coroutineContext) {
            getUid(firebaseAuth).let {
                databaseReference.child(it).child(SUB_CATEGORIES).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            listOfDataFromFirebase.postValue(task.result.children.mapNotNull { data ->
                                data.getValue<SubCategoryUiModel>()
                            } as MutableList<SubCategoryUiModel>)
                        } else {
                            isDataFailed.postValue(Unit)
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isUserRegistered.removeObserver(firebaseDataObserver)
    }
}