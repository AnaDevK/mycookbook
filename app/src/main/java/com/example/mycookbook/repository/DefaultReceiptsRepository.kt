package com.example.mycookbook.repository

import androidx.lifecycle.LiveData
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.database.ReceiptsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DefaultReceiptsRepository(private val receiptsLocalDataSource: ReceiptsDataSource,
                                private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): ReceiptsRepository{

    override suspend fun insertReceipt(receipt: Receipt) {
        coroutineScope {
            launch {
                receiptsLocalDataSource.insert(receipt)
            }
        }
    }

    override suspend fun updateReceipt(receipt: Receipt) {
        coroutineScope {
            launch {
                receiptsLocalDataSource.update(receipt)
            }
        }
    }

    override fun getReceiptbyId(id: Long): LiveData<Receipt>{
        return receiptsLocalDataSource.GetReceipt(id)
    }

    override suspend fun getAllReceipts(): LiveData<List<Receipt>>{
        return receiptsLocalDataSource.getReceipts()
    }

    override suspend fun deleteReceipt(rec: Receipt){
        coroutineScope {
            launch {
                receiptsLocalDataSource.deleteReceipt(rec)
            }
        }
    }

    override fun searchReceipt(searchQuery: String): LiveData<List<Receipt>>{
       return receiptsLocalDataSource.searchReceipt(searchQuery)
    }
}