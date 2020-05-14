package com.example.currentweather

import android.net.Uri
import android.util.Log
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ApiConnectHelper {


    companion object {
        const val KEY_API = "c327856571d5b2c211601f7820bbe71a"
    }


    private fun getUrlBytes(urlSpec: String): ByteArray {

        var url = URL(urlSpec)
        val connection = url.openConnection() as HttpsURLConnection

        try {
            val out = ByteArrayOutputStream()
            val input = connection.inputStream

            if (connection.responseCode != HttpsURLConnection.HTTP_OK) {
                throw IOException(connection.responseMessage)
            }

            var bytesRead: Int
            val buffer = ByteArray(1024)

            do {
                bytesRead = input.read(buffer)

                out.write(buffer, 0, bytesRead)
            } while (input.read(buffer) > 0)
            out.close()

            return out.toByteArray()

        } catch (e: IOException) {
            Log.e("ERROR_HTTP_CONNECTION", e.message)
            return ByteArray(0)
        } finally {
            connection.disconnect()
        }

    }

    private fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    fun getJSONString(city: String): String {

        var jsonString = "Something's go wrong"

        //  api.openweathermap.org/data/2.5/weather?q=London&appid=c327856571d5b2c211601f7820bbe71a
        try {
            val url: String = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                .buildUpon()
                .appendQueryParameter("q", city)
                .appendQueryParameter("appid", KEY_API)
                .appendQueryParameter("units", "metric")
                .build().toString()

            jsonString = getUrlString(url)

        } catch (jsonException: JSONException) {
            Log.e("JSON_ERROR", jsonException.message)
        }

        return jsonString

    }


}