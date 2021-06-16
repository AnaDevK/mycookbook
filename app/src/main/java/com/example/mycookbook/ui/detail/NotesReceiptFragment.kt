package com.example.mycookbook.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.mycookbook.R
import com.example.mycookbook.database.ReceiptsDatabase
import com.example.mycookbook.database.ReceiptsLocalDataSource
import com.example.mycookbook.databinding.NotesReceiptFragmentBinding
import com.example.mycookbook.repository.DefaultReceiptsRepository
import kotlinx.coroutines.Dispatchers

class NotesReceiptFragment : Fragment() {
    private var receiptId: Long = 0
    private lateinit var notesReceiptViewModel: DetailReceiptViewModel
    private lateinit var binding: NotesReceiptFragmentBinding
    private lateinit var viewModelFactory: DetailReceiptViewModelFactory
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate( inflater, R.layout.notes_receipt_fragment, container,false)

        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
        setHasOptionsMenu(true)

        val application = requireNotNull(activity).application
        receiptId = NotesReceiptFragmentArgs.fromBundle(requireArguments()).receiptId

        val dataBase = ReceiptsDatabase.getInstance(application).receiptsDatabaseDao
        val receiptsLocalDataSource = ReceiptsLocalDataSource(dataBase, Dispatchers.Main)
        val repository = DefaultReceiptsRepository(receiptsLocalDataSource)
        viewModelFactory = DetailReceiptViewModelFactory(receiptId, repository)
        notesReceiptViewModel = ViewModelProvider(this, viewModelFactory).get(DetailReceiptViewModel::class.java)

        binding.notesReceiptViewModel = notesReceiptViewModel
        binding.spinner2.isEnabled = false
        binding.setLifecycleOwner(this)

        return binding.root
    }
}