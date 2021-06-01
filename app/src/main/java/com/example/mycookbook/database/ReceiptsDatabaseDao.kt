package com.example.mycookbook.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReceiptsDatabaseDao {
    @Insert
    suspend fun insert(receipt: Receipt)

    @Update
    suspend fun update(receipt: Receipt)

    @Delete
    suspend fun delete(receipt: Receipt)

    @Query("SELECT * from receipts_table ORDER BY receiptId DESC")
    fun getAllReceipts(): LiveData<List<Receipt>>

    @Query("SELECT * from receipts_table WHERE receiptId = :id")
    fun getReceiptById(id: Long): LiveData<Receipt>

    @Query("SELECT category from receipts_table WHERE receiptId = :id")
    fun getReceiptCategory(id: Long): Char

    @Query("SELECT * from receipts_table WHERE receiptId = :id")
    fun getReceiptToDelete(id: Long): Receipt

    @Query("SELECT * from receipts_table WHERE title LIKE :name")
    fun getReceiptByName(name: String): LiveData<List<Receipt>>
}