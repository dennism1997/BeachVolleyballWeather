package com.moumou.beachvolleyballweather

data class Weather(val summary : String, val temperature : Double, val precipProb : Double, val windSpeed : Double) {

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
        possible = weatherResult > .7
    }

    fun calculateWeather() : Double {
        val total : Double = .5 * temperatureFactor + .2 * windFactor + .3 * precipFactor
        return total
    }

    fun calculateTemperatureFactor() : Double {
        return ((-0.0215 * Math.pow(temperature, 3.0)) + 0.8754 * Math.pow(temperature, 2.0) + (-4.8251 * temperature) + 7.7724) / 100
    }

    fun calculateWindFactor() : Double {
        return 1.0
    }

    fun calculatePrecipFactor() : Double {
        return (0.0003 * Math.pow(precipProb, 3.0) + (-0.052 * Math.pow(precipProb, 2.0)) + 1.0299 * precipProb + 96.6506) / 100
    }
}