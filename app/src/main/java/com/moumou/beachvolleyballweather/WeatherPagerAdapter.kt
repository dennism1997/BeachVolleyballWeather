package com.moumou.beachvolleyballweather

import android.location.Location
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.moumou.beachvolleyballweather.fragments.CurrentLocationFragment
import com.moumou.beachvolleyballweather.fragments.EmptyFragment
import com.moumou.beachvolleyballweather.fragments.WeatherFragment

/**
 * Created by MouMou on 20-06-17.
 */
class WeatherPagerAdapter(fm : FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private var locations : ArrayList<Location> = ArrayList()

    override fun getItem(item : Int) : Fragment {
        if (item == 0) {
            return CurrentLocationFragment()
        } else if (item == count - 1) {
            //TODO return a fragments here that lets you add a new location
            return EmptyFragment()
        }

        return WeatherFragment(locations[item - 1])
    }

    override fun getItemPosition(`object` : Any?) : Int {
        return super.getItemPosition(`object`)
    }

    override fun getCount() : Int {
        return locations.size + 2
    }

    fun addLocation(l : Location) {
        if (count == 0) {
            locations.add(l)
        } else {
            locations.add(locations.size, l)
            notifyDataSetChanged()
        }
    }
}