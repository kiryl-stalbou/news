package com.lasha.mobilenews.data

import com.lasha.mobilenews.data.dataModel.ArticleDataModel
import com.lasha.mobilenews.data.dataModel.MainCategoryDataModel
import com.lasha.mobilenews.data.dataModel.SubCategoryDataModel
import com.lasha.mobilenews.data.repository.RemoteRepositoryImpl
import com.lasha.mobilenews.domain.service.RemoteService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@MockKExtension.ConfirmVerification
class MobileNewsRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var remoteService: RemoteService

    private lateinit var remoteRepositoryImpl: RemoteRepositoryImpl

    @Before
    fun setUp() {
        remoteRepositoryImpl = RemoteRepositoryImpl(remoteService)
    }

    @Test
    fun `test fetchCategories with an empty list`() = runTest {
        //Given
        val expectedList = listOf<MainCategoryDataModel>()
        coEvery {
            remoteService.fetchCategories()
        } returns expectedList

        //When
        val actualResult = remoteRepositoryImpl.fetchCategories()

        //Then
        assertEquals(expectedList, actualResult)
        coVerify(exactly = 1) {
            remoteService.fetchCategories()
        }
    }

    @Test
    fun `test fetchSubCategories with an empty list`() = runTest {
        //Given
        val expectedList = listOf<SubCategoryDataModel>()
        coEvery {
            remoteService.fetchSubCategories()
        } returns expectedList

        //When
        val actualResult = remoteRepositoryImpl.fetchSubCategories()

        //Then
        assertEquals(expectedList, actualResult)
        coVerify(exactly = 1) {
            remoteService.fetchSubCategories()
        }
    }

    @Test
    fun `test fetchFilteredSubCategories with an empty list`() = runTest {
        //Given
        val mainCategoryDataModel = mockk<MainCategoryDataModel>()
        val expectedList = listOf<SubCategoryDataModel>()
        coEvery {
            remoteService.fetchFilteredSubCategories(mainCategoryDataModel)
        } returns expectedList

        //When
        val actualResult = remoteService.fetchFilteredSubCategories(mainCategoryDataModel)

        //Then
        assertEquals(expectedList, actualResult)
        coVerify(exactly = 1) {
            remoteService.fetchFilteredSubCategories(mainCategoryDataModel)
        }
    }

    @Test
    fun `test fetchArticles with an empty list`() = runTest {
        //Given
        val expectedList = listOf<ArticleDataModel>()
        coEvery {
            remoteService.fetchArticles()
        } returns expectedList

        //When
        val actualResult = remoteRepositoryImpl.fetchArticles()

        //Then
        assertEquals(expectedList, actualResult)
        coVerify(exactly = 1) {
            remoteService.fetchArticles()
        }
    }

    @Test
    fun `test fetchArticlesByCategoryAndSubCategory with an empty list`() = runTest {
        //Given
        val subCategoryDataModel = mockk<SubCategoryDataModel>()
        val mainCategoryDataModel = mockk<MainCategoryDataModel>()
        val expectedList = listOf<ArticleDataModel>()
        coEvery {
            remoteService.fetchArticlesByCategoryAndSubCategory(
                subCategoryDataModel,
                mainCategoryDataModel
            )
        } returns expectedList

        //When
        val actualResult = remoteService.fetchArticlesByCategoryAndSubCategory(
            subCategoryDataModel,
            mainCategoryDataModel
        )

        //Then
        assertEquals(expectedList, actualResult)
        coVerify(exactly = 1) {
            remoteService.fetchArticlesByCategoryAndSubCategory(
                subCategoryDataModel,
                mainCategoryDataModel
            )
        }
    }

    @After
    fun clear() {
        unmockkAll()
    }
}