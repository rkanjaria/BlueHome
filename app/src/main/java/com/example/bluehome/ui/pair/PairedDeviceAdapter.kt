package com.example.bluehome.ui.pair

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bluehome.databinding.CellPairedDeviceBinding

class PairedDeviceAdapter(
    private val deviceList: List<BluetoothDevice?>,
    private val mListener: PairedDeviceListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PairedDeviceViewHolder(
            CellPairedDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PairedDeviceViewHolder).bind(deviceList[position])
    }

    inner class PairedDeviceViewHolder(private val binding: CellPairedDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bluetoothDevice: BluetoothDevice?) {
            binding.item = bluetoothDevice
            binding.mainCard.setOnClickListener { mListener?.onDeviceSelected(bluetoothDevice) }
            binding.executePendingBindings()

        }
    }

    interface PairedDeviceListener {
        fun onDeviceSelected(bluetoothDevice: BluetoothDevice?)
    }
}