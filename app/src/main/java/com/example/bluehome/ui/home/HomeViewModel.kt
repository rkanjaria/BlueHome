package com.example.bluehome.ui.home

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluehome.R
import com.example.bluehome.classes.DEVICE_MAC_ADDRESS
import com.example.bluehome.classes.Event
import com.example.bluehome.classes.getPreferenceManager
import com.example.bluehome.classes.toast
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

    val status = MutableLiveData<String>()

    fun createDeviceList() {
        deviceList.add(Device("Living Room Light", R.drawable.ic_hall_light, "1"))
        deviceList.add(Device("Living Room Fan", R.drawable.ic_hall_fan, "2"))
        deviceList.add(Device("Balcony Light", R.drawable.ic_balcony_light, "3"))
        deviceList.add(Device("Balcony Fan", R.drawable.ic_balcony_fan, "4"))
        deviceList.add(Device("Living Room Lamp", R.drawable.ic_hall_lamp, "5"))
        deviceList.add(Device("Door Lamp", R.drawable.ic_door_lamp, "6"))
        deviceList.add(Device("Television", R.drawable.ic_tv, "7"))
        deviceList.add(Device("Switch One", R.drawable.ic_hall_light, "8"))

        deviceListLiveData.value = deviceList
    }

    private fun startConnection(bluetoothDevice: BluetoothDevice) {
        //service = BluetoothService(context)
        //service?.startClient(bluetoothDevice, UUID_INSECURE)

        _startConnection.value = Event(bluetoothDevice)
    }

    fun enableBluetoothAndConnect() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        when {
            bluetoothAdapter == null -> context.toast("Your device does not have bluetooth capabilities.")
            !bluetoothAdapter.isEnabled -> {
                bluetoothAdapter.enable()
                connectToDevice()
            }
            else -> connectToDevice()
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

    fun createGreetings() {
        _greeting.value = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            in 21..24 -> "Hello" // this is good night
            else -> "Good Morning"
        }
    }
}