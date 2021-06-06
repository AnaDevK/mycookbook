package com.example.mycookbook.ui.detail

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.onNavDestinationSelected
import androidx.viewpager2.widget.ViewPager2
import com.example.mycookbook.R
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.databinding.DetailReceiptFragmentBinding
import com.example.mycookbook.utils.ViewPageAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException


class DetailReceiptFragment : Fragment() {

    private var receiptId: Long = 0
    private lateinit var detailReceiptViewModel: DetailReceiptViewModel
    private lateinit var binding: DetailReceiptFragmentBinding
    private lateinit var viewModelFactory: DetailReceiptViewModelFactory
    private lateinit var navController: NavController
    var imagesList: MutableList<Uri> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imagesList.clear()
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.detail_receipt_fragment,
            container,
            false
        )

        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
        setHasOptionsMenu(true)

        val application = requireNotNull(activity).application
        receiptId = DetailReceiptFragmentArgs.fromBundle(requireArguments()).selectedReceiptId
        viewModelFactory = DetailReceiptViewModelFactory(receiptId, application)
        detailReceiptViewModel = ViewModelProvider(this, viewModelFactory).get(
            DetailReceiptViewModel::class.java
        )

        binding.detailReceiptViewModel = detailReceiptViewModel
        binding.setLifecycleOwner(this)

        navController = Navigation.findNavController(requireActivity(), R.id.myNavHostFragment)
        val view_pager2 = binding.viewPager2
        view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val indicator = binding.indicator
        detailReceiptViewModel.receipt.observe(viewLifecycleOwner, {
            if(it.receiptTitle != null) (activity as? AppCompatActivity)?.supportActionBar?.setTitle(it.receiptTitle)

            val imagesListStr = it.images.toString().split(";").toTypedArray()
            for (i in 0 until imagesListStr.count() - 1) {
                imagesList.add(setImageFullPath(imagesListStr[i]))
            }
            if (!imagesList.isEmpty()) {
                view_pager2.adapter = ViewPageAdapter(imagesList)
            } else {
                val uri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + requireContext().getPackageName() + File.separator + R.drawable.foodpic);
                view_pager2.adapter = ViewPageAdapter(listOf(uri))
            }
            indicator.setViewPager(view_pager2)
        })

        binding.txtMoreDetails.setOnClickListener {
            navController.navigate(
                DetailReceiptFragmentDirections.actionDetailReceiptFragmentToNotesReceiptFragment(
                    receiptId
                )
            )
        }

        return binding.root
    }

    private fun delete() {
        showDeleteDialog()
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no_button)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.yes_button)) { _, _ ->
                val receipt = Receipt(receiptId)

                detailReceiptViewModel.Delete(receipt)
                if (imagesList.isNotEmpty()) {
                    deleteInternalImages(imagesList)
                }
                imagesList.clear()
                navController.navigate(DetailReceiptFragmentDirections.actionDetailReceiptFragmentToAllReceiptsFragment())
            }
            .show()
    }

    private fun edit() {
        navController.navigate(
            DetailReceiptFragmentDirections.actionDetailReceiptFragmentToNewReceiptFragment(
                receiptId
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> edit()
            R.id.delete -> delete()
            R.id.home ->
                navController.navigate(DetailReceiptFragmentDirections.actionDetailReceiptFragmentToAllReceiptsFragment())
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun deleteInternalImages(photoUriList: List<Uri>) {
        for (i in 0 until photoUriList.count()) {
            val file = File(photoUriList[i].toString())
            try {
                if(file.exists()) {
                    file.delete()
                }
            } catch (e: IOException) { // Catch the exception
                e.printStackTrace()
                Log.d(
                    "AllReceiptsFragment",
                    "Error at image nr: ${i}" + e.printStackTrace().toString()
                )
            }
        }
    }

    fun setImageFullPath(fileName: String): Uri{
        return ((ContextWrapper(requireContext()).getDir("images", Context.MODE_PRIVATE)).toString() + "/" + fileName).toUri()
    }
}
