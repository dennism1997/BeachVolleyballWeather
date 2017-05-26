package com.moumou.beachvolleyballweather

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

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

    fun setTemperature(temp: String) {
        temperatureTextView.text = getString(R.string.temperatureLabel, temp)
    }

    fun setPrecip(prec: String) {
        precipTextView.text = getString(R.string.precipLabel, prec)
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
            val temp = data.getString("apparentTemperatureMax")
            val precip = data.getString("precipProbability")
            val summary = data.getString("summary")
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
        } catch (e : JSONException) {
            e.printStackTrace()
            createDialog("JSon Parser ")
        }
    }

    fun createDialog(message: String) {
        AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.ok) { dialog, which ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

    fun createDialog(message: Int) {
        AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.ok) { dialog, which ->
            dialog.dismiss()
            swipeRefreshLayout.isRefreshing = false
        }.show()
    }

}
