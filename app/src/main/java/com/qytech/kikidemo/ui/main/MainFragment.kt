package com.qytech.kikidemo.ui.main

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.qytech.kikidemo.databinding.MainFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainFragment : Fragment(), CoroutineScope {

    companion object {

        fun newInstance() = MainFragment()

        const val BODY_VENDOR_ID = 1155
        const val BODY_PRODUCT_ID = 0
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

        private const val LED_OFF =
            "0x4 0x3 0x0 0x0 0x0 0x0 0x3 0x10 0x0 0x0 0xff 0x4 0x10 0x0 0xff 0x0 0x5 0x10 0xff 0x0 0x0"
        private const val LED_RED =
            "0x4 0x6 0x0 0x0 0x0 0x0 0x0 0x10 0xff 0x0 0x0 0x1 0x10 0xff 0x0 0x0 0x2 0x10 0xff 0x0 0x0 0x3 0x10 0xff 0x0 0x0 0x4 0x10 0xff 0x0 0x0 0x5 0x10 0xff 0x0 0x0"
        private const val LED_GREEN =
            "0x4 0x6 0x0 0x0 0x0 0x0 0x0 0x10 0x0 0xff 0x0 0x1 0x10 0x0 0xff 0x0 0x2 0x10 0x0 0xff 0x0 0x3 0x10 0x0 0xff 0x0 0x4 0x10 0x0 0xff 0x0 0x5 0x10 0x0 0xff 0x0"
        private const val LED_BLUE =
            "0x4 0x6 0x0 0x0 0x0 0x0 0x0 0x10 0x0 0x0 0xff 0x1 0x10 0x0 0x0 0xff 0x2 0x10 0x0 0x0 0xff 0x3 0x10 0x0 0x0 0xff 0x4 0x10 0x0 0x0 0xff 0x5 0x10 0x0 0x0 0xff"

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    private lateinit var viewModel: MainViewModel
    private lateinit var dataBinding: MainFragmentBinding

    private var usbManager: UsbManager? = null
    private var usbInterface: UsbInterface? = null
    private var deviceConnection: UsbDeviceConnection? = null
    private val usbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    (intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?)?.let { device ->
                        launch(Dispatchers.IO) {
                            if (intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                    false
                                )
                            ) {
                                Timber.d("onReceive message: interfaceCount ${device.interfaceCount}")
                                usbInterface = device.getInterface(0)
                                usbManager?.openDevice(device)?.let { connect ->
                                    val result = connect.claimInterface(usbInterface, true)
                                    Timber.d("onReceive message: claimInterface result is $result ")
                                    if (!result) {
                                        connect.close()
                                    } else {
                                        deviceConnection = connect
                                        testLeds()
                                    }
                                }
                            } else {
                                Timber.d("permission denied for device $device")
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun testLeds() {
        sendToUsb(strToHexByteArray(LED_RED))
        delay(1000L)
        sendToUsb(strToHexByteArray(LED_GREEN))
        delay(1000L)
        sendToUsb(strToHexByteArray(LED_BLUE))
        delay(1000L)
        sendToUsb(strToHexByteArray(LED_OFF))
    }

    private fun strToHexByteArray(str: String): ByteArray {
        val arr = str.split(' ')
        return ByteArray(arr.size) { index ->
            arr[index].replace("0x", "").toInt(radix = 16).toByte()
        }
    }

    private fun sendToUsb(content: ByteArray) {
        //UsbConstants#USB_DIR_OUT
        val usbEpOut = usbInterface?.getEndpoint(0)
        //UsbConstants#USB_DIR_IN
        val usbEpIn = usbInterface?.getEndpoint(1)
        Timber.d("sendToUsb message: sendBytes ${content.contentToString()} ")
        val ret = deviceConnection?.bulkTransfer(usbEpOut, content, content.size, 0)
        Timber.d("sendToUsb message: result is $ret ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = MainFragmentBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewmodel = viewModel

        usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        val permissionIntent =
            PendingIntent.getBroadcast(requireContext(), 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        requireContext().registerReceiver(usbReceiver, filter)

        usbManager?.deviceList?.values?.forEach { device ->
            if (BODY_VENDOR_ID == device.vendorId && BODY_PRODUCT_ID == device.productId) {
                usbManager?.requestPermission(device, permissionIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deviceConnection?.close()
    }
}