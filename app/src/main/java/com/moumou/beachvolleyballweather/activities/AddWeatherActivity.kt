package com.moumou.beachvolleyballweather.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.moumou.beachvolleyballweather.Constants

class AddWeatherActivity : AppCompatActivity() {

    val PLACE_PICKER_RC = 1

    override fun onStart() {
        super.onStart()
        val i = PlacePicker.IntentBuilder().build(this)
        startActivityForResult(i, PLACE_PICKER_RC)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        if (requestCode == PLACE_PICKER_RC) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                val toastMsg = String.format("Place: %s", place.name)
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.LAT_RC, place.latLng.latitude)
                resultIntent.putExtra(Constants.LONG_RC, place.latLng.longitude)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}