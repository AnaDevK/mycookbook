package com.example.mycookbook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeDataSource(var receipts: LiveData<List<Receipt>>) : ReceiptsDataSource {
    override suspend fun insert(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override suspend fun update(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override fun GetReceipt(id: Long): LiveData<Receipt> {
        val receipt1 = MutableLiveData<Receipt>()
        val receipt2 = MutableLiveData<Receipt>()
        val receipt3 = MutableLiveData<Receipt>()
        receipt1.value =
            Receipt(1, "Test receipt1", 'D', "Receipt notes1", "recurl1.com", null, null)
        receipt2.value =
            Receipt(2, "Test receipt2", 'D', "Receipt notes2", "recurl2.com", null, null)
        receipt3.value =
            Receipt(3, "Test receipt3", 'D', "Receipt notes3", "recurl3.com", null, null)

        when (id) {
            1.toLong() -> return receipt1
            2.toLong() -> return receipt2
            else -> return receipt3
        }
    }

    override suspend fun getReceipts(): LiveData<List<Receipt>> {
        return receipts
    }

    override suspend fun deleteReceipt(rec: Receipt) {
        TODO("Not yet implemented")
    }

    override fun searchReceipt(searchQuery: String): LiveData<List<Receipt>> {
        TODO("Not yet implemented")
    }

}