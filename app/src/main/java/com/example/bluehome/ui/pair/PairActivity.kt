package com.example.bluehome.ui.pair

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluehome.R
import com.example.bluehome.classes.obtainViewModel
import com.example.bluehome.databinding.ActivityPairBinding

class PairActivity : AppCompatActivity(), PairedDeviceAdapter.PairedDeviceListener {

    override fun onDeviceSelected(bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.address?.let { address ->
            viewModel.saveDeviceToPreference(address)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private lateinit var viewModel: PairViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityPairBinding>(this, R.layout.activity_pair)

        viewModel = obtainViewModel(PairViewModel::class.java)
        val bluetoothDevices =
            BluetoothAdapter.getDefaultAdapter()?.bondedDevices?.toList().orEmpty()

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@PairActivity

            pairedRecyclerview.apply {
                layoutManager = LinearLayoutManager(this@PairActivity)
                adapter = PairedDeviceAdapter(bluetoothDevices, this@PairActivity)
            }
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, PairActivity::class.java)
    }
}
