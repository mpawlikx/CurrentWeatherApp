package com.example.currentweather

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.currentweather.models.WeatherResponseModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var currentWeather: WeatherResponseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    val sharedPref= getSharedPreferences("LOCATION", Context.MODE_PRIVATE)
        load_weather.setOnClickListener {
            val city_name= city_name_edit_text.text.toString()
            val editor= sharedPref.edit()

            editor.putString("CITY_NAME",city_name)

            editor.apply()

            val job = CoroutineScope(Dispatchers.IO).launch {
                val weatherJson = ApiConnectHelper().getJSONString("London")
                Log.d("HTTP_JSON", weatherJson)
                currentWeather = Gson().fromJson(weatherJson, WeatherResponseModel::class.java)

                val temperature = currentWeather.main.temp
                val pressure = currentWeather.main.pressure
                val humidity = currentWeather.main.humidity
                val wind = currentWeather.wind.speed

            }
            job.start()
            if (job.isCompleted) {
                job.cancel()
            }
        }
        }

    }