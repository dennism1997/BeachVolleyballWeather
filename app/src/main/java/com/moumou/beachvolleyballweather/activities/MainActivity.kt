package com.moumou.beachvolleyballweather.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.places.ui.PlacePicker
import com.moumou.beachvolleyballweather.R
import com.moumou.beachvolleyballweather.SharedPreferencesHandler
import com.moumou.beachvolleyballweather.WeatherPagerAdapter
import com.moumou.beachvolleyballweather.weather.WeatherCalculator
import com.moumou.beachvolleyballweather.weather.WeatherLocation
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val RC_LOCATION_PERMISSION = 9001
    private var weatherPagerAdapter : WeatherPagerAdapter = WeatherPagerAdapter(null)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this,
                                              android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                                              arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                              RC_LOCATION_PERMISSION)
        } else {
            weatherPagerAdapter = WeatherPagerAdapter(supportFragmentManager)
            weatherPagerAdapter.locations = SharedPreferencesHandler.getLocations(this)
            viewPager.adapter = weatherPagerAdapter
            viewPager.offscreenPageLimit = 3
        }

        getSettings()
    }

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        when (item?.itemId) {
            R.id.settings_action -> {
                val i = Intent(this, SettingsActivity().javaClass)
                startActivity(i)
                return true
            }
            R.id.about_action -> {
                showAboutScreen()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutScreen() {
        startActivity(Intent(this, AboutActivity().javaClass))
    }

    override fun onRequestPermissionsResult(requestCode : Int,
                                            permissions : Array<out String>,
                                            grantResults : IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate()
                } else {
                    ActivityCompat.requestPermissions(this,
                                                      arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                                      RC_LOCATION_PERMISSION)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val place = PlacePicker.getPlace(this, data).latLng
            val city = getCity(place.latitude, place.longitude)
            createWeatherFragment(place.latitude, place.longitude, city)
        } else {
            super.onActivityResult(requestCode, resultCode, data)

        }

    }

    fun createWeatherFragment(lat : Double, long : Double, city : String) {
        val l = WeatherLocation(lat, long, city)
        weatherPagerAdapter.addLocation(this, l)
    }

    fun getSettings() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        WeatherCalculator.metric = sharedPref.getBoolean(getString(R.string.settings_metric_key),
                                                         true)
        val niceWeatherOnly = sharedPref.getBoolean(getString(R.string.settings_nice_weather_key),
                                                    false)
        WeatherCalculator.setThreshold(niceWeatherOnly)
    }

    fun getCity(lat : Double, long : Double) : String {
        val gcd = Geocoder(this, Locale.getDefault())
        val cities = gcd.getFromLocation(lat,
                                         long, 1)
        return cities[0].locality
    }

}
