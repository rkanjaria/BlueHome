package com.example.bluehome.ui.home


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluehome.classes.Event
import com.example.bluehome.databinding.FragmentPairBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 */
class PairBottomSheetFragment : BottomSheetDialogFragment(),
    PairedDeviceAdapter.PairedDeviceListener {

    override fun onDeviceSelected(bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.address?.let { address ->
            viewModel.apply {
                saveDeviceToPreference(address)
                dismiss()
                if (activity is HomeActivity) {
                    (activity as HomeActivity).getService()?.cancel()
                    enableBluetoothAndConnect()
                }
            }
        }
    }

    private lateinit var binding: FragmentPairBottomSheetBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPairBottomSheetBinding.inflate(inflater, container, false)
        viewModel = (activity as HomeActivity).obtainViewModel()

        val bluetoothDevices =
            BluetoothAdapter.getDefaultAdapter()?.bondedDevices?.toList().orEmpty()

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@PairBottomSheetFragment

            pairedRecyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = PairedDeviceAdapter(
                    bluetoothDevices,
                    this@PairBottomSheetFragment
                )
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "pair_bottom_sheet_fragment"
        fun getInstance() = PairBottomSheetFragment()
    }
}
