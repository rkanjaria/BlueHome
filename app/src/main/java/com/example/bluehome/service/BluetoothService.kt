package com.example.bluehome.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.bluehome.classes.Status
import com.example.bluehome.classes.UUID_INSECURE
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


class BluetoothService(private val mCallback: BluetoothServiceCallback?) {

    private var mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mAcceptThread: AcceptThread? = null
    private var mConnectThread: ConnectThread? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    private var deviceUUID: UUID? = null

    private var mConnectedThread: ConnectedThread? = null


    init {
        start()
    }

    companion object {
        const val TAG = "BluetoothService"
        const val appName = "BlueHome"
    }

    private inner class AcceptThread : Thread() {
        private var mServerSocket: BluetoothServerSocket? = null

        init {
            var tmp: BluetoothServerSocket? = null
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, UUID_INSECURE)
                Log.d(TAG, "AcceptThread: Setting up Server using: $UUID_INSECURE")
            } catch (e: IOException) {
                Log.d(TAG, "AcceptThread: IOException: " + e.message)
                mCallback?.updateStatus(Status.FAILED)
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
                connected(it)
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
            var tmp: BluetoothSocket? = null
            try {
                tmp = mBluetoothDevice?.createInsecureRfcommSocketToServiceRecord(deviceUUID)
                //tmp = mBluetoothDevice?.createRfcommSocketToServiceRecord(deviceUUID)
            } catch (e: IOException) {
                mCallback?.updateStatus(Status.FAILED)
            }

            mSocket = tmp
            mBluetoothAdapter.cancelDiscovery()

            try {
                mSocket?.connect()
                // Here the bluetooth device is connected
                mCallback?.updateStatus(Status.CONNECTED)

            } catch (e: IOException) {
                try {
                    mSocket?.close()
                    mCallback?.updateStatus(Status.FAILED)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            mSocket?.let { connected(it) }
        }

        fun cancel() {
            try {
                mSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    @Synchronized
    fun start() {
        mConnectThread?.let { it.cancel() }
        mConnectThread = null
        if (mAcceptThread == null) {
            mAcceptThread = AcceptThread()
            mAcceptThread?.start()
        }
    }

    fun startClient(bluetoothDevice: BluetoothDevice, uuid: UUID) {
        Log.d(TAG, "StartCliend: started...")
        mConnectThread = ConnectThread(bluetoothDevice, uuid)
        mConnectThread?.start()
        mCallback?.updateStatus(Status.CONNECTING)
    }

    private inner class ConnectedThread(bluetoothSocket: BluetoothSocket) : Thread() {

        private var mSocket: BluetoothSocket? = null
        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null

        init {
            mSocket = bluetoothSocket
            var tempInputStream: InputStream? = null
            var tempOutputStream: OutputStream? = null

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
            var bytes: Int?

            while (true) {
                try {
                    bytes = inputStream?.read(buffer)
                    val incomingMessage = bytes.toString()
                    Log.d(TAG, "Incoming message: $incomingMessage")
                } catch (e: IOException) {
                    Log.d(TAG, "Exception in reading inputstream: ${e.message}")
                    mCallback?.updateStatus(Status.FAILED)
                    e.printStackTrace()
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
                mCallback?.updateStatus(Status.FAILED)
            }
        }

        fun cancel() {
            try {
                inputStream?.close()
                outputStream?.close()
                mSocket?.close()
            } catch (e: IOException) {
            }
        }
    }

    fun connected(bluetoothSocket: BluetoothSocket?) {
        bluetoothSocket?.let {
            mConnectedThread = ConnectedThread(it)
            mConnectedThread?.start()
        }
    }

    fun write(byteArray: ByteArray) {
        mConnectedThread?.write(byteArray)
    }

    fun cancel() {
        mAcceptThread?.cancel()
        mConnectThread?.cancel()
        mConnectedThread?.cancel()
    }

    interface BluetoothServiceCallback {
        fun updateStatus(status: Status)
    }
}