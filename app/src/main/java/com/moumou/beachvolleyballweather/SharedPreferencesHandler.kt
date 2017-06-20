package com.moumou.beachvolleyballweather

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.google.gson.Gson
import com.moumou.beachvolleyballweather.Weather.Weather

/**
 * Created by MouMou on 20-06-17.
 */

object SharedPreferencesHandler {
    val KEY_WEATHER = "WEATHER"

    fun storeWeather(c : Context, w : Weather) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val editor = sharedPref.edit()

        editor.putString(KEY_WEATHER, Gson().toJson(w))
        editor.apply()
    }

    fun getWeather(c : Context) : Weather {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(c)
        val s = sharedPref.getString(KEY_WEATHER, "")

        if(s == ""){
            return Weather("dummy", 0.0, 0.0, 0.0, "Silicon Valley")
        }
        val w = Gson().fromJson(s, Weather::class.java)

        return w
    }
}