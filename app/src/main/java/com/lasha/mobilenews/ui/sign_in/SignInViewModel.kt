package com.lasha.mobilenews.ui.sign_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) : ViewModel() {

    private val isAuthSuccessful = MutableLiveData<Boolean>()

    val listOfDataFromFirebase = MutableLiveData<List<SubCategoryUiModel>>()

    val isNavigateToBottomSheetNeeded = MutableLiveData<Unit>()

    val isDataFailed = MutableLiveData<Unit>()

    private val firebaseDataObserver = Observer<Boolean> { isAuthSuccessful ->
        if (isAuthSuccessful) {
            viewModelScope.launch { readFirebaseData() }
        } else {
            isNavigateToBottomSheetNeeded.value = Unit
        }
    }

    init {
        isAuthSuccessful.observeForever(firebaseDataObserver)
    }

    override fun onCleared() {
        super.onCleared()
        isAuthSuccessful.removeObserver(firebaseDataObserver)
    }

    private suspend fun readFirebaseData() {
        withContext(viewModelScope.coroutineContext) {
            firebaseAuth.uid?.let {
                databaseReference.child(it).child(SUB_CATEGORIES).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            listOfDataFromFirebase.postValue(task.result.children.mapNotNull { data ->
                                data.getValue<SubCategoryUiModel>()
                            })
                        } else {
                            isDataFailed.postValue(Unit)
                        }
                    }
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                isAuthSuccessful.postValue(it.isSuccessful)
            }
        }
    }

    companion object {
        const val SUB_CATEGORIES = "subCategories"
    }
}