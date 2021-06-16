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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycookbook.MainActivity
import com.example.mycookbook.R
import com.example.mycookbook.database.Receipt
import com.example.mycookbook.database.ReceiptsDatabase
import com.example.mycookbook.database.ReceiptsLocalDataSource
import com.example.mycookbook.databinding.NewReceiptFragmentBinding
import com.example.mycookbook.repository.DefaultReceiptsRepository
import com.example.mycookbook.utils.*
import kotlinx.coroutines.Dispatchers
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
    var isEdit = false
    var updateReceiptId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.new_receipt_fragment, container, false)
        if (activity is MainActivity) {
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(true)
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(getString(R.string.create_new_receipt))
        }
        setHasOptionsMenu(true)

        val application = requireNotNull(activity).application
        val dataBase = ReceiptsDatabase.getInstance(application).receiptsDatabaseDao
        val receiptsLocalDataSource = ReceiptsLocalDataSource(dataBase, Dispatchers.Main)
        val repository = DefaultReceiptsRepository(receiptsLocalDataSource)
        val viewModelFactory = NewReceiptViewModelFactory(repository)

        newReceiptViewModel =
            ViewModelProvider(this, viewModelFactory).get(NewReceiptViewModel::class.java)

        //check if is update
        if (NewReceiptFragmentArgs.fromBundle(requireArguments()).receiptUpdateId != 0L) {
            isEdit = true
            updateReceiptId = NewReceiptFragmentArgs.fromBundle(requireArguments()).receiptUpdateId
        }
        if (isEdit) {
            newReceiptViewModel.getReceipt(updateReceiptId).observe(
                viewLifecycleOwner,
                {
                    // Protect from null, which occurs when we delete the item
                    if (it != null) {
                        // populate with data
                        binding.receiptName.setText(it.receiptTitle)
                        binding.receiptNotes.setText(it.receiptNotes)
                        binding.receiptUrl.setText(it.receiptUrl)
                        if (it.receiptCategory != null) binding.spinner.setSelection(
                            setReceiptCategory(it.receiptCategory!!)
                        )
                        if (it.images != null) {
                            val imagesListStr = it.images.toString().split(";").toTypedArray()
                            for (i in 0 until imagesListStr.count() - 1) {
                                chosenImageUris.add(imagesListStr[i].toUri())
                            }
                        }
                    }
                    if (activity is MainActivity) {
                        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(getString(R.string.edit_recipe))
                    }
                    adapter.notifyDataSetChanged()
                })
        }

        binding.setLifecycleOwner(this)

        binding.newReceiptViewModel = newReceiptViewModel

        binding.buttonSave.setOnClickListener {
            save()
        }

        rvImagePicker = binding.rvImagePicked
        adapter = pickerAdapter(chosenImageUris)
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(context, 3)

        return binding.root
    }

    fun pickerAdapter(chosenImageUris: MutableList<Uri>): ImagePickerAdapter {
        return ImagePickerAdapter(requireContext(),
            chosenImageUris,
            object : ImagePickerAdapter.ImageClickListener {
                override fun onPlaceHolderClicked() {
                    if (requireActivity() is MainActivity) {
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
                }
            })
    }

    private fun save() {
        if (binding.receiptName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.add_name), Toast.LENGTH_SHORT)
                .show()
        } else {
            //receipt.receiptId = receiptId
            receipt.receiptTitle = binding.receiptName.text.toString().trim()
            if (binding.receiptNotes.text.toString().isNotEmpty()) receipt.receiptNotes =
                binding.receiptNotes.text.toString().trim()
            if (binding.receiptUrl.text.toString().isNotEmpty()) receipt.receiptUrl =
                binding.receiptUrl.text.toString().trim()
            val recCategory = getReceiptCategory(binding.spinner.selectedItemPosition)
            if (receipt.receiptCategory != 'O') receipt.receiptCategory = recCategory
            //save image internal
            saveImagesInternal()

            if (isEdit) {
                receipt.receiptId = updateReceiptId
                newReceiptViewModel.updateReceipt(receipt)
            } else {
                newReceiptViewModel.insertReceipt(receipt)
            }
            this.findNavController()
                .navigate(NewReceiptFragmentDirections.actionNewReceiptFragmentToAllReceiptsFragment())
        }
    }

    private fun saveImagesInternal() {
        val imgUrl = saveImagesList(chosenImageUris)
        val images = StringBuilder()
        for (i in 0 until imgUrl.count()) {
            images.append(imgUrl[i].toString() + ";")
        }
        if (images.toString() != "") receipt.images = images.toString()
        if (imgUrl.size > 0) receipt.receiptImageUri1 = imgUrl[0].toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.findNavController()
                .navigate(NewReceiptFragmentDirections.actionNewReceiptFragmentToAllReceiptsFragment())
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode != PICK_PHOTO_CODE || resultCode != Activity.RESULT_OK || data == null) {
//            Log.w(TAG, "Did not get data back from launched activity, user likely cancelled flow")
//            return
//        }
//        val selectedUri: Uri? = data.data
//        val clipData = data.clipData
//        if (clipData != null) {
//            Log.i(TAG, "clipData numImages ${clipData.itemCount}: $clipData")
//            for (i in 0 until clipData.itemCount) {
//                val clipItem = clipData.getItemAt(i)
//                if (chosenImageUris.size < numImagesRequired) {
//                    chosenImageUris.add(clipItem.uri)
//                }
//            }
//        } else if (selectedUri != null) {
//            Log.i(TAG, "data: $selectedUri")
//            chosenImageUris.add(selectedUri)
//        }
//        adapter.notifyDataSetChanged()
//    }

     fun getImages(data: Intent?) {
         if(data != null) {
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
    }

    private fun saveImagesList(photoUriList: List<Uri>): List<Uri> {
        val listOfImages = mutableListOf<Uri>()
        // Get the context wrapper instance
        val wrapper = ContextWrapper(requireContext())

        for (i in 0 until photoUriList.count()) {
            if (photoUriList[i].toString().contains("media")) {
                // Initializing a new file
                // The bellow line return a directory in internal storage
                var file = wrapper.getDir("images", Context.MODE_PRIVATE)
                val fileName = UUID.randomUUID().toString() + "img${i}" + ".jpg"
                // Create a file to save the image
                file = File(file, fileName)
                try {
                    // Get the file output stream
                    val stream: OutputStream = FileOutputStream(file)
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        photoUriList[i]
                    )
                    // Compress bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    // Flush the stream
                    stream.flush()
                    // Close stream
                    stream.close()
                    listOfImages.add(Uri.parse(fileName))
                } catch (e: IOException) { // Catch the exception
                    e.printStackTrace()
                    Log.d(TAG, "Error at image nr: ${i}" + e.printStackTrace().toString())
                }
            } else {
                //we don't save again images already saved
                if (photoUriList[i].toString().contains("img")) {
                    listOfImages.add(photoUriList[i])
                }
            }
        }
        return listOfImages
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
                Toast.makeText(
                    context,
                    getString(R.string.photos_access_request),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(Intent.createChooser(intent, getString(R.string.choose_pics)))
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            getImages(data)
        }
    }
}
