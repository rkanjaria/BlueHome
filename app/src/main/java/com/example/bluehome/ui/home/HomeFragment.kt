package com.example.bluehome.ui.home


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluehome.R
import com.example.bluehome.classes.Event
import com.example.bluehome.classes.Status
import com.example.bluehome.databinding.FragmentHomeBinding


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), DeviceAdapter.DeviceAdapterListener {

    private lateinit var binding: FragmentHomeBinding

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
         binding = FragmentHomeBinding.inflate(inflater, container, false)

        deviceAdapter = DeviceAdapter()

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@HomeFragment
            TooltipCompat.setTooltipText(binding.pairIcon, getString(R.string.pair_devices))

            deviceRecyclerview.apply {
                layoutManager = GridLayoutManager(context, 2)
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
                        deviceAdapter.setListener(this@HomeFragment)
                        runLayoutAnimation(binding.deviceRecyclerview)
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

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    companion object {
        const val TAG = "home_fragment"
        fun newInstance() = HomeFragment()
    }
}
