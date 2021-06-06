package com.example.mycookbook.utils

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.mycookbook.R

class ImagePickerAdapter(
    private val context: Context,
    private val imageUris: List<Uri>,
    private val imageClickListener: ImageClickListener
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    interface ImageClickListener {
        fun onPlaceHolderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_picked, parent, false)
//        val cardWidth = parent.width / 3
        val cardHeight = (parent.height - 100) / 2
//        //val cardSideLength = min(cardWidth, cardHeight)
        val layoutParams = view.findViewById<ImageView>(R.id.ivCustomImage).layoutParams
        layoutParams.height = cardHeight
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < imageUris.size) {
            val imgPath: Uri
            if(imageUris[position].toString().contains("img")) {
                imgPath = ((ContextWrapper(context).getDir(
                    "images",
                    Context.MODE_PRIVATE
                )).toString() + "/" + imageUris[position].toString()).toUri()
            } else {
                imgPath = imageUris[position]
            }
            holder.bind(imgPath)
        } else {
            holder.bind()
        }
    }

    override fun getItemCount() = 6

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomImage)

        fun bind(uri: Uri) {
            ivCustomImage.setImageURI(uri)
            ivCustomImage.setOnClickListener(null)
        }

        fun bind() {
            ivCustomImage.setOnClickListener {
                //Launch intent for user to select photos
                imageClickListener.onPlaceHolderClicked()

            }
        }
    }
}