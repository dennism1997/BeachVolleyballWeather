package com.moumou.beachvolleyballweather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moumou.beachvolleyballweather.R
import com.moumou.beachvolleyballweather.weather.WeatherLocation
import kotlinx.android.synthetic.main.fragment_weather.*

class WeatherFragment(l : WeatherLocation) : WeatherFragmentAbstract() {

    init {
        location = l
    }

    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragments
        val view = inflater!!.inflate(R.layout.fragment_weather, container, false)

        iconResource = getString(R.string.wi_na)
        return view
    }

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeatherData()
        swipeRefreshLayout.setOnRefreshListener {
            getWeatherData()
        }
    }

}