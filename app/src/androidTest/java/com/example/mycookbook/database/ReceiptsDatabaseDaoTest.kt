package com.example.mycookbook.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.mycookbook.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReceiptsDatabaseDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ReceiptsDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ReceiptsDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReceiptAndGetById() = runBlockingTest {
        // GIVEN - Insert a recipe.
        val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes", "recurl.com", null, null)
        database.receiptsDatabaseDao.insert(receipt)

        // WHEN - Get the recipe by id from the database.
        val loaded = database.receiptsDatabaseDao.getReceiptById(receipt.receiptId)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, notNullValue())
        val value = loaded.getOrAwaitValue()
        assertThat(value.receiptId, `is`(receipt.receiptId))
        assertThat(value.receiptCategory, `is`(receipt.receiptCategory))
        assertThat(value.receiptUrl, `is`(receipt.receiptUrl))
        assertThat(value.receiptTitle, `is`(receipt.receiptTitle))
    }

    @Test
    fun insertReceiptsAndGetAll() = runBlockingTest {
        // GIVEN - Insert a recipe.
        val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes", "recurl.com", null, null)

        var loaded = database.receiptsDatabaseDao.getAllReceipts()
        var value = loaded.getOrAwaitValue()
        assertThat(value.size, `is`(0))

        database.receiptsDatabaseDao.insert(receipt)

        // WHEN - Get the recipe by id from the database.
        loaded = database.receiptsDatabaseDao.getAllReceipts()

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, notNullValue())
        value = loaded.getOrAwaitValue()
        assertThat(value.size, `is`(1))
    }

    @Test
    fun deleteReceiptAndGetAll() = runBlockingTest {
        // GIVEN - Insert a recipe.
        val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes", "recurl.com", null, null)
        database.receiptsDatabaseDao.insert(receipt)

        // WHEN - Get the recipe by id from the database.
        val loaded = database.receiptsDatabaseDao.getAllReceipts()

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, notNullValue())
        var value = loaded.getOrAwaitValue()
        assertThat(value.size, `is`(1))

        database.receiptsDatabaseDao.delete(receipt)
        value = loaded.getOrAwaitValue()
        assertThat(value.size, `is`(0))
    }

    @Test
    fun updateReceiptAndGetById() = runBlockingTest {
        // GIVEN - Insert a recipe.
        val receipt = Receipt(1, "Test receipt1", 'D', "Receipt notes", "recurl.com", null, null)
        database.receiptsDatabaseDao.insert(receipt)

        // WHEN - Get the recipe by id from the database.
        var loaded = database.receiptsDatabaseDao.getReceiptById(receipt.receiptId)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, notNullValue())
        var value = loaded.getOrAwaitValue()
        value.receiptTitle = "Titlu nou"
        value.receiptUrl = "Url nou"
        value.receiptCategory = 'D'
        value.receiptNotes = "Test notes"
        value.receiptImageUri1 = "url1"
        value.images = "img1; img2; img3"

        database.receiptsDatabaseDao.update(value)
        loaded = database.receiptsDatabaseDao.getReceiptById(receipt.receiptId)
        value = loaded.getOrAwaitValue()

        assertThat(value.receiptTitle, `is`("Titlu nou"))
        assertThat(value.receiptUrl, `is`("Url nou"))
        assertThat(value.receiptCategory, `is`('D'))
        assertThat(value.receiptNotes, `is`("Test notes"))
        assertThat(value.receiptImageUri1, `is`("url1"))
        assertThat(value.images, `is`("img1; img2; img3"))
    }
}