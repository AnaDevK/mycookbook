package com.example.mycookbook.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.database.ReceiptsDatabase
import com.example.mycookbook.utils.setReceiptCategory
import kotlinx.coroutines.launch

class DetailReceiptViewModel(receiptId: Long, app: Application) : AndroidViewModel(app) {

//    private val _selectedReceiptId = MutableLiveData<Long>()
//    val selectedReceiptId: LiveData<Long>
//        get() =  _selectedReceiptId
    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _navigateToUpdate = MutableLiveData<Long>()
    val navigateToUpdate
        get() = _navigateToUpdate

    fun onUpdateNavigated() {
        _navigateToUpdate.value = null
    }

    val receipt: LiveData<Receipt>
    val database = ReceiptsDatabase.getInstance(app).receiptsDatabaseDao


    init {
            receipt = database.getReceiptById(receiptId)
        }

    private suspend fun DeleteReceipt(rec: Receipt) {
        database.delete(rec)
    }

    fun Delete(receipt: Receipt) {
        viewModelScope.launch {
            DeleteReceipt(receipt)
        }
    }

    val displayCategory = Transformations.map(receipt) {
        setReceiptCategory(it.receiptCategory?:'O')
    }

}