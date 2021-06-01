package com.example.mycookbook.ui.newupdate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.database.ReceiptsDatabase
import kotlinx.coroutines.launch

class NewReceiptViewModel(app: Application) : AndroidViewModel(app) {

    val database = ReceiptsDatabase.getInstance(app).receiptsDatabaseDao

   // private var _receipt = MutableLiveData<Receipt?>()
   // var receipt: LiveData<Receipt>
        //get() = _receipt

    private suspend fun insert(receipt: Receipt) {
        database.insert(receipt)
    }

    private suspend fun update(receipt: Receipt) {
        database.update(receipt)
    }

    fun InsertReceipt(receipt: Receipt) {
        viewModelScope.launch {
           insert(receipt)
        }
    }

    fun UpdateReceipt(receipt: Receipt) {
        viewModelScope.launch {
            update(receipt)
        }
    }

    fun GetReceipt(id: Long): LiveData<Receipt> {
        return database.getReceiptById(id)
    }
}