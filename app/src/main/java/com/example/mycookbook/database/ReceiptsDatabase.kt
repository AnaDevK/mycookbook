package com.example.mycookbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Receipt::class], version = 6, exportSchema = false)
abstract class ReceiptsDatabase : RoomDatabase() {

    abstract val receiptsDatabaseDao: ReceiptsDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: ReceiptsDatabase? = null

        fun getInstance(context: Context): ReceiptsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReceiptsDatabase::class.java,
                        "receipts_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}