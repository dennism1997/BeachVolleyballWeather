package com.moumou.beachvolleyballweather

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.support.v7.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale
import kotlin.collections.ArrayList

object SharedPreferencesHandler {
    val KEY_WEATHER_LOCATIONS = "LOCATIONS"

    fun storeLocations(c : Context, l : ArrayList<Location>) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val editor = sharedPref.edit()

        editor.putString(KEY_WEATHER_LOCATIONS, Gson().toJson(l))
        editor.apply()
    }

    fun getLocations(c : Context) : ArrayList<Location> {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val s = sharedPref.getString(KEY_WEATHER_LOCATIONS, "")

        if(s == ""){
            return ArrayList()
        }
        val type = object : TypeToken<ArrayList<Location>>() {}.type
        val list = Gson().fromJson<ArrayList<Location>>(s, type)

        return list
    }

    @Synchronized
    fun getCity(context : Context, lat : Double, long : Double) : String {
        val gcd = Geocoder(context, Locale.getDefault())
        val cities = gcd.getFromLocation(lat,
                                         long, 1)
        return cities[0].locality
    }
}