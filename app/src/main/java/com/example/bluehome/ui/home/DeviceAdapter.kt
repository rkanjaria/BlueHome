package com.example.bluehome.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bluehome.databinding.CellDeviceBinding
import com.example.bluehome.models.Device

class DeviceAdapter(
    private val deviceList: List<Device>,
    private val mListener: DeviceAdapterListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DeviceViewHolder(
            CellDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DeviceViewHolder).bind(deviceList[position])
    }

    inner class DeviceViewHolder(private val binding: CellDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: Device) {
            binding.item = device
            binding.mainCard.setOnClickListener { mListener?.onDeviceClicked(device.data) }
            binding.executePendingBindings()
        }
    }

    interface DeviceAdapterListener {
        fun onDeviceClicked(data: String)
    }

}