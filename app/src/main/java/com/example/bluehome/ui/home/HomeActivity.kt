package com.example.bluehome.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bluehome.R
import com.example.bluehome.classes.EventObserver
import com.example.bluehome.classes.UUID_INSECURE
import com.example.bluehome.classes.addFragmentInActivity
import com.example.bluehome.classes.obtainViewModel
import com.example.bluehome.databinding.ActivityHomeBinding
import com.example.bluehome.service.BluetoothService
import com.example.bluehome.ui.pair.PairActivity

class HomeActivity : AppCompatActivity(), BluetoothService.BluetoothServiceCallback {

    override fun updateStatus(status: String) {
        viewModel.status.postValue(status)
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        viewModel = obtainViewModel()

        viewModel.apply {
            enableBluetoothAndConnect()

            openPairActivity.observe(this@HomeActivity, EventObserver {
                startActivityForResult(
                    PairActivity.createIntent(this@HomeActivity),
                    PAIR_REQUEST_CODE
                )
            })

            startConnection.observe(this@HomeActivity, EventObserver {


                // set text connecting

                val service = BluetoothService(this@HomeActivity, this@HomeActivity)
                service.startClient(it, UUID_INSECURE)
            })
        }

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@HomeActivity
        }

        addFragmentInActivity(R.id.fragment_container, HomeFragment.newInstance(), HomeFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PAIR_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                viewModel.enableBluetoothAndConnect()
            }
        }
    }

    companion object {
        const val PAIR_REQUEST_CODE = 7000
    }

    override fun onResume() {
        super.onResume()
        viewModel.createGreetings()
    }

    fun obtainViewModel() = obtainViewModel(HomeViewModel::class.java)
}
