package com.lasha.mobilenews.ui

import com.lasha.mobilenews.ui.base.BaseViewModelTest
import com.lasha.mobilenews.ui.articles_filtered.ArticleFilteredViewModel
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.utils.getOrAwaitValue
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@MockKExtension.ConfirmVerification
@OptIn(ExperimentalCoroutinesApi::class)
class ArticlesFilteredViewModelTest : BaseViewModelTest() {

    private lateinit var articlesFilteredViewModel: ArticleFilteredViewModel

    @Before
    fun init() {
        articlesFilteredViewModel = spyk(
            ArticleFilteredViewModel(
                remoteRepository,
                databaseReference,
                firebaseAuth
            )
        )
    }

    @Test
    fun `test fetchFilteredArticles with an empty list`() = runTest {
        //Given
        val subCategoryUiModel = mockk<SubCategoryUiModel>()
        val mainCategoryUiModel = mockk<MainCategoryUiModel>()
        val expectedList = listOf<ArticleUiModel>()
        coEvery {
            remoteRepository.fetchArticlesByCategoryAndSubCategory(
                subCategoryUiModel,
                mainCategoryUiModel
            )
        } returns expectedList

        //When
        articlesFilteredViewModel.fetchFilteredArticles(subCategoryUiModel, mainCategoryUiModel)

        //Then
        val actualResult = articlesFilteredViewModel.articlesData.getOrAwaitValue()
        assertEquals(expectedList, actualResult)
        coVerify {
            remoteRepository.fetchArticlesByCategoryAndSubCategory(
                subCategoryUiModel,
                mainCategoryUiModel
            )
        }
    }

    @After
    fun clear() {
        unmockkAll()
    }
}