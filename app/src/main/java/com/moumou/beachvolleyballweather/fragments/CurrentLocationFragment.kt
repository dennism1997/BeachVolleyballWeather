package com.moumou.beachvolleyballweather.fragments

import android.Manifest
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.moumou.beachvolleyballweather.R
import com.moumou.beachvolleyballweather.weather.WeatherLocation
import kotlinx.android.synthetic.main.fragment_weather.*
import java.util.*

class CurrentLocationFragment : WeatherFragmentAbstract(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val RC_LOCATION_PERMISSION = 9001

    private var googleApiClient : GoogleApiClient? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context).addConnectionCallbacks(
                    this).addOnConnectionFailedListener(
                    this).addApi(LocationServices.API).build()
        }

    }

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            getLocation()
        }

        setAnimations()
    }

    override fun onStart() {
        super.onStart()
        getLocation()
    }

    override fun onStop() {
        googleApiClient?.disconnect()
        super.onStop()
    }

    override fun onConnected(p0 : Bundle?) {
        try {
            val loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (loc != null) {
                val city = getCity(loc.latitude, loc.longitude)
                location = WeatherLocation(loc.latitude, loc.longitude, city)
                getWeatherData()
            } else {
                createDialog(R.string.no_location)
            }
        } catch (e : SecurityException) {
            e.printStackTrace()
            ActivityCompat.requestPermissions(activity,
                                              arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                              RC_LOCATION_PERMISSION)
        }
    }

    override fun onConnectionSuspended(p0 : Int) {
    }

    override fun onConnectionFailed(p0 : ConnectionResult) {
        createDialog("Couldn't retrieve location: " + p0.errorMessage)
    }

    fun getLocation() {
        googleApiClient?.disconnect()
        googleApiClient?.connect()
    }

    fun getCity(lat : Double, long : Double) : String {
        val gcd = Geocoder(context, Locale.getDefault())
        val cities = gcd.getFromLocation(lat,
                                         long, 1)
        return cities[0].locality
    }
}
