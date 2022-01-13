package com.example.koordinatrobuddy

import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LocationLogger {
    companion object{
        fun write(longitude:Double,latitude:Double){
            val date = Calendar.getInstance().time as Date
            val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate = dateformat.format(date)
            val line = String.format("$longitude, $latitude, $formattedDate")
            val state = Environment.getExternalStorageState()
            if(state.equals(Environment.MEDIA_MOUNTED)){
                val file = File(Environment.getExternalStorageDirectory(),"qps_data.csv")
                val bf = BufferedWriter(FileWriter(file,true))
                bf.append(line)
                bf.append(System.lineSeparator())
                bf.close()
            }
        }
    }
}