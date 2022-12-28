package com.example.echo_m

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android_serialport_api.SerialPort
import android_serialport_api.SerialPortFinder
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    val SERIAL_PORT_NAME = "ttyS0"
    val SERIAL_BAUDRATE = 115200

    var serialPort: SerialPort? = null
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    //test1
    lateinit var serialThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OpenSerialPort(SERIAL_PORT_NAME)
        StartRxThread()
    }

    private fun OpenSerialPort(name: String){
        val serialPortFinder: SerialPortFinder = SerialPortFinder();
        val devices: Array<String> = serialPortFinder.allDevices
        val devicesPath: Array<String> = serialPortFinder.allDevicesPath

        for(device in devices){
            if(device.contains(name, true)){
                val index = devices.indexOf(device)
                serialPort = SerialPort(File(devicesPath.get(index)), SERIAL_BAUDRATE, 0)
                break;
            }
        }

        serialPort?.let{
            inputStream = it.inputStream
            outputStream = it.outputStream
        }
    }

    private fun StartRxThread(){
        if(inputStream == null) {
            Log.e("SerialExam", "Can't open inputstream")
            return
        }
        serialThread = Thread{
            while(true)
                try{
                    val buffer = ByteArray(64)
                    val size = inputStream?.read(buffer)
                    onReceiveData(buffer, size?:0)
                }catch (e: IOException) {e.printStackTrace()}
        }
        serialThread.start()
    }

    private fun onReceiveData(buffer: ByteArray, size: Int){
        if(size < 1) return

        var strBuilder = StringBuilder()
        for(i in 0 until size){
            strBuilder.append(String.format("%02x ", buffer[i]))
        }
        Log.d("SerialExam", "rx: " + strBuilder.toString())
    }

    private fun SendData(data: ByteArray){
        try{
            outputStream?.write(data)
        }catch (e:IOException) {e.printStackTrace()}
    }
}