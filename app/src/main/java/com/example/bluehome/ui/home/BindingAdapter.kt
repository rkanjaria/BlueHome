package com.example.bluehome.ui.home

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageSrc")
fun ImageView.setImageResource(resource: Int) {
    this.setImageResource(resource)
}
