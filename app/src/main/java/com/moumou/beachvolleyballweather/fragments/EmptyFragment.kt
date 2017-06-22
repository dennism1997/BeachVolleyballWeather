package com.moumou.beachvolleyballweather.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.ui.PlacePicker
import com.moumou.beachvolleyballweather.Constants
import com.moumou.beachvolleyballweather.R

class EmptyFragment : Fragment() {

    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View? {

        val view = inflater!!.inflate(R.layout.fragment_empty, container, false)

        view.setOnClickListener {
            //            val i = Intent(context, AddWeatherActivity().javaClass)
//            startActivityForResult(i, Constants.NEW_WEATHER_RC)
            val i = PlacePicker.IntentBuilder().build(activity)
            startActivityForResult(i, Constants.NEW_WEATHER_RC)
        }
        return view
    }

}