package com.example.mycookbook.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mycookbook.MainActivity
import com.example.mycookbook.R
import com.example.mycookbook.database.ReceiptsDatabase
import com.example.mycookbook.database.ReceiptsLocalDataSource
import com.example.mycookbook.databinding.AllReceiptsFragmentBinding
import com.example.mycookbook.repository.DefaultReceiptsRepository
import com.example.mycookbook.utils.ReceiptAdapter
import kotlinx.coroutines.Dispatchers


class AllReceiptsFragment : Fragment() {

    private lateinit var adapter: ReceiptAdapter
    private lateinit var allReceiptsViewModel: AllReceiptsViewModel
    private lateinit var binding: AllReceiptsFragmentBinding
    private var searchViewQuerySubmitted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        binding =
            DataBindingUtil.inflate(inflater, R.layout.all_receipts_fragment, container, false)

        setHasOptionsMenu(true)
        if (activity is MainActivity) {
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(true)
            (activity as AppCompatActivity?)?.supportActionBar?.setLogo(R.drawable.ic_book)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayUseLogoEnabled(true)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title)
        }
        val application = requireNotNull(this.activity).application
        val dataBase = ReceiptsDatabase.getInstance(application).receiptsDatabaseDao
        val receiptsLocalDataSource = ReceiptsLocalDataSource(dataBase, Dispatchers.Main)
        val repository = DefaultReceiptsRepository(receiptsLocalDataSource)
        val viewModelFactory = AllReceiptsViewModelFactory(repository)

        allReceiptsViewModel =
            ViewModelProvider(this, viewModelFactory).get(AllReceiptsViewModel::class.java)

        binding.allReceiptsViewModel = allReceiptsViewModel
        binding.setLifecycleOwner(this)

        adapter = ReceiptAdapter(requireContext(), ReceiptAdapter.ReceiptClickListener {
            allReceiptsViewModel.displayReceiptDetails(it)
        })

        allReceiptsViewModel.receipts?.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.rvReceipts.adapter = adapter
        allReceiptsViewModel.navigateToSelectedReceipt.observe(
            viewLifecycleOwner,
            { receipt ->
                receipt?.let {
                    this.findNavController().navigate(
                        AllReceiptsFragmentDirections.actionAllReceiptsFragmentToDetailReceiptFragment(
                            receipt.receiptId
                        )
                    )
                    allReceiptsViewModel.displayReceiptDetailsComplete()
                }
            })

        binding.floatingActionButtonAdd.setOnClickListener {
            this.findNavController().navigate(
                AllReceiptsFragmentDirections.ActionAllReceiptsFragmentToNewReceiptFragment2(0)
            )
        }

        return binding.root
    }

    private fun searchReceiptByName(query: String) {
        val searchQuery = "%$query%"
        allReceiptsViewModel.searchReceiptByName(searchQuery).observe(
            viewLifecycleOwner, { list -> list.let { adapter.submitList(it) } })
    }

    private fun searchReceiptyByCategory(position: Int) {
        allReceiptsViewModel.searchReceiptByCategory(position)?.observe(
            viewLifecycleOwner,
            { list -> list.let { adapter.submitList(it.sortedBy { it.receiptId }) } })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                searchReceiptyByCategory(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.searchView.queryHint = getString(R.string.search_recipe)
        binding.searchView.clearFocus()
        binding.searchView.setIconifiedByDefault(true)
        binding.searchView.setQuery("", false)
        binding.searchView.onActionViewCollapsed()

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewQuerySubmitted = true
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                searchReceiptByName(query)
                return true
            }
        })
    }
}
