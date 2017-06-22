package com.moumou.beachvolleyballweather.weather

object WeatherCalculator{

    private const val lowThreshold = 0.75
    private const val highThreshold = 1.0

    private var _threshold = lowThreshold
    var Threshold : Double
        get() = _threshold
        set(value) {
            _threshold = value
        }

    private var _metric : Boolean = true
    var metric : Boolean
        get() = _metric
        set(value) {
            _metric = value
        }

    fun setThreshold(niceWeatherOnly : Boolean) {
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

