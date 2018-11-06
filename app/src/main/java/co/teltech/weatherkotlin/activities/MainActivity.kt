package co.teltech.weatherkotlin.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import co.teltech.weatherkotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val LOG_TAG: String = "FETCHING_WEATHER"

    private val OPEN_WEATHER_API: String = "http://api.openweathermap.org/data/2.5/weather?q=[QUERY]&appid=ffee28b9ec9430dc8d18e4c8a3d69854&units=metric"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fetchWeather(v: View) {
        weatherData.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        if (searchField.text.toString().equals("")) {
            Toast.makeText(this, getString(R.string.toast_enter_city_name), Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        } else {
            val url = OPEN_WEATHER_API.replace("[QUERY]", searchField.text.toString())
            val request = Request.Builder()
                .url(url)
                .build()

            var client: OkHttpClient = OkHttpClient()
            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(LOG_TAG, e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    val jsonObject: JSONObject = JSONObject(responseBody)

                    if (jsonObject.has("message")) {
                        val message = jsonObject.getString("message")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    } else {
                        val weatherDescriptionArray = jsonObject.getJSONArray("weather")
                        val weatherDescription = weatherDescriptionArray.getJSONObject(0)
                        val weatherDataObject = jsonObject.getJSONObject("main")

                        val cityName = jsonObject.getString("name")
                        val weatherDescriptionText = weatherDescription.getString("description")
                        val currentTemp = weatherDataObject.getDouble("temp").toString()

                        runOnUiThread {
                            labelCityName.text = cityName
                            labelWeatherDescription.text = weatherDescriptionText
                            labelCurrentTemp.text = currentTemp

                            progressBar.visibility = View.GONE
                            weatherData.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }
    }
}
