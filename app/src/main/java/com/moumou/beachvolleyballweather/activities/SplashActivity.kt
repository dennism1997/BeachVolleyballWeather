package com.moumou.beachvolleyballweather.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        val i = Intent(this, MainActivity().javaClass)
        startActivity(i)
        finish()
    }
}
