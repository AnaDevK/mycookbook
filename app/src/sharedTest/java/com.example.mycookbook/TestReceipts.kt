package com.example.mycookbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mycookbook.database.Receipt

val receipt1 = Receipt(1, "Test receipt1", 'D', "Receipt notes1", "recurl1.com", null, null)
val receipt2 = Receipt(2, "Test receipt2", 'D', "Receipt notes2", "recurl2.com", null, null)
val receipt3 = Receipt(3, "Test receipt3", 'D', "Receipt notes3", "recurl3.com", null, null)

private val receipts = listOf(receipt1, receipt2, receipt3)
private var receiptsLiveData = MutableLiveData<List<Receipt>>()

fun getReceipts(): List<Receipt> {
    return receipts
}

fun getReceiptsLiveData(): LiveData<List<Receipt>> {
    receiptsLiveData.value = receipts
    return receiptsLiveData
}
