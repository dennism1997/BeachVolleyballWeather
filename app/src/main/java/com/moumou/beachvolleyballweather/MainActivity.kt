package com.moumou.beachvolleyballweather

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.main_content_frame, MainFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        when (item?.itemId) {
            R.id.settings_action -> {
                val i = Intent(this, SettingsActivity().javaClass)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
