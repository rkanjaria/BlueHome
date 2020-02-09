package com.example.bluehome.ui.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.bluehome.R
import com.example.bluehome.classes.Event
import com.example.bluehome.classes.Status
import com.example.bluehome.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), DeviceAdapter.DeviceAdapterListener {

    override fun onDeviceClicked(data: String) {
        viewModel.sendData.value = Event(data)
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = (activity as HomeActivity).obtainViewModel()
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        deviceAdapter = DeviceAdapter()

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@HomeFragment
            TooltipCompat.setTooltipText(binding.pairIcon, getString(R.string.pair_devices))

            deviceRecyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = deviceAdapter
            }
        }

        viewModel.apply {

            createDeviceList()

            deviceListLiveData.observe(this@HomeFragment, Observer { deviceList ->
                deviceAdapter.submitList(deviceList)
            })

            status.observe(this@HomeFragment, Observer {
                when (it) {
                    Status.CONNECTED -> {
                        deviceAdapter.apply {
                            setListener(this@HomeFragment)
                            notifyDataSetChanged()
                        }
                    }
                    else -> {
                        deviceAdapter.apply {
                            setListener(null)
                            notifyDataSetChanged()
                        }
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
