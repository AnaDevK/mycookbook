package com.example.mycookbook.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mycookbook.MainCoroutineRule
import com.example.mycookbook.getOrAwaitValue
import com.example.mycookbook.repository.DefaultReceiptsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultReceiptsRepositoryTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val receipt1 = Receipt(1, "Test receipt1", 'D', "Receipt notes1", "recurl1.com",null, null)
    val receipt2 = Receipt(2, "Test receipt2", 'D', "Receipt notes2", "recurl2.com",null, null)
    val receipt3 = Receipt(3, "Test receipt3", 'D', "Receipt notes3", "recurl3.com",null, null)

    private val localReceipts = listOf(receipt1, receipt2, receipt3)
    private lateinit var receiptsLocalDataSource: FakeDataSource
    private var receipts = MutableLiveData<List<Receipt>>()
    //Class under test
    private lateinit var receiptsRepository: DefaultReceiptsRepository

    @Before
    fun createRepository() {
        receipts.value = localReceipts
        receiptsLocalDataSource = FakeDataSource(receipts)
        receiptsRepository = DefaultReceiptsRepository(receiptsLocalDataSource, Dispatchers.Main)
    }

    @Test
    fun getReceipts_requestAllReceiptsFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
        val allReceipts = receiptsRepository.getAllReceipts()
        assertThat(allReceipts, notNullValue())
        val receiptsValue = allReceipts.getOrAwaitValue()
        assertThat(receiptsValue.size, `is`(3))
    }

    @Test
    fun getReceiptById_requestReceiptFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
        val receipt1 = receiptsRepository.getReceiptbyId(1)
        val receiptValue1 = receipt1.getOrAwaitValue()
        assertThat(receiptValue1.receiptTitle, `is`("Test receipt1"))

        val receipt2 = receiptsRepository.getReceiptbyId(2)
        val receiptValue2 = receipt2.getOrAwaitValue()
        assertThat(receiptValue2.receiptTitle, `is`("Test receipt2"))

        val receipt3 = receiptsRepository.getReceiptbyId(3)
        val receiptValue3 = receipt3.getOrAwaitValue()
        assertThat(receiptValue3.receiptTitle, `is`("Test receipt3"))
    }



}