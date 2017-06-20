package com.moumou.beachvolleyballweather

import android.location.Location
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by MouMou on 20-06-17.
 */
class WeatherPagerAdapter(fm : FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val locations : ArrayList<Location> = ArrayList()
    override fun getItem(item : Int) : Fragment {
        if (item == 0) {
            return CurrentLocationFragment()
        } else if (item == count-1) {
            //TODO return a fragment here that lets you add a new location
            return CurrentLocationFragment()
        }

        return WeatherFragment(locations[item])
    }

    override fun getCount() : Int {
        return locations.size + 2
    }
}