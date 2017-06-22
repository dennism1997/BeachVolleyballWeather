package com.moumou.beachvolleyballweather

import android.content.Context
import android.location.Location
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.moumou.beachvolleyballweather.fragments.CurrentLocationFragment
import com.moumou.beachvolleyballweather.fragments.EmptyFragment
import com.moumou.beachvolleyballweather.fragments.WeatherFragment

class WeatherPagerAdapter(fm : FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private var _locations : ArrayList<Location> = ArrayList()
    var locations : ArrayList<Location>
        get() = _locations
        set(value) {
            _locations = value
        }

    override fun getItem(item : Int) : Fragment {
        if (item == 0) {
            return CurrentLocationFragment()
        } else if (item == count - 1) {
            //TODO return a fragments here that lets you add a new location
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

    fun addLocation(c : Context, l : Location) {
        if (count == 0) {
            locations.add(l)
        } else {
            locations.add(locations.size, l)
            notifyDataSetChanged()
        }

        SharedPreferencesHandler.storeLocations(c, locations)
    }
}