package com.example.mycookbook

import com.example.mycookbook.database.Receipt
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test

class NewReceiptTest {
    @Test
    fun createNewReceipt() {
    val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes", "recurl.com",null, null)
       // Assert.assertThat(receipt.receiptId, Is.`is`(1))
        Assert.assertThat(receipt.receiptTitle, Is.`is`("Test receipt1"))
       // Assert.assertThat(receipt.receiptCategory, Is.`is`('D'))
       // Assert.assertThat(receipt.receiptNotes, Is.`is`("Receipt notes"))
       // Assert.assertThat(receipt.receiptUrl, Is.`is`("recurl.com"))
    }
}