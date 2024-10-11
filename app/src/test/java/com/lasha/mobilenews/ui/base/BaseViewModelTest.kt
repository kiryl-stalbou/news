package com.lasha.mobilenews.ui.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lasha.mobilenews.utils.TestViewModelScopeRule
import com.lasha.mobilenews.domain.repository.RemoteRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Rule

open class BaseViewModelTest {

    @MockK
    lateinit var remoteRepository: RemoteRepository

    @MockK
    lateinit var databaseReference: DatabaseReference

    @MockK
    lateinit var firebaseAuth: FirebaseAuth

    @get:Rule
    val testViewModelScopeRule = TestViewModelScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)
}