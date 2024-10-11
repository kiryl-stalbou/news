package com.lasha.mobilenews.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.lasha.mobilenews.di.utils.ArticlesReference
import com.lasha.mobilenews.di.utils.CategoriesReference
import com.lasha.mobilenews.di.utils.SubCategoriesReference
import com.lasha.mobilenews.domain.repository.LocalRepository
import com.lasha.mobilenews.domain.repository.RemoteRepository
import com.lasha.mobilenews.domain.repository.Repository
import com.lasha.mobilenews.ui.common.ViewModelFactory
import com.lasha.mobilenews.util.ID_TOKEN
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MobileNewsModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideViewModelFactory(
        sharedPreferences: SharedPreferences,
        firebaseAuth: FirebaseAuth,
        remoteRepository: RemoteRepository,
        databaseReference: DatabaseReference,
        uid: String,
        localRepository: LocalRepository,
        repository: Repository
    ): ViewModelProvider.Factory =
        ViewModelFactory(
            sharedPref = sharedPreferences,
            firebaseAuth = firebaseAuth,
            remoteRepository = remoteRepository,
            database = databaseReference,
            uid = uid,
            localRepository = localRepository,
            repository = repository
        )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideUsersDatabaseReference(): DatabaseReference =
        Firebase.database.getReference(DATABASE_KEY)

    @Provides
    @Singleton
    @ArticlesReference
    fun provideArticlesDatabaseReference(): DatabaseReference =
        Firebase.database.getReference(ARTICLES_KEY)

    @Provides
    @Singleton
    @CategoriesReference
    fun provideCategoriesDatabaseReference(): DatabaseReference =
        Firebase.database.getReference(CATEGORIES_KEY)

    @Provides
    @Singleton
    @SubCategoriesReference
    fun provideSubCategoriesDatabaseReference(): DatabaseReference =
        Firebase.database.getReference(SUBCATEGORIES_KEY)

    @Provides
    @Singleton
    fun provideGso(): GoogleSignInOptions = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(ID_TOKEN)
        .requestEmail()
        .build()

    @Provides
    @Singleton
    fun provideUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    companion object {
        private const val DATABASE_KEY = "Users"
        private const val ARTICLES_KEY = "Articles"
        private const val CATEGORIES_KEY = "Categories"
        private const val SUBCATEGORIES_KEY = "SubCategories"
    }
}