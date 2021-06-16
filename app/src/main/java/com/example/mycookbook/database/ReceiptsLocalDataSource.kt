package com.example.mycookbook.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReceiptsLocalDataSource internal constructor(
    private val database: ReceiptsDatabaseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReceiptsDataSource {

    override suspend fun insert(receipt: Receipt) = withContext(ioDispatcher)  {
        database.insert(receipt)
    }

    override suspend fun update(receipt: Receipt) = withContext(ioDispatcher)  {
        database.update(receipt)
    }

    override fun GetReceipt(id: Long): LiveData<Receipt> {
        return database.getReceiptById(id)
    }

    override suspend fun deleteReceipt(rec: Receipt) = withContext(ioDispatcher){
        database.delete(rec)
    }

    override suspend fun getReceipts(): LiveData<List<Receipt>> = withContext(ioDispatcher)  {
        return@withContext database.getAllReceipts()
    }

    override fun searchReceipt(searchQuery: String): LiveData<List<Receipt>> {
        return database.getReceiptByName(searchQuery)
    }

}