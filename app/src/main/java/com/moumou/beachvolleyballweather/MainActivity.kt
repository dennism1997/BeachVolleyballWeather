package com.moumou.beachvolleyballweather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

//    private var locationString : String = getString(R.string.defaultLatLong)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout.setOnRefreshListener {
            getWeatherData()
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherData()
    }

    fun setTemperature(temp: String) {
        temperatureTextView.text = getString(R.string.temperatureLabel, temp)
    }

    fun setPrecip(prec: String) {
        precipTextView.text = getString(R.string.precipLabel, prec)
    }

    fun getWeatherData() {
        val url = getString(R.string.weatherUrl) + getString(R.string.defaultLatLong) + getString(R.string.defaultQuery)
        url.httpGet().responseJson { request, response, result ->
            print(result.toString())
            when (result) {
                is Result.Success -> {
                    val json: JSONObject = result.get().obj()
                    parseResponse(json)
                    swipeRefreshLayout.isRefreshing = false

                }
                is Result.Failure -> {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    fun parseResponse(json: JSONObject) {
        val temp = json.getJSONObject("currently").get("apparentTemperature").toString()
        val precip = json.getJSONObject("currently").get("precipProbability").toString()
        setTemperature(temp)
        setPrecip(precip)
    }

}
