package com.braveyet.weathertest1.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/16.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
