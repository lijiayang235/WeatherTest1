package com.braveyet.weathertest1.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */

public class Weather {
    public Basic basic;
    public AQI aqi;
    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;
    public Suggestion suggestion;
    public Now now;
    public String status;
}
