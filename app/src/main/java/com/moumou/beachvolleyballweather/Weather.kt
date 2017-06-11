package com.moumou.beachvolleyballweather

/**
 * Created by MouMou on 08-06-17.
 */
data class Weather(val summary : String, val temperature : Double, val precipProb : Double, val windSpeed : Double) {

    val weatherResult : Double
    val possible : Boolean

    init {
        weatherResult = calculateWeather(temperature, precipProb, windSpeed)
        possible = weatherResult > 1.0
    }

    fun calculateWeather(temperature : Double, precipProb : Double, windSpeed : Double) : Double {
        val temperatureFactor : Double = (-0.0215 * Math.pow(temperature, 3.0)) + 0.8754 * Math.pow(temperature, 2.0) + (-4.8251 * temperature) + 7.7724
        val windFactor : Double = 1.0
        val precipFactor : Double = 0.0003 * Math.pow(precipProb, 3.0) + (-0.052 * Math.pow(precipProb, 2.0)) + 1.0299 * precipProb + 96.6506
        val total : Double = .5 * temperatureFactor + .2 * windFactor + .3 * precipFactor
        return total
    }
}