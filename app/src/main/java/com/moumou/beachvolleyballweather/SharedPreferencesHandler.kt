package com.moumou.beachvolleyballweather

import android.content.Context
import android.location.Geocoder
import android.support.v7.preference.PreferenceManager
import com.google.gson.Gson
import com.moumou.beachvolleyballweather.weather.Weather
import java.util.*

object SharedPreferencesHandler {
    val KEY_WEATHER = "WEATHER"

    fun storeLocations(c : Context, w : Weather) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val editor = sharedPref.edit()

        editor.putString(KEY_WEATHER, Gson().toJson(w))
        editor.apply()
    }

    fun getLocations(c : Context) : Weather {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val s = sharedPref.getString(KEY_WEATHER, "")

        if(s == ""){
            return Weather("dummy", 0.0, 0.0, 0.0, "Silicon Valley")
        }
        val w = Gson().fromJson(s, Weather::class.java)

        return w
    }

    @Synchronized
    fun getCity(context : Context, lat : Double, long : Double) : String {
        val gcd = Geocoder(context, Locale.getDefault())
        val cities = gcd.getFromLocation(lat,
                                         long, 1)
        return cities[0].locality
    }
}