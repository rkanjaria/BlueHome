package com.example.bluehome.ui.pair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bluehome.classes.DEVICE_MAC_ADDRESS
import com.example.bluehome.classes.getPreferenceManager

class PairViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    fun saveDeviceToPreference(address: String) {
        context.getPreferenceManager().saveString(DEVICE_MAC_ADDRESS, address)
    }
}