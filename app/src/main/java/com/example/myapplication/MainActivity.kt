package com.example.myapplication

import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val rqEnable = 1
    var btAdapter: BluetoothAdapter? = null
    companion object{
        var EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val manager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            manager.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }
        if (btAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,rqEnable)
        }

        var data: dataHandler = dataHandler()
        var battery = this.findViewById<TextView>(R.id.battery)
        var refreshBt = this.findViewById<Button>(R.id.refresh)
        data.loadBattery(battery, this)
        refreshBt.setOnClickListener { pairedDeviceList() }

    }

    private fun pairedDeviceList() {
        var dev = findViewById<ListView>(R.id.devices)

        val bondedDevices = btAdapter!!.bondedDevices
        val list = ArrayList<CustomDevice>()
        bondedDevices?.forEach{
                device ->
            val deviceName = device.address
            val deviceAddress = device.name
            //Toast.makeText(this,deviceName+" "+deviceAddress, Toast.LENGTH_LONG).show()
            list.add(CustomDevice(device))
        }
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,list)
        dev.adapter=adapter
        dev.onItemClickListener= AdapterView.OnItemClickListener{_,_, position, _ ->
            val device:CustomDevice = list[position]
            val address= device.dev.address
            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==rqEnable)
            /*rqEnable ->*/
                if (resultCode == Activity.RESULT_OK)
                    Toast.makeText(this, "Bluetooth is on",Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Cant turn Bluetooth on",Toast.LENGTH_LONG).show()


    }
    /**








     */
}