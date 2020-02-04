package com.example.bluehome.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.bluehome.R
import com.example.bluehome.classes.UUID_INSECURE
import com.example.bluehome.classes.toast
import com.example.bluehome.widgets.ProgressDialog
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

class BluetoothService(private val context: Context, private val mCallback: BluetoothServiceCallback?) {

    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mInsecureAcceptThread: AcceptThread? = null
    private var mConnectThread: ConnectThread? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    private var deviceUUID: UUID? = null

    //private var progressDialog: ProgressDialog? = null

    private var mConnectedThread: ConnectedThread? = null

    companion object {
        const val TAG = "BluetoothService"
        const val appName = "BlueHome"
    }

    private inner class AcceptThread : Thread() {
        private var mServerSocket: BluetoothServerSocket? = null

        init {
            var tmp: BluetoothServerSocket? = null
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    appName,
                    UUID_INSECURE
                )
                Log.d(TAG, "AcceptThread: Setting up Server using: $UUID_INSECURE")
            } catch (e: IOException) {
                Log.d(TAG, "AcceptThread: IOException: " + e.message)
                mCallback?.updateStatus(context.getString(R.string.failed_to_connect))
            }
            mServerSocket = tmp
        }


        override fun run() {
            Log.d(TAG, "AcceptThread running")
            var socket: BluetoothSocket? = null
            try {
                Log.d(TAG, "RFCOM server start...")
                socket = mServerSocket?.accept()
                Log.d(TAG, "RFCOM server accepted connection")
            } catch (e: IOException) {
                Log.d(TAG, "AcceptThread: IOException ${e.message}")
            }

            socket?.let {
                connected(it, mBluetoothDevice)
            }
        }

        fun cancel() {
            Log.e(TAG, "AcceptThread: closing...")
            try {
                mServerSocket?.close()
            } catch (e: IOException) {
                Log.d(TAG, "AcceptThread: closing exception ${e.message}")
            }
        }
    }

    private inner class ConnectThread(bluetoothDevice: BluetoothDevice, uuid: UUID) : Thread() {
        private var mSocket: BluetoothSocket? = null

        init {
            mBluetoothDevice = bluetoothDevice
            deviceUUID = uuid
        }

        override fun run() {
            Log.d(TAG, "ConnectThread: Running Connect Thread")
            var tmp: BluetoothSocket? = null
            try {
                Log.d(TAG, "ConnectThread: Creating RFCOM socket")
                tmp = mBluetoothDevice?.createInsecureRfcommSocketToServiceRecord(deviceUUID)
            } catch (e: IOException) {
                Log.d(TAG, "ConnectThread: Exception in creating RFCOM socket ${e.message}")

                mCallback?.updateStatus(context.getString(R.string.failed_to_connect))
            }

            mSocket = tmp
            mBluetoothAdapter.cancelDiscovery()

            try {
                mSocket?.connect()
                Log.d(TAG, "ConnectThread: Connected.")
                mCallback?.updateStatus(context.getString(R.string.devices_connected))
            } catch (e: IOException) {
                mSocket?.close()
                Log.d(TAG, "ConnectThread: Exception in connection ${e.message}")

                mCallback?.updateStatus(context.getString(R.string.failed_to_connect))
            }

            mSocket?.let {
                connected(it, mBluetoothDevice)
            }
        }

        fun cancel() {
            Log.d(TAG, "ConnectThread: closing...")
            try {
                mSocket?.close()
            } catch (e: IOException) {
                Log.d(TAG, "ConnectThread: closing exception ${e.message}")
            }
        }
    }


    @Synchronized
    fun start() {
        mConnectThread?.let { it.cancel() }
        mConnectThread = null
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = AcceptThread()
            mInsecureAcceptThread?.start()
        }
    }

    fun startClient(bluetoothDevice: BluetoothDevice, uuid: UUID) {
        Log.d(TAG, "StartCliend: started...")

        // todo show connection dialog here

        mCallback?.updateStatus(context.getString(R.string.connecting_devices))

        mConnectThread = ConnectThread(bluetoothDevice, uuid)
        mConnectThread?.start()
    }

    private inner class ConnectedThread(bluetoothSocket: BluetoothSocket) : Thread() {

        private var mSocket: BluetoothSocket? = null
        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null

        init {
            mSocket = bluetoothSocket
            var tempInputStream: InputStream? = null
            var tempOutputStream: OutputStream? = null

            //progressDialog?.dismiss()

            try {
                tempInputStream = mSocket?.inputStream
                tempOutputStream = mSocket?.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }

            inputStream = tempInputStream
            outputStream = tempOutputStream
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int? = 0

            while (true) {
                try {
                    bytes = inputStream?.read(buffer)
                    val incomingMessage = bytes.toString()
                    Log.d(TAG, "Incoming message: $incomingMessage")
                } catch (e: IOException) {
                    Log.d(TAG, "Exception in reading inputstream: ${e.message}")
                    mCallback?.updateStatus(context.getString(R.string.failed_to_connect))
                    break
                }
            }
        }

        fun write(byteArray: ByteArray) {
            val data = String(byteArray, Charset.defaultCharset())
            Log.d(TAG, "OutputStream message: $data")
            try {
                outputStream?.write(byteArray)
            } catch (e: IOException) {
                Log.d(TAG, "Exception in writing outputstream: ${e.message}")
            }
        }

        fun cancel() {
            Log.d(TAG, "ConnectedThread: closing...")
            try {
                mSocket?.close()
            } catch (e: IOException) {
                Log.d(TAG, "ConnectedThread: closing exception ${e.message}")
            }
        }
    }

    fun connected(bluetoothSocket: BluetoothSocket?, bluetoothDevice: BluetoothDevice?) {
        bluetoothSocket?.let {
            Log.d(TAG, "Connected: Starting...")
            mConnectedThread = ConnectedThread(it)
            mConnectedThread?.start()
        }
    }

    fun write(byteArray: ByteArray) {
        Log.d(TAG, "Connected: Starting...")
        mConnectedThread?.write(byteArray)
    }


    interface BluetoothServiceCallback {
        fun updateStatus(status: String)
    }
}