package com.moumou.beachvolleyballweather

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_main.*
import org.json.JSONException
import org.json.JSONObject

class MainFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val RC_LOCATION_PERM = 9001

    //    private var locationString : String = getString(R.string.defaultLatLong)
    private var currentLocation : Location? = null
    private var googleApiClient : GoogleApiClient? = null
    var weather : Weather? = null
    var iconResource : String = ""
    var iconColor : Int = Color.BLACK
    var switch : Boolean = true

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
        }

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RC_LOCATION_PERM)
        } else {
            getLocation()
        }
    }

    override fun onCreateView(inflater : LayoutInflater?, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        iconResource = getString(R.string.wi_na)
        return view
    }

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            getLocation()
        }

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation : Animation?) {
            }

            override fun onAnimationEnd(animation : Animation?) {
                if (switch) {
                    weatherIconView.startAnimation(fadeOutAnimation)
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

                    weatherIconView.startAnimation(fadeInAnimation)
                    if (weather?.possible as Boolean) {
                        weatherIconView.setIconColor(ContextCompat.getColor(context, R.color.weather_possible))
                    } else {
                        weatherIconView.setIconColor(ContextCompat.getColor(context, R.color.weather_not_possible))
                    }
                    switch = false

                }
            }

            override fun onAnimationStart(animation : Animation?) {
            }
        })

        weatherIconView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable?) {
                switch = true
                weatherIconView.startAnimation(fadeInAnimation)
            }

            override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
            }

        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        googleApiClient?.disconnect()
        super.onStop()
    }

    override fun onConnected(p0 : Bundle?) {
        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (currentLocation != null) {
                getWeatherData()
            } else {
                createDialog(R.string.no_location)
            }
        } catch (e : java.lang.SecurityException) {
            e.printStackTrace()
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RC_LOCATION_PERM)
        }
    }

    override fun onConnectionSuspended(p0 : Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0 : ConnectionResult) {
        createDialog("Couldn't retrieve location: " + p0.errorMessage)
    }

    fun getLocation() {
        googleApiClient?.disconnect()
        googleApiClient?.connect()
    }

    fun setTemperature(temp : Double) {
        temperatureTextView.text = getString(R.string.temperatureLabel, temp.toString())
    }

    fun setPrecip(prec : Double) {
        precipTextView.text = getString(R.string.precipLabel, prec.toString())
    }

    fun setSummary(sum : String) {
        summaryTextView.text = sum
    }

    fun getWeatherData() {
        if (currentLocation != null) {
            val url = getString(R.string.weatherUrl) + currentLocation?.latitude + "," + currentLocation?.longitude + getString(R.string.celcius)
            url.httpGet().responseJson { _, _, result ->
                print(result.toString())
                when (result) {
                    is Result.Success -> {
                        val json : JSONObject = result.get().obj()
                        parseResponse(json)
                        swipeRefreshLayout.isRefreshing = false

                    }
                    is Result.Failure -> {
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
            val result = calculateWeather(temp, precip)
            weather = Weather(summary, temp, precip, windSpeed, result > 1.0, result)
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
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_50)
                }
                "partly-cloudy-day" -> {
                    iconResource = getString(R.string.wi_day_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_200)
                }
                "cloudy" -> {
                    iconResource = getString(R.string.wi_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_300)
                }
                "fog" -> {
                    iconResource = getString(R.string.wi_fog)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_600)
                }
                "sleet" -> {
                    iconResource = getString(R.string.wi_sleet)
                    iconColor = ContextCompat.getColor(context, R.color.md_blue_grey_300)
                }
                "clear-night" -> {
                    iconResource = getString(R.string.wi_night_clear)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_300)
                }
                "wind" -> {
                    iconResource = getString(R.string.wi_night_clear)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_200)
                }
                "partly-cloudy-night" -> {
                    iconResource = getString(R.string.wi_night_cloudy)
                    iconColor = ContextCompat.getColor(context, R.color.md_grey_400)
                }
                else -> {
                    iconResource = getString(R.string.wi_na)
                    iconColor = Color.BLACK

                }
            }
            weatherIconView.setIconResource(iconResource)
            weatherIconView.setIconColor(iconColor)
            setTemperature(temp)
            setSummary(summary)
            setPrecip(precip)
            Log.d("WEATHER", (weather as Weather).weatherResult.toString())
        } catch (e : JSONException) {
            e.printStackTrace()
            createDialog("JSONException:" + e.localizedMessage)
        }
    }

    fun createDialog(message : String) {
        AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

    fun createDialog(message : Int) {
        AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

    data class Weather(val summary : String, val temperature : Double, val precipProb : Double, val windSpeed : Double, val possible : Boolean, val weatherResult : Double)

    fun calculateWeather(temperature : Double, precipProb : Double) : Double {
        val temperatureFactor : Double = (-0.0215 * Math.pow(temperature, 3.0)) + 0.8754 * Math.pow(temperature, 2.0) + (-4.8251 * temperature) + 7.7724
        val windFactor : Double = 1.0
        val precipFactor : Double = 0.0003 * Math.pow(precipProb, 3.0) + (-0.052 * Math.pow(precipProb, 2.0)) + 1.0299 * precipProb + 96.6506
        val total : Double = .5 * temperatureFactor + .2 * windFactor + .3 * precipFactor
        return total
    }

}
