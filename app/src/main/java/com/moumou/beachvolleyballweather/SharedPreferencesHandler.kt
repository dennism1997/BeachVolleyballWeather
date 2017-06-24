package com.moumou.beachvolleyballweather

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.moumou.beachvolleyballweather.weather.WeatherLocation

object SharedPreferencesHandler {
    val KEY_WEATHER_LOCATIONS = "WEATHERLOCATIONS"

    fun storeLocations(c : Context, l : ArrayList<WeatherLocation>) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val editor = sharedPref.edit()

        editor.putString(KEY_WEATHER_LOCATIONS, Gson().toJson(l))
        editor.apply()
    }

    fun getLocations(c : Context) : ArrayList<WeatherLocation> {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val s = sharedPref.getString(KEY_WEATHER_LOCATIONS, "")

        val type = object : TypeToken<ArrayList<WeatherLocation>>() {}.type
        if (s != "") {
            try {
                return Gson().fromJson<ArrayList<WeatherLocation>>(s, type)
            } catch (e : JsonSyntaxException) {
                AlertDialog.Builder(c).setMessage(c.getString(R.string.storage_parsing_error)).setPositiveButton(
                        R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }.show()
            }
        }
        return ArrayList()
    }

}