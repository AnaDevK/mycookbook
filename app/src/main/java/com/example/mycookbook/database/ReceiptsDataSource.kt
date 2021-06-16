package com.example.mycookbook.database

import androidx.lifecycle.LiveData

interface ReceiptsDataSource {
    suspend fun insert(receipt: Receipt)

    suspend fun update(receipt: Receipt)

    fun GetReceipt(id: Long): LiveData<Receipt>

    suspend fun getReceipts(): LiveData<List<Receipt>>

    suspend fun deleteReceipt(rec: Receipt)

    fun searchReceipt(searchQuery: String): LiveData<List<Receipt>>
}