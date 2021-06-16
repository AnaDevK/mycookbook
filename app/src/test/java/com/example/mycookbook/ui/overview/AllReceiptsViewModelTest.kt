package com.example.mycookbook.ui.overview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mycookbook.MainCoroutineRule
import com.example.mycookbook.database.FakeTestRepository
import com.example.mycookbook.getOrAwaitValue
import com.example.mycookbook.getReceiptsLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AllReceiptsViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var receiptsRepository: FakeTestRepository

    @Before
    fun createRepository() {
        val receiptsLiveData = getReceiptsLiveData()
        receiptsRepository = FakeTestRepository(receiptsLiveData)
    }

    @Test
    fun getReceipts() = runBlockingTest {
        // Given a fresh NewReceiptViewModel
        val allReceiptViewModel = AllReceiptsViewModel(receiptsRepository)
        // When deleting a receipt
        val value = allReceiptViewModel.receipts?.getOrAwaitValue()

        // Then the receipt event is triggered
        MatcherAssert.assertThat(value, not(nullValue()))
        MatcherAssert.assertThat(value?.size, `is`(3))
    }
}