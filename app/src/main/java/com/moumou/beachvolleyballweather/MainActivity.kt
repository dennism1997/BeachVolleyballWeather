package com.moumou.beachvolleyballweather

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //    private var locationString : String = getString(R.string.defaultLatLong)
    private val RC_LOCATION_PERM = 9001
    private var currentLocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    var weather: Weather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RC_LOCATION_PERM)
        } else {
            getLocation()
        }

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
        }

        swipeRefreshLayout.setOnRefreshListener {
            getLocation()
        }
    }

    override fun onStart() {
        getLocation()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient?.disconnect()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings_action -> {
                fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConnected(p0: Bundle?) {
        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (currentLocation != null) {
                getWeatherData()
            } else {
                createDialog(R.string.no_location)
            }
        } catch (e: java.lang.SecurityException) {
            e.printStackTrace()
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RC_LOCATION_PERM)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        createDialog("Couldn't retrieve location: " + p0.errorMessage)
    }

    fun getLocation() {
        googleApiClient?.disconnect()
        googleApiClient?.connect()
    }

    fun setTemperature(temp: Double) {
        temperatureTextView.text = getString(R.string.temperatureLabel, temp.toString())
    }

    fun setPrecip(prec: Double) {
        precipTextView.text = getString(R.string.precipLabel, prec.toString())
    }

    fun setSummary(sum: String) {
        summaryTextView.text = sum
    }

    fun getWeatherData() {
        if (currentLocation != null) {
            val url = getString(R.string.weatherUrl) + currentLocation?.latitude + "," + currentLocation?.longitude + getString(R.string.celcius)
            url.httpGet().responseJson { _, _, result ->
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
    }

    fun parseResponse(json: JSONObject) {
        try {
            val daily = json.getJSONObject("daily")
            val data = daily.getJSONArray("data").getJSONObject(0)
            val temp = data.getDouble("apparentTemperatureMax")
            val precip = data.getDouble("precipProbability")
            val windSpeed = data.getDouble("windSpeed")
            val summary = data.getString("summary")
            weather = Weather(summary, temp, precip, windSpeed)
            val iconType: String = daily.getString("icon")
            when (iconType.trim()) {
                "rain" -> {
                    weatherIconView.setIconResource(getString(R.string.wi_rain))
                    weatherIconView.setIconColor(R.color.md_blue_500)
                }
                "clear-day" -> weatherIconView.setIconResource(getString(R.string.wi_day_sunny))
                "snow" -> weatherIconView.setIconResource(getString(R.string.wi_snow))
                "partly-cloudy-day" -> weatherIconView.setIconResource(getString(R.string.wi_day_cloudy))
                "cloudy" -> weatherIconView.setIconResource(getString(R.string.wi_cloudy))
                "fog" -> weatherIconView.setIconResource(getString(R.string.wi_fog))
                "sleet" -> weatherIconView.setIconResource(getString(R.string.wi_sleet))
                "clear-night" -> weatherIconView.setIconResource(getString(R.string.wi_night_clear))
                "wind" -> weatherIconView.setIconResource(getString(R.string.wi_night_clear))
                "partly-cloudy-night" -> weatherIconView.setIconResource(getString(R.string.wi_night_cloudy))
                else -> weatherIconView.setIconResource(getString(R.string.wi_na))
            }
            setTemperature(temp)
            setSummary(summary)
            setPrecip(precip)
            Log.d("WEATHER", calculateWeather(weather).toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            createDialog("JSONException:" + e.localizedMessage)
        }
    }

    fun createDialog(message: String) {
        AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

    fun createDialog(message: Int) {
        AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

    data class Weather(val summary: String, val temperature: Double, val precipProb: Double, val windSpeed: Double)

    fun calculateWeather(weather: Weather?): Double {
        if (weather != null) {
            val temperatureFactor: Double = (-0.0215 * Math.pow(weather.temperature, 3.0)) + 0.8754 * Math.pow(weather.temperature, 2.0) + (-4.8251 * weather.temperature) + 7.7724
            val windFactor: Double = 1.0
            val precipFactor: Double = 0.0003 * Math.pow(weather.precipProb, 3.0) + (-0.052 * Math.pow(weather.precipProb, 2.0)) + 1.0299 * weather.precipProb + 96.6506
            val total: Double = .5 * temperatureFactor + .2 * windFactor + .3 * precipFactor
            return total
        }
        return 0.0
    }

}
