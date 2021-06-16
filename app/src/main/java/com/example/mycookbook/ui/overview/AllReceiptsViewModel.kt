package com.example.mycookbook.ui.overview

import androidx.lifecycle.*
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.repository.ReceiptsRepository
import com.example.mycookbook.utils.getReceiptCategory
import kotlinx.coroutines.launch

class AllReceiptsViewModel(private val receiptsRepository: ReceiptsRepository) : ViewModel() {


    var receipts: LiveData<List<Receipt>>? = null
    var searchReceiptsByCategory: LiveData<List<Receipt>>? = null

    private val _navigateToSelectedReceipt = MutableLiveData<Receipt?>()
    val navigateToSelectedReceipt: MutableLiveData<Receipt?>
        get() = _navigateToSelectedReceipt

    init {
        getReceipts()
    }

    private fun getReceipts() {
        viewModelScope.launch {
            receipts = receiptsRepository.getAllReceipts()
        }
    }

    fun displayReceiptDetails(receipt: Receipt) {
        _navigateToSelectedReceipt.value = receipt
    }

    fun displayReceiptDetailsComplete() {
        _navigateToSelectedReceipt.value = null
    }

    private fun filterReceiptsByCategory(category: Char): LiveData<List<Receipt>> {
        val filteredReceipts = receipts
        return filteredReceipts?.map {
            it.filter { it.receiptCategory!!.equals(category) }
        }!!
    }

    private fun searchReceipt(searchQuery: String): LiveData<List<Receipt>> {
        return receiptsRepository.searchReceipt(searchQuery)
    }

    fun searchReceiptByName(searchQuery: String): LiveData<List<Receipt>> {
        return searchReceipt(searchQuery)
    }

    fun searchReceiptByCategory(position: Int): LiveData<List<Receipt>>? {
        if (position == 0) {
            searchReceiptsByCategory = receipts
        } else {
            val selection = getReceiptCategory(position)
            searchReceiptsByCategory = filterReceiptsByCategory(selection)
        }
        return searchReceiptsByCategory
    }
}