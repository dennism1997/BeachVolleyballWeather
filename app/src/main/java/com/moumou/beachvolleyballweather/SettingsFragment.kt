package com.moumou.beachvolleyballweather

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat


/**
 * Created by MouMou on 21-05-17.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

}