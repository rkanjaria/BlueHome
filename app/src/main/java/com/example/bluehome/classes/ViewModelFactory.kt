package com.example.bluehome.classes

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bluehome.ui.pair.PairViewModel
import com.example.bluehome.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = with(modelClass) {
        when {
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application)
            isAssignableFrom(PairViewModel::class.java) -> PairViewModel(application)
            else -> throw IllegalArgumentException("Unknown viewModel class ${modelClass.name}")
        }
    } as T

    companion object {

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(application).also { INSTANCE = it }
            }
    }
}