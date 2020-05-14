package com.example.currentweather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.currentweather.models.WeatherResponseModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var currentWeather: WeatherResponseModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("LOCATION", Context.MODE_PRIVATE)

        city_name_edit_text.setText(sharedPref.getString("CITY_NAME", ""))
        temperature_text.text = sharedPref.getString("TEMPERATURE", "temp")
        pressure_text.text = sharedPref.getString("PRESSURE", "pres")
        humidity_text.text = sharedPref.getString("HUMIDITY", "hum")
        wind_text.text = sharedPref.getString("WIND", "wind")

        //todo usunąc powtórzenia, dodać datę i naprawić bo wywala

        load_weather.setOnClickListener {
            val city_name = city_name_edit_text.text.toString().trim()
            val editor = sharedPref.edit()

            editor.putString("CITY_NAME", city_name)

            editor.apply()

            val job = CoroutineScope(Dispatchers.IO).launch {
                val weatherJson = ApiConnectHelper().getJSONString(city_name)
                Log.d("HTTP_JSON", weatherJson)
                currentWeather = Gson().fromJson(weatherJson, WeatherResponseModel::class.java)

                val temperature = currentWeather!!.main.temp.toString().trim()
                val pressure = currentWeather!!.main.pressure.toString().trim()
                val humidity = currentWeather!!.main.humidity.toString().trim()
                val wind = currentWeather!!.wind.speed.toString().trim()

                editor.putString("TEMPERATURE", temperature)
                editor.putString("PRESSURE", pressure)
                editor.putString("HUMIDITY", humidity)
                editor.putString("WIND", wind)
                editor.apply()

                temperature_text.text = sharedPref.getString("TEMPERATURE", "temp")
                pressure_text.text = sharedPref.getString("PRESSURE", "pres")
                humidity_text.text = sharedPref.getString("HUMIDITY", "hum")
                wind_text.text = sharedPref.getString("WIND", "wind")

            }
            job.start()
            if (job.isCompleted) {
                job.cancel()
            }
        }
    }

}