package com.example.mycookbook.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailReceiptViewModelFactory(
    private val selectedReceiptId: Long,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailReceiptViewModel::class.java)) {
            return DetailReceiptViewModel(selectedReceiptId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}