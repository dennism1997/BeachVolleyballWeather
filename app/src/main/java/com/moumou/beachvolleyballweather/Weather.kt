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
        possible = weatherResult > WeatherCalculator.Threshold
    }

    fun calculateWeather() : Double {
        val total : Double = .5 * temperatureFactor + .3 * windFactor + .2 * precipFactor
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

object WeatherCalculator{

    private const val lowThreshold = 0.75
    private const val highThreshold = 1.0

    private var _threshold = lowThreshold
    public var Threshold : Double
        get() = _threshold
        set(value) {
            _threshold = value
        }

    fun setThreshhold(niceWeatherOnly : Boolean) {
        if(niceWeatherOnly){
            _threshold = highThreshold
        } else {
            _threshold = lowThreshold
        }
    }

    fun toFahrenheit(temperature : Double) : Double {
        return temperature * 9.0/5.0 + 32.0
    }

    fun toMilesPerHour(speed : Double) : Double {
        return speed * 2.24
    }
}

