package com.example.mycookbook.ui.overview

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.mycookbook.R
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class AllReceiptsFragmentTest {
    @Test
    fun allReceipts_DisplayedInUi() {
        // GIVEN - Add recipe to the DB
        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<AllReceiptsFragment>(null,
           R.style.ThemeMyCookBook
        )
    }
}