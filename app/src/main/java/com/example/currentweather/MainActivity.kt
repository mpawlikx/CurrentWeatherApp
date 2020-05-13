package com.example.currentweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val job= CoroutineScope(Dispatchers.IO).launch {
            Log.d("HTTP_JSON",ApiConnectHelper().getJSONString("London"))
        }
        job.start()
        if (job.isCompleted){
            job.cancel()
        }
    }
}
