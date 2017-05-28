package com.moumou.beachvolleyballweather

import android.os.Bundle
import android.preference.PreferenceFragment


/**
 * Created by MouMou on 21-05-17.
 */
class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

}