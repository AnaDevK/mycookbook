package com.example.mycookbook.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycookbook.repository.DefaultReceiptsRepository

class AllReceiptsViewModelFactory(
    private val receiptsRepository: DefaultReceiptsRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllReceiptsViewModel::class.java)) {
            return AllReceiptsViewModel(receiptsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}