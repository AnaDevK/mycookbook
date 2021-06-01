package com.example.mycookbook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts_table")
//@TypeConverters(Converters::class)
data class Receipt(
        @PrimaryKey(autoGenerate = true)
        var receiptId: Long = 0L,
        @ColumnInfo(name = "title")
        var receiptTitle: String = "Test receipt",
        @ColumnInfo(name = "category")
        var receiptCategory: Char? = '\u0000',
        @ColumnInfo(name = "notes")
        var receiptNotes: String? = null,
        @ColumnInfo(name = "url")
        var receiptUrl: String? = null,
        @ColumnInfo(name = "image1")
        var receiptImageUri1: String? = null,
        @ColumnInfo(name = "images")
        var images: String? = null
)