package com.moumou.beachvolleyballweather

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val RC_LOCATION_PERMISSION = 9001

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this,
                                              android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                                              arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                              RC_LOCATION_PERMISSION)
        } else {
            val weatherPagerAdapter = WeatherPagerAdapter(supportFragmentManager)
            viewPager.adapter = weatherPagerAdapter
        }

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
            R.id.request_feature_action -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = (Uri.parse(getString(R.string.mailto) + getString(R.string.email) + getString(
                        R.string.mail_body)))

                startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode : Int,
                                            permissions : Array<out String>,
                                            grantResults : IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate()
                } else {
                    ActivityCompat.requestPermissions(this,
                                                      arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                                      RC_LOCATION_PERMISSION)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}
