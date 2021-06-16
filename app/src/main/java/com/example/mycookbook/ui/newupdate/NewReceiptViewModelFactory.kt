package com.example.mycookbook.ui.newupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycookbook.repository.DefaultReceiptsRepository

class NewReceiptViewModelFactory(
    private val receiptsRepository: DefaultReceiptsRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewReceiptViewModel::class.java)) {
            return NewReceiptViewModel(receiptsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}