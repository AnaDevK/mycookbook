package com.example.mycookbook.ui.overview

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycookbook.database.ReceiptsDatabaseDao

class AllReceiptsViewModelFactory(
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllReceiptsViewModel::class.java)) {
            return AllReceiptsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}