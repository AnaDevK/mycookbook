package com.example.mycookbook.ui.overview

import android.app.Application
import androidx.lifecycle.*
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.database.ReceiptsDatabase
import com.example.mycookbook.utils.getReceiptCategory
import kotlinx.coroutines.launch

class AllReceiptsViewModel(app: Application) : ViewModel() {


    lateinit var receipts: LiveData<List<Receipt>>
    var searchReceiptsByName: LiveData<List<Receipt>>? = null
    var searchReceiptsByCategory: LiveData<List<Receipt>>? = null

    private val _navigateToSelectedReceipt = MutableLiveData<Receipt>()
    val navigateToSelectedReceipt: LiveData<Receipt>
        get() = _navigateToSelectedReceipt

    val database = ReceiptsDatabase.getInstance(app).receiptsDatabaseDao

    init {
        getReceipts()
    }

    private fun getReceipts() {
        viewModelScope.launch {
            receipts = database.getAllReceipts()
        }
    }


    fun displayReceiptDetails(receipt: Receipt) {
        _navigateToSelectedReceipt.value = receipt
    }

    fun displayReceiptDetailsComplete() {
        _navigateToSelectedReceipt.value = null
    }

    fun filterReceiptsByCategory(category: Char): LiveData<List<Receipt>> {
        val filteredReceipts = receipts
        return filteredReceipts.map {
            it.filter { it.receiptCategory!!.equals(category) }
        }
    }

    private fun searchReceipt(searchQuery: String): LiveData<List<Receipt>> {
        return database.getReceiptByName(searchQuery)
    }

   fun searchReceiptByName(searchQuery: String): LiveData<List<Receipt>>? {
       viewModelScope.launch {
          searchReceiptsByName = searchReceipt(searchQuery)
       }
       return searchReceiptsByName
    }

    fun searchReceiptByCategory(position: Int): LiveData<List<Receipt>>? {
        viewModelScope.launch {
            if(position == 0) {
                searchReceiptsByCategory = receipts//database.getAllReceipts()
            }
            else {
                val selection = getReceiptCategory(position)
                searchReceiptsByCategory = filterReceiptsByCategory(selection)
            }
        }
        return searchReceiptsByCategory
    }
}