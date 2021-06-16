package com.example.mycookbook.ui.newupdate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mycookbook.MainCoroutineRule
import com.example.mycookbook.database.FakeTestRepository
import com.example.mycookbook.getOrAwaitValue
import com.example.mycookbook.getReceiptsLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewReceiptViewModelTest {
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
    fun addNewReceipt() = runBlockingTest {
        // Given a fresh NewReceiptViewModel
        val newReceiptViewModel = NewReceiptViewModel(receiptsRepository)
        // When getting a receipt
        val value = newReceiptViewModel.getReceipt(1).getOrAwaitValue()

        // Then the receipt event is triggered
        assertThat(value, not(nullValue()))
        assertThat(value.receiptTitle, `is`("Test receipt1"))
    }
}