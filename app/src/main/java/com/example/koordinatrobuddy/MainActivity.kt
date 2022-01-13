package com.example.koordinatrobuddy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.koordinatrobuddy.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    lateinit var timer: Timer
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    lateinit var locationManager: LocationManager
    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitude = location.longitude
            latitude = location.latitude
        }
    }


    var writePermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bind.textViewGPS.text = "Fájlművelet inaktív!"
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this,permissions,0)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bind.textViewGPS.text = "Helymeghatározás inaktív!"
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this,permissions,0)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 0){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                bind.textViewGPS.text = "Helymeghatározás inaktív!"
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                ActivityCompat.requestPermissions(this,permissions,1)
            }
          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run(){
                timerMethod()
            }
        },1000,5000)
    }

    fun timerMethod(){
        this.runOnUiThread(timerTick)
    }

    var timerTick = object:Runnable{
        override fun run(){
            val longi = this@MainActivity.getString(R.string.longitude)
            val latti = this@MainActivity.getString(R.string.Latitude)
            "$longi: $longitude \n$latti: $latitude".also { bind.textViewGPS.text = it }
            try{
                LocationLogger.write(longitude,latitude)
            }
            catch (e:Exception){
                Log.e("Állapot","Sikertelen")
                e.printStackTrace()
            }
        }
    }
}