package com.example.bluehome.ui.home

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluehome.R
import com.example.bluehome.classes.*
import com.example.bluehome.models.Device
import java.util.*


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _openPairActivity = MutableLiveData<Event<Unit>>()
    val openPairActivity: LiveData<Event<Unit>> = _openPairActivity

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _startConnection = MutableLiveData<Event<BluetoothDevice>>()
    val startConnection: LiveData<Event<BluetoothDevice>> = _startConnection

    private val deviceList = mutableListOf<Device>()
    val deviceListLiveData = MutableLiveData<MutableList<Device>>()

    val sendData = MutableLiveData<Event<String>>()

    val status = MutableLiveData<Status>()

    fun createDeviceList() {
        deviceList.add(Device("Hall Light", R.drawable.ic_hall_light, "a"))
        deviceList.add(Device("Hall Fan", R.drawable.ic_hall_fan, "b"))
        deviceList.add(Device("Hall Lamp", R.drawable.ic_hall_lamp, "c"))
        deviceList.add(Device("Balcony Light", R.drawable.ic_balcony_light, "d"))
        deviceList.add(Device("Balcony Fan", R.drawable.ic_balcony_fan, "e"))
        deviceList.add(Device("Outdoor Lamp", R.drawable.ic_door_lamp, "f"))
        deviceList.add(Device("Television", R.drawable.ic_tv, "g"))
        deviceList.add(Device("Switch One", R.drawable.ic_hall_light, "h"))
        deviceListLiveData.value = deviceList
    }

    private fun startConnection(bluetoothDevice: BluetoothDevice) {
        _startConnection.value = Event(bluetoothDevice)
    }

    fun enableBluetoothAndConnect() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        when {
            bluetoothAdapter == null -> context.toast(context.getString(R.string.device_capabilities_error))
            !bluetoothAdapter.isEnabled -> {
                bluetoothAdapter.enable()
                connectToDevice()
            }
            else -> connectToDevice()
        }
    }

    fun disableBluetooth() {
        when (val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) {
            null -> context.toast(context.getString(R.string.device_capabilities_error))
            else -> bluetoothAdapter.disable()
        }
    }

    private fun connectToDevice() {
        val macAddress = context.getPreferenceManager().getString(DEVICE_MAC_ADDRESS, "")
        if (macAddress.isNullOrEmpty()) {
            _openPairActivity.value = Event(Unit)
        } else {
            BluetoothAdapter.getDefaultAdapter()?.getRemoteDevice(macAddress)?.let { btDevice ->
                startConnection(btDevice)
            }
        }
    }

    fun onPairClicked() {
        _openPairActivity.value = Event(Unit)
    }

    fun createGreetings() {
        _greeting.value = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            in 21..24 -> "Hello!" // this is good night
            else -> "Good Morning"
        }
    }
}