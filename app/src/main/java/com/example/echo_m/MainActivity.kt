package com.example.echo_m

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.echo_m.databinding.ActivityMainBinding
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException
import java.lang.Thread.interrupted
import java.math.BigInteger


class MainActivity : AppCompatActivity() {

    //test1
    var port:UsbSerialPort ?= null
    lateinit var serialThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // 바인딩 초기화
        val binding = ActivityMainBinding.inflate(layoutInflater)
        // 레이아웃(root뷰) 표시
        setContentView(binding.root)

        //setContentView(R.layout.activity_main);
        registerReceiver(mUsbDeviceReceiver, IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED))

        binding.button2.setOnClickListener{

            var temp:String = "AE01"
            var temp2:ByteArray = temp.decodeHex()
            port?.write(temp2,100)
        }

        binding.button.setOnClickListener{

            val manager = getSystemService(USB_SERVICE) as UsbManager
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) {
                return@setOnClickListener;
            }
            val driver = availableDrivers[0]
            val connection = manager.openDevice(driver.device)
            val hasPermision = manager.hasPermission(driver.device)
            if (connection == null) {
                //UsbManager.requestPermission(driver.getDevice(), mPendingIntent)
                // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                return@setOnClickListener
            }
            port = driver.ports[0] // Most devices have just one port (port 0)

            try {
                port?.open(connection)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                port?.setParameters(921600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            serialThread.start()
        }

        serialThread = Thread() {
            var mKillSign = false

            // stop this thread
            fun setKillSign(isTrue: Boolean) {
                mKillSign = true
            }
            while(mKillSign == false)
            {
                val readBuffer = ByteArray(4096)
                while (!interrupted()) {
                    try {
                        // Read and Display to Terminal
                        val numBytesRead: Int = port?.read(readBuffer, 2000) ?: -1


                        if (numBytesRead > 0) {
                            val Hex_Value: String =
                                getByte_To_HexString(readBuffer, numBytesRead)
                            val Hex_Value1: String =
                                getByte_To_HexString(readBuffer, numBytesRead)
                        }
                    } catch (e: IOException) {
                    }
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                    if (mKillSign) break
                }
            }
        }


    }



    private val mUsbDeviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
            }
        }
    }

    fun getByte_To_HexString(buf: ByteArray, size: Int): String {
        var Hex_Value = ""
        for (i in 0 until size) {
            Hex_Value += String.format("0x%02x ", buf[i]) //x가 소문자면 소문자 출력, 대문자면 대문자 출력
        }
        return Hex_Value
    }

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        val byteIterator = chunkedSequence(2)
            .map { it.toInt(16).toByte() }
            .iterator()

        return ByteArray(length / 2) { byteIterator.next() }
    }


}
