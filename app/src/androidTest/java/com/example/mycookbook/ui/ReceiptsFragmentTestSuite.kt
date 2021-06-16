package com.example.mycookbook.ui

import com.example.mycookbook.ui.detail.DetailReceiptFragmentTest
import com.example.mycookbook.ui.newupdate.NewReceiptFragmentTest
import com.example.mycookbook.ui.overview.AllReceiptsFragmentTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
        @Suite.SuiteClasses(
            AllReceiptsFragmentTest::class,
            NewReceiptFragmentTest::class,
            DetailReceiptFragmentTest::class
        )
class ReceiptsFragmentTestSuite