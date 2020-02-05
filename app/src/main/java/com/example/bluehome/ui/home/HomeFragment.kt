package com.example.bluehome.ui.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.bluehome.R
import com.example.bluehome.classes.Event
import com.example.bluehome.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), DeviceAdapter.DeviceAdapterListener {

    override fun onDeviceClicked(data: String) {
        viewModel.sendData.postValue(Event(data))
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = (activity as HomeActivity).obtainViewModel()
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@HomeFragment
        }

        viewModel.apply {

            createDeviceList()

            deviceListLiveData.observe(this@HomeFragment, Observer { deviceList ->
                binding.apply {
                    deviceRecyclerview.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DeviceAdapter(deviceList, null)
                    }
                }
            })
        }

        return binding.root
    }

    companion object {
        const val TAG = "home_fragment"
        fun newInstance() = HomeFragment()
    }


}
