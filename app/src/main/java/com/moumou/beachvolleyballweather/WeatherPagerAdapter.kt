package com.moumou.beachvolleyballweather

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.moumou.beachvolleyballweather.fragments.CurrentLocationFragment
import com.moumou.beachvolleyballweather.fragments.EmptyFragment
import com.moumou.beachvolleyballweather.fragments.WeatherFragment
import com.moumou.beachvolleyballweather.weather.WeatherLocation

class WeatherPagerAdapter(fm : FragmentManager?) : FragmentStatePagerAdapter(fm) {
    private var _locations : ArrayList<WeatherLocation> = ArrayList()

    var locations : ArrayList<WeatherLocation>
        get() = _locations
        set(value) {
            _locations = value
        }

    override fun getItem(item : Int) : Fragment {
        if (item == 0) {
            return CurrentLocationFragment()
        } else if (item == count - 1) {
            return EmptyFragment()
        }

        return WeatherFragment(locations[item - 1])
    }

    override fun getCount() : Int {
        return locations.size + 2
    }

    override fun getItemPosition(`object` : Any?) : Int {
        return PagerAdapter.POSITION_NONE
    }

    fun addLocation(c : Context, l : WeatherLocation) {
        if (count == 0) {
            locations.add(l)
        } else {
            locations.add(locations.size, l)
            notifyDataSetChanged()
        }

        SharedPreferencesHandler.storeLocations(c, locations)
    }

    fun removeLocation(i : Int) {
        locations.removeAt(i)
    }

}
