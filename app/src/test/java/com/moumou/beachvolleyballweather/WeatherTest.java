package com.moumou.beachvolleyballweather;

import android.support.annotation.NonNull;

import com.moumou.beachvolleyballweather.Weather.Weather;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class WeatherTest {

    @NonNull
    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{{0.0, 0.0, 0.0, false},
                {15, 0, 0, true},
                {15, 100, 0, false},
                {15, 50, 5, true},
                {6, 0, 0, false},
                {15, 0, 0, true}});
    }

    private final double temp;
    private final double precip;
    private final double wind;
    private final boolean expected;

    public WeatherTest(double temp, double precip, double wind, boolean expected) {
        this.temp = temp;
        this.precip = precip;
        this.wind = wind;
        this.expected = expected;
    }

    @Test
    public void test() {
        Weather w = new Weather("", temp, precip, wind);
        Assert.assertEquals(expected, w.getPossible());
    }
}

