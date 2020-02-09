package com.example.bluehome.ui.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.bluehome.R
import com.example.bluehome.classes.Status

@BindingAdapter("imageSrc")
fun ImageView.setImageResource(resource: Int) {
    this.setImageResource(resource)
}

@BindingAdapter("statusText")
fun TextView.setStatusText(status: Status?) {
    when (status) {
        Status.CONNECTED -> {
            text = context.getString(R.string.devices_connected)
            isClickable = false
        }
        Status.CONNECTING -> {
            text = context.getString(R.string.connecting_devices)
            isClickable = true
        }
        else -> {
            text = context.getString(R.string.failed_to_connect)
            isClickable = true
        }
    }
}
