package com.moumou.beachvolleyballweather.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moumou.beachvolleyballweather.R
import com.moumou.beachvolleyballweather.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.settings_content_frame,
                                                          SettingsFragment()).commit()
    }
}
