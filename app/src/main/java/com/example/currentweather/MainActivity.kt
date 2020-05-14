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
import java.util.*

class MainActivity : AppCompatActivity() {
    var currentWeather: WeatherResponseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

                val sharedPref = getSharedPreferences("LOCATION", Context.MODE_PRIVATE)

        city_name_edit_text.text = sharedPref.getString("CITY_NAME", "")

        load_weather.setOnClickListener {
            val city_name = city_name_edit_text.text.toString().trim()
            val editor = sharedPref.edit()

            editor.putString("CITY_NAME", city_name)

            editor.apply()

            val job = CoroutineScope(Dispatchers.IO).launch {
                val weatherJson = ApiConnectHelper().getJSONString(city_name)
                Log.d("HTTP_JSON", weatherJson)
                currentWeather = Gson().fromJson(weatherJson, WeatherResponseModel::class.java)

                var temperature = currentWeather.main.temp.toString().trim()
                var pressure = currentWeather.main.pressure.toString().trim()
                var humidity = currentWeather.main.humidity.toString().trim()
                var wind = currentWeather.wind.speed.toString().trim()

                temperature_text.text = "$temperature"
                pressure_text.text = "$pressure hPa"
                humidity_text.text="$humidity %"
                wind_text.text="$wind km/h"

            }
            job.start()
            if (job.isCompleted) {
                job.cancel()
            }
        }
    }

}