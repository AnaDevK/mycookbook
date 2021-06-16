package com.example.mycookbook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.mycookbook.getReceiptsLiveData
import com.example.mycookbook.repository.ReceiptsRepository

class FakeTestRepository(val receipts: LiveData<List<Receipt>>): ReceiptsRepository {

    override suspend fun insertReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override fun getReceiptbyId(id: Long): LiveData<Receipt> {
        val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes1", "recurl1.com",null, null)
        val recLd = MutableLiveData<Receipt>()
        recLd.value = receipt
        return recLd
    }

    override suspend fun getAllReceipts(): LiveData<List<Receipt>> {
        return getReceiptsLiveData()
    }

    override suspend fun deleteReceipt(rec: Receipt) {
        TODO("Not yet implemented")
    }

    override fun searchReceipt(searchQuery: String): LiveData<List<Receipt>> {
        return receipts.map {
            it.filter { it.receiptTitle.equals(searchQuery) }
        }
    }
}