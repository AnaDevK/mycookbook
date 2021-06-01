package com.example.mycookbook.utils

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mycookbook.R

@BindingAdapter("imageUri")
fun bindImage(imgView: ImageView, imgUriString: String?) {
    imgUriString?.let {
        val imgUri = imgUriString.toUri()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}