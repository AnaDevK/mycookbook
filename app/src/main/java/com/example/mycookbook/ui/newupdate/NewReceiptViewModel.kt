package com.example.mycookbook.ui.newupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.repository.ReceiptsRepository
import kotlinx.coroutines.launch

class NewReceiptViewModel(private val receiptsRepository: ReceiptsRepository) : ViewModel() {

    fun insertReceipt(receipt: Receipt) {
        viewModelScope.launch {
            receiptsRepository.insertReceipt(receipt)
        }
    }

    fun updateReceipt(receipt: Receipt) {
        viewModelScope.launch {
            receiptsRepository.updateReceipt(receipt)
        }
    }

    fun getReceipt(id: Long): LiveData<Receipt> {
        return receiptsRepository.getReceiptbyId(id)
    }
}