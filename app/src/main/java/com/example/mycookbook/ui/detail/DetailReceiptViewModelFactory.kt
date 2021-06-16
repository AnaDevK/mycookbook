package com.example.mycookbook.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycookbook.repository.DefaultReceiptsRepository

class DetailReceiptViewModelFactory(
    private val selectedReceiptId: Long,
    private val receiptsRepository: DefaultReceiptsRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailReceiptViewModel::class.java)) {
            return DetailReceiptViewModel(selectedReceiptId, receiptsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}