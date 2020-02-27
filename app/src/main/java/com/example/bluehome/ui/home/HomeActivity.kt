package com.example.bluehome.ui.home

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bluehome.R
import com.example.bluehome.classes.*
import com.example.bluehome.databinding.ActivityHomeBinding
import com.example.bluehome.service.BluetoothService


class HomeActivity : AppCompatActivity(), BluetoothService.BluetoothServiceCallback {

    override fun updateStatus(status: Status) {
        viewModel.status.postValue(status)
    }

    private lateinit var viewModel: HomeViewModel
    private var service: BluetoothService? = null

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {

                kotlin.run returnOfWhen@{
                    when (BluetoothAdapter.getDefaultAdapter()?.state) {
                        BluetoothAdapter.STATE_ON -> {
                            viewModel.connectToDevice()
                            return@returnOfWhen
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        viewModel = obtainViewModel()

        viewModel.apply {
            showPairBottomSheet.observe(this@HomeActivity, EventObserver {
                val pairBottomSheet = PairBottomSheetFragment.getInstance()
                pairBottomSheet.show(supportFragmentManager, PairBottomSheetFragment.TAG)
            })

            startConnection.observe(this@HomeActivity, EventObserver {
                service = BluetoothService(this@HomeActivity)
                service?.startClient(it, UUID_INSECURE)
            })

            sendData.observe(this@HomeActivity, EventObserver {
                service?.write(it.toByteArray())
            })
        }

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@HomeActivity
        }

        addFragmentInActivity(R.id.fragment_container, HomeFragment.newInstance(), HomeFragment.TAG)
    }

    private fun registerBluetoothReceiver() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        viewModel.apply {
            createGreetings()
            enableBluetoothAndConnect()
        }
        registerBluetoothReceiver()
    }

    override fun onPause() {
        super.onPause()
        service?.cancel()
        viewModel.disableBluetooth()
        unregisterReceiver(bluetoothReceiver)
    }

    fun obtainViewModel() = obtainViewModel(HomeViewModel::class.java)
    fun getService() = service
}
