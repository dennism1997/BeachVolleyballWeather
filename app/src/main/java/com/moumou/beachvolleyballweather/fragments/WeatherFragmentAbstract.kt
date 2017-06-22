package com.moumou.beachvolleyballweather.fragments

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.moumou.beachvolleyballweather.R
import com.moumou.beachvolleyballweather.SharedPreferencesHandler
import com.moumou.beachvolleyballweather.weather.Weather
import com.moumou.beachvolleyballweather.weather.WeatherCalculator
import kotlinx.android.synthetic.main.fragment_weather.*
import org.json.JSONException
import org.json.JSONObject

abstract class WeatherFragmentAbstract : Fragment() {

    private var metric : Boolean = true

    var location : Location = Location("dummy")
    var weather : Weather = Weather("dummy", 0.0, 0.0, 0.0, "Silicon Valley")
    var iconResource : String = ""
    var iconColor : Int = Color.BLACK
    var switch : Boolean = true

    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragments
        val view = inflater!!.inflate(R.layout.fragment_weather, container, false)

        iconResource = getString(R.string.wi_na)

        metric = WeatherCalculator.metric

        return view
    }

    fun setAnimations() {
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation : Animation?) {
            }

            override fun onAnimationEnd(animation : Animation?) {
                if (switch) {
                    recyclerview_item_icon.startAnimation(fadeOutAnimation)
                }
            }

            override fun onAnimationStart(animation : Animation?) {
            }
        })

        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation : Animation?) {
            }

            override fun onAnimationEnd(animation : Animation?) {
                if (switch) {

                    recyclerview_item_icon.startAnimation(fadeInAnimation)
                    if (weather.possible) {
                        recyclerview_item_icon.setIconColor(
                                ContextCompat.getColor(context,
                                                       R.color.weather_possible))
                    } else {
                        recyclerview_item_icon.setIconColor(
                                ContextCompat.getColor(context,
                                                       R.color.weather_not_possible))
                    }
                    switch = false

                }
            }

            override fun onAnimationStart(animation : Animation?) {
            }
        })

        recyclerview_item_icon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable?) {
                switch = true
                recyclerview_item_icon.startAnimation(fadeInAnimation)
            }

            override fun beforeTextChanged(s : CharSequence?,
                                           start : Int,
                                           count : Int,
                                           after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
            }

        })
    }

    fun setTemperature(temp : Double) {
        val s : String
        if (metric) {
            s = getString(R.string.temperatureLabel,
                          Math.round(temp).toString()) + getString(R.string.unit_celsius)
        } else {
            s = getString(R.string.temperatureLabel,
                          Math.round(WeatherCalculator.toFahrenheit(temp)).toString()) + getString(
                    R.string.unit_fahrenheit)
        }
        temperatureTextView.text = s
    }

    fun setPrecip(prec : Double) {
        val s = getString(R.string.precipLabel, Math.round(prec).toString()) + '%'
        precipTextView.text = s
    }

    fun setSummary(sum : String) {
        summaryTextView.text = sum
        summaryTextView.isSelected = true
    }

    fun setWindspeed(speed : Double) {
        val s : String
        if (metric) {
            s = getString(R.string.windspeedLabel,
                          Math.round(speed).toString()) + getString(R.string.unit_meters_per_second)
        } else {
            s = getString(R.string.windspeedLabel,
                          Math.round(WeatherCalculator.toMilesPerHour(speed)).toString()) + getString(
                    R.string.unit_miles_per_hour)
        }
        windTextView.text = s
    }

    fun setLocationLabel(location : String) {
        locationTextView.text = getString(R.string.locationLabel, location)
    }

    fun getWeatherData() {
        if (location.latitude != 0.0) {
            val url = getString(R.string.weatherUrl) +
                    location.latitude + "," + location.longitude +
                    getString(R.string.query_celsius) + getString(R.string.lang_query) +
                    getString(R.string.weather_lang)
            url.httpGet().responseJson { _, _, result ->
                print(result.toString())
                when (result) {
                    is Result.Success -> {
                        val json : JSONObject = result.get().obj()
                        parseResponse(json)
                        swipeRefreshLayout.isRefreshing = false

                    }
                    is Result.Failure -> {
                        result.getException().printStackTrace()
                        createDialog(result.error.localizedMessage)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

        }
    }

    fun parseResponse(json : JSONObject) {
        try {
            val data = json.getJSONObject("hourly").getJSONArray("data").getJSONObject(0)
            val temp = data.getDouble("apparentTemperature")
            val precip = data.getDouble("precipProbability")
            val windSpeed = data.getDouble("windSpeed")
            val summary = data.getString("summary")

            val city = SharedPreferencesHandler.getCity(activity,
                                                        location.latitude,
                                                        location.longitude)

            Log.d("location", city)

            weather = Weather(summary, temp, precip, windSpeed, city)
            val iconType : String = data.getString("icon")
            when (iconType.trim()) {
                "rain" -> {
                    iconResource = getString(R.string.wi_rain)
                    iconColor = ContextCompat.getColor(context, R.color.md_blue_500)
                }
                "clear-day" -> {
                    iconResource = getString(R.string.wi_day_sunny)
                    iconColor = ContextCompat.getColor(context, R.color.md_yellow_700)
                }
                "snow" -> {
                    iconResource = getString(R.string.wi_snow)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_300)
                }
                "partly-cloudy-day" -> {
                    iconResource = getString(R.string.wi_day_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_500)
                }
                "cloudy" -> {
                    iconResource = getString(R.string.wi_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_500)
                }
                "fog" -> {
                    iconResource = getString(R.string.wi_fog)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_500)
                }
                "sleet" -> {
                    iconResource = getString(R.string.wi_sleet)
                    iconColor = ContextCompat.getColor(context, R.color.md_blue_grey_300)
                }
                "clear-night" -> {
                    iconResource = getString(R.string.wi_night_clear)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_400)
                }
                "wind" -> {
                    iconResource = getString(R.string.wi_night_clear)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_400)
                }
                "partly-cloudy-night" -> {
                    iconResource = getString(R.string.wi_night_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_600)
                }
                else -> {
                    iconResource = getString(R.string.wi_na)
                    iconColor = Color.BLACK

                }
            }
            Log.d("WEATHER", (weather).weatherResult.toString())
            setValues()
        } catch (e : JSONException) {
            e.printStackTrace()
            createDialog("JSONException:" + e.localizedMessage)
        }
    }

    fun setValues() {
        setTemperature(weather.temperature)
        setSummary(weather.summary)
        setPrecip(weather.precipProb)
        setWindspeed(weather.windSpeed)
        setLocationLabel(weather.city)
        recyclerview_item_icon.setIconResource(iconResource)
        recyclerview_item_icon.setIconColor(iconColor)
        SharedPreferencesHandler.storeWeather(context, weather)
    }

    fun createDialog(message : String) {
        setValues()
        swipeRefreshLayout.isRefreshing = false
        AlertDialog.Builder(context).setMessage(message).setPositiveButton(
                R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    fun createDialog(message : Int) {
        createDialog(getString(message))
    }
}