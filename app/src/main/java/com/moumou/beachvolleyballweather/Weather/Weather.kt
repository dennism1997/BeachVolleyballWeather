package com.moumou.beachvolleyballweather.Weather

import java.lang.Math.pow

data class Weather(val summary : String,
                   val temperature : Double,
                   val precipProb : Double,
                   val windSpeed : Double,
                   val city : String) {

    val weatherResult : Double
    val possible : Boolean
    val temperatureFactor : Double
    val windFactor : Double
    val precipFactor : Double

    init {
        temperatureFactor = calculateTemperatureFactor()
        windFactor = calculateWindFactor()
        precipFactor = calculatePrecipFactor()
        weatherResult = calculateWeather()
        possible = calculatePossible()
    }

    fun calculateWeather() : Double {
        return .5 * temperatureFactor + .3 * windFactor + .2 * precipFactor
    }

    fun calculateTemperatureFactor() : Double {
        return ((-0.0215 * pow(temperature, 3.0)) + 0.8754 * pow(temperature,
                                                                 2.0) + (-4.8251 * temperature) + 7.7724) / 100
    }

    fun calculateWindFactor() : Double {
        return (0.0162 * pow(windSpeed, 3.0)) + (-0.0445 * pow(windSpeed,
                                                               2.0)) + (-9.9975 * windSpeed) + 100.0416
    }

    fun calculatePrecipFactor() : Double {
        return (0.0003 * pow(precipProb, 3.0) + (-0.052 * pow(precipProb,
                                                              2.0)) + 1.0299 * precipProb + 96.6506) / 100
    }

    fun calculatePossible() : Boolean {
        if (temperature < 5.0 || windSpeed > 17.0 || precipProb > 90.0) {
            return false
        }

        return weatherResult > WeatherCalculator.Threshold
    }

}
