package com.example.bluehome.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluehome.databinding.CellDeviceBinding
import com.example.bluehome.models.Device
import java.util.*

class DeviceAdapter : ListAdapter<Device, RecyclerView.ViewHolder>(DeviceDiffUtil()) {

    private var mListener: DeviceAdapterListener? = null

    class DeviceDiffUtil : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device) =
            Objects.equals(oldItem, newItem)

        override fun areContentsTheSame(oldItem: Device, newItem: Device) =
            oldItem.data == newItem.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DeviceViewHolder(
            CellDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DeviceViewHolder).bind(getItem(position))
    }

    inner class DeviceViewHolder(private val binding: CellDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: Device) {
            binding.item = device
            binding.mainCard.apply {
                if (mListener == null) {
                    alpha = 0.3f
                    isClickable = false
                } else {
                    alpha = 1f
                    isClickable = true
                    setOnClickListener { mListener?.onDeviceClicked(device.data) }
                }
            }
            binding.executePendingBindings()
        }
    }

    fun setListener(listener: DeviceAdapterListener?) {
        mListener = listener
    }

    interface DeviceAdapterListener {
        fun onDeviceClicked(data: String)
    }
}
