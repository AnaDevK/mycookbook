package com.example.mycookbook.repository

import androidx.lifecycle.LiveData
import com.example.mycookbook.database.Receipt

interface ReceiptsRepository {
    suspend fun insertReceipt(receipt: Receipt)

    suspend fun updateReceipt(receipt: Receipt)

    fun getReceiptbyId(id: Long): LiveData<Receipt>

    suspend fun getAllReceipts(): LiveData<List<Receipt>>

    suspend fun deleteReceipt(rec: Receipt)

    fun searchReceipt(searchQuery: String): LiveData<List<Receipt>>
}