package com.example.mycookbook.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.repository.ReceiptsRepository
import com.example.mycookbook.utils.setReceiptCategory
import kotlinx.coroutines.launch

class DetailReceiptViewModel(receiptId: Long, private val receiptsRepository: ReceiptsRepository) : ViewModel() {
    lateinit var receipt: LiveData<Receipt>

    init {
        viewModelScope.launch {
            receipt =
                receiptsRepository.getReceiptbyId(receiptId)
        }
    }

    fun delete(rec: Receipt) {
        viewModelScope.launch {
            receiptsRepository.deleteReceipt(rec)
        }
    }

    val displayCategory = Transformations.map(receipt) {
        setReceiptCategory(it.receiptCategory?:'O')
    }

}