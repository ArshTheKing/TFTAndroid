package com.example.myapplication

import android.app.ProgressDialog
import android.bluetooth.*
import android.content.Context
import android.location.Address
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object{
        var myUUID = UUID.randomUUID()
        var socket: BluetoothSocket?=null
        lateinit var progress:ProgressDialog
        lateinit var adapter: BluetoothAdapter
        var connected: Boolean=false
        lateinit var address: String
    }
    var rssiRead: Int? =null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        address= intent.getStringExtra(MainActivity.EXTRA_ADDRESS)!!
        ConnectToDevice(this).execute()
        val control_on = findViewById<Button>(R.id.control_on)
        control_on.setOnClickListener{sendCommand("a")}
        val control_off = findViewById<Button>(R.id.control_off)
        control_off.setOnClickListener{sendCommand("b")}
        val control_dc = findViewById<Button>(R.id.control_dc)
        control_dc.setOnClickListener{disconect()}
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun sendCommand(data: String){
        if (socket!=null)
            try {
                BluetoothDevice.EXTRA_RSSI
                val device= BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
                val connectGatt = device.connectGatt(this, true, object : BluetoothGattCallback() {
                    override fun onConnectionStateChange(
                        gatt: BluetoothGatt,
                        status: Int,
                        newState: Int
                    ) {
                        super.onConnectionStateChange(gatt, status, newState)
                    }

                    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
                        rssiRead = rssi
                    }
                })
                connectGatt.readRemoteRssi()
                println(rssiRead.toString())
                socket!!.outputStream.write(rssiRead.toString().toByteArray())

            } catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this,data, Toast.LENGTH_LONG).show()
            }
    }
    private fun disconect(){
        if (socket !=null) {
            try {
                socket!!.outputStream.write("exit".toByteArray())
                socket!!.close()
                socket = null
                connected=false
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
        Toast.makeText(this,"disconected", Toast.LENGTH_LONG).show()
    }
    private fun updateUI(case:Int){
        var txt=this.findViewById<Button>(R.id.conStatus)
        if(case==1) txt.text="connected"
        else    txt.text="not connected"
    }

    private class ConnectToDevice(c: Context): AsyncTask<Void,Void,String>() {
        private var connectSuccess:Boolean =true
        private val context:Context
        init {
            context= c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress= ProgressDialog.show(context, "Connecting...", "Please wait")
        }
    override fun doInBackground(vararg p0: Void?): String? {
           try {
                if (socket==null||!connected){
                    adapter=BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = adapter.getRemoteDevice(address)
                    val fromString = UUID.fromString("7f49f6fa-12e5-11ec-82a8-0242ac130003")
                    socket= device.createRfcommSocketToServiceRecord(fromString)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    socket!!.connect()
           /*
            */
                }
            } catch (e:IOException){
                println(e.stackTrace.toString())
                Toast.makeText(this.context, "Connection rejected", Toast.LENGTH_SHORT).show()
            }
            return null
         }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            connected=true

           progress.dismiss()
        }

    }
}