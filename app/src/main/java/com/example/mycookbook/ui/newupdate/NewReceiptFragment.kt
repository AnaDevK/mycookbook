package com.example.mycookbook.ui.newupdate

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycookbook.R
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.databinding.NewReceiptFragmentBinding
import com.example.mycookbook.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class NewReceiptFragment : Fragment() {
    companion object {
        private const val PICK_PHOTO_CODE = 22
        private const val READ_EXTERNAL_PHOTOS_CODE = 248
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val TAG = "NewReceiptFragment"
    }

    private lateinit var rvImagePicker: RecyclerView
    private lateinit var adapter: ImagePickerAdapter
    private val chosenImageUris = mutableListOf<Uri>()
    private val numImagesRequired = 6
    private val receipt = Receipt()
    private lateinit var binding: NewReceiptFragmentBinding
    private lateinit var newReceiptViewModel: NewReceiptViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.new_receipt_fragment, container,false)

        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(getString(R.string.create_new_receipt))
        setHasOptionsMenu(true)

        val application = requireNotNull(activity).application

        val viewModelFactory = NewReceiptViewModelFactory(application)

        newReceiptViewModel = ViewModelProvider(this, viewModelFactory).get(NewReceiptViewModel::class.java)

        val updateReceiptId = NewReceiptFragmentArgs.fromBundle(requireArguments()).receiptUpdateId
        var isEdit = false

        binding.setLifecycleOwner(this)

        binding.newReceiptViewModel = newReceiptViewModel

        binding.buttonSave.setOnClickListener {
            Save(updateReceiptId, isEdit)
        }

        updateReceiptId?.let {
            if (it != 0L) {
                newReceiptViewModel.GetReceipt(updateReceiptId).observe(
                        viewLifecycleOwner,
                        Observer {

                            // Protect from null, which occurs when we delete the item
                            if (it != null) {
                                // populate with data
                                binding.receiptName.setText(it.receiptTitle)
                                binding.receiptNotes.setText(it.receiptNotes)
                                binding.receiptUrl.setText(it.receiptUrl)
                                if(it.receiptCategory != null) binding.spinner.setSelection(setReceiptCategory(it.receiptCategory!!))
                                if (it.images != null) {
                                    val imagesListStr = it.images.toString().split(";").toTypedArray()
                                    for (i in 0 until imagesListStr.count() - 1) {
                                        chosenImageUris.add(imagesListStr[i].toUri())
                                    }
                                }
                            }
                            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(getString(R.string.edit_recipe))
                        })
                isEdit = true

            }
        }

        fun pickerAdapter(chosenImageUris: MutableList<Uri>): ImagePickerAdapter {
            return ImagePickerAdapter(requireContext(),
                    chosenImageUris,
                    object : ImagePickerAdapter.ImageClickListener {
                        override fun onPlaceHolderClicked() {
                            if (isPermissionGranted(requireActivity(), READ_PHOTOS_PERMISSION)) {
                                //for existing image, alertdialog: options -> delete image, pick other image from gallery
                                launchIntentForPhotos()
                            } else {
                                requestPermission(
                                        requireActivity(),
                                        READ_PHOTOS_PERMISSION,
                                        READ_EXTERNAL_PHOTOS_CODE
                                )
                            }
                        }
                    })
        }
        rvImagePicker = binding.rvImagePicked
        adapter = pickerAdapter(chosenImageUris)
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(context, 3)

        return binding.root
    }

    private fun Save(updateReceiptId: Long, isEdit: Boolean) {
        if (binding.receiptName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.add_name), Toast.LENGTH_SHORT).show()
        } else {
            receipt.receiptId = updateReceiptId
            receipt.receiptTitle = binding.receiptName.text.toString().trim()
            if(binding.receiptNotes.text.toString().isNotEmpty()) receipt.receiptNotes = binding.receiptNotes.text.toString().trim()
            if(binding.receiptUrl.text.toString().isNotEmpty()) receipt.receiptUrl = binding.receiptUrl.text.toString().trim()
            val recCategory = getReceiptCategory(binding.spinner.selectedItemPosition)
            if(receipt.receiptCategory != 'O') receipt.receiptCategory = recCategory
            //save image internal
            saveImagesInternal()

            if (isEdit) {
                newReceiptViewModel.UpdateReceipt(receipt)
            } else {
                newReceiptViewModel.InsertReceipt(receipt)
            }
            this.findNavController().navigate(NewReceiptFragmentDirections.actionNewReceiptFragmentToAllReceiptsFragment())
        }
    }

    private fun saveImagesInternal() {
        val imgUrl = saveImagesList(chosenImageUris)
        var images = StringBuilder()
        for (i in 0 until imgUrl.count()) {
            images.append(imgUrl[i].toString() + ";")
        }
        if(images.toString() != "") receipt.images = images.toString()
        if(imgUrl.size > 0) receipt.receiptImageUri1 = imgUrl[0].toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.findNavController().navigate(NewReceiptFragmentDirections.actionNewReceiptFragmentToAllReceiptsFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_PHOTO_CODE || resultCode != Activity.RESULT_OK || data == null) {
            Log.w(TAG, "Did not get data back from launched activity, user likely cancelled flow")
            return
        }
        val selectedUri: Uri? = data.data
        val clipData = data.clipData
        if (clipData != null) {
            Log.i(TAG, "clipData numImages ${clipData.itemCount}: $clipData")
            for (i in 0 until clipData.itemCount) {
                val clipItem = clipData.getItemAt(i)
                if (chosenImageUris.size < numImagesRequired) {
                    chosenImageUris.add(clipItem.uri)
                }
            }
        } else if (selectedUri != null) {
            Log.i(TAG, "data: $selectedUri")
            chosenImageUris.add(selectedUri)
        }
        adapter.notifyDataSetChanged()
    }

    fun saveImagesList(photoUriList: List<Uri>): List<Uri> {
        var listofImages = mutableListOf<Uri>()
        // Get the context wrapper instance
        val wrapper = ContextWrapper(requireContext())

        for (i in 0 until photoUriList.count()) {
            if (photoUriList[i].toString().contains("media")) {
                // Initializing a new file
                // The bellow line return a directory in internal storage
                var file = wrapper.getDir("images", Context.MODE_PRIVATE)
                // Create a file to save the image
                file = File(file, UUID.randomUUID().toString() + "img${i}" + ".jpg")
                try {
                    // Get the file output stream
                    val stream: OutputStream = FileOutputStream(file)
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, photoUriList[i])
                    // Compress bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    // Flush the stream
                    stream.flush()
                    // Close stream
                    stream.close()
                    listofImages.add(Uri.parse(file.absolutePath))
                } catch (e: IOException) { // Catch the exception
                    e.printStackTrace()
                    Log.d(TAG, "Error at image nr: ${i}" + e.printStackTrace().toString())
                }
            } else {
                //we don't save again images already saved
                if (photoUriList[i].toString().contains("data")) {
                    listofImages.add(photoUriList[i])
                }
            }
        }
        return listofImages
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_PHOTOS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchIntentForPhotos()
            } else {
                Toast.makeText(context, getString(R.string.photos_access_request), Toast.LENGTH_LONG)
                        .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_pics)), PICK_PHOTO_CODE)
    }
}
