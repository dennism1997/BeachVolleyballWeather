package com.moumou.beachvolleyballweather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val retrofit: Retrofit? = Retrofit.Builder().baseUrl("https://api.darksky.net/forecast/").build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTemperature(15)
        setPrecip(20)
    }

    fun setTemperature(temp: Int) {
        temperatureTextView.text = getString(R.string.temperatureLabel, temp)
    }

    fun setPrecip(prec: Int) {
        precipTextView.text = getString(R.string.precipLabel, prec)
    }


}
