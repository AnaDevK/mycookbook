package com.example.mycookbook.ui.newupdate

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.mycookbook.R
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class NewReceiptFragmentTest {
    @Test
    fun newReceipt_DisplayedInUi() {
        // GIVEN - Add recipe to the DB
        val bundle = Bundle()
        bundle.putLong("receiptUpdateId", 0L)
        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<NewReceiptFragment>(bundle,
            R.style.ThemeMyCookBook
        )
    }
}