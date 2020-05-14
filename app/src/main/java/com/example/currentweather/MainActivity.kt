package com.example.currentweather

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.currentweather.models.WeatherResponseModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private var currentWeather: WeatherResponseModel? = null
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("WEATHER_INFO", Context.MODE_PRIVATE)
        getSavedWeather(sharedPref)
        updateViews()

        load_weather.setOnClickListener {
            cityName = city_name_edit_text.text.toString().trim()

            GlobalScope.launch(Dispatchers.Main) {
                fetchAndShowResults()
            }
        }
    }

    suspend fun fetchWeather() {
        return GlobalScope.async(Dispatchers.IO) {
            val weatherJson = ApiConnectHelper().getJSONString(cityName!!)

            currentWeather = Gson().fromJson(weatherJson, WeatherResponseModel::class.java)

            val editor = getSharedPreferences("WEATHER_INFO", Context.MODE_PRIVATE).edit()
            editor.putString("WEATHER_JSON", weatherJson)
            editor.putString("CITY_NAME", cityName)
            editor.apply()
        }.await()
    }

    private suspend fun fetchAndShowResults() {
        fetchWeather() // fetch on IO thread
        updateViews() // back on UI thread
    }

    private fun updateViews() {
        if (currentWeather != null) {
            temperature_text.text = currentWeather!!.main.temp.toInt().toString()
            pressure_text.text = currentWeather!!.main.pressure.toString()
            humidity_text.text = currentWeather!!.main.humidity.toString()
            wind_text.text = currentWeather!!.wind.speed.toInt().toString()
        }
        if (cityName != null) {
            city_name_edit_text.setText(cityName)
        }
    }

    private fun getSavedWeather(sharedPref: SharedPreferences) {
        if (sharedPref.getString("WEATHER_JSON", null) != null) {
            currentWeather = Gson().fromJson(
                sharedPref.getString("WEATHER_JSON", null),
                WeatherResponseModel::class.java
            )
            cityName = sharedPref.getString("CITY_NAME", null)
        }
    }

}