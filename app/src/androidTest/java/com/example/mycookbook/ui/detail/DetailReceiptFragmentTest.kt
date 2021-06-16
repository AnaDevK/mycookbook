package com.example.mycookbook.ui.detail

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.mycookbook.R
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class DetailReceiptFragmentTest {
    @Test
    fun receiptDetails_DisplayedInUi() {
        // GIVEN - Add recipe to the DB
        val bundle = Bundle()
        bundle.putLong("selectedReceiptId", 1)
        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<DetailReceiptFragment>(fragmentArgs = bundle, R.style.ThemeMyCookBook)
    }
}