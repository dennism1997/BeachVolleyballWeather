package com.moumou.beachvolleyballweather.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.moumou.beachvolleyballweather.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0 : Bundle?, p1 : String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

}