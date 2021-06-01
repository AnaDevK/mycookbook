package com.example.mycookbook.ui.newupdate

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewReceiptViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewReceiptViewModel::class.java)) {
            return NewReceiptViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}