package com.lasha.mobilenews.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lasha.mobilenews.ui.models.ProfilePageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val isUserLoggedOut = MutableLiveData<Unit>()

    val userData = MutableLiveData<ProfilePageData>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userData.postValue(
                ProfilePageData(
                    firebaseAuth.currentUser?.email.toString(),
                    firebaseAuth.currentUser?.displayName.toString(),
                    firebaseAuth.currentUser?.photoUrl.toString()
                )
            )
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            isUserLoggedOut.postValue(Unit)
        }
    }
}