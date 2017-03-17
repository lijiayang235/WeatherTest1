package com.braveyet.weathertest1.util;

import android.text.TextUtils;
import android.util.Log;

import com.braveyet.weathertest1.db.City;
import com.braveyet.weathertest1.db.County;
import com.braveyet.weathertest1.db.Province;
import com.braveyet.weathertest1.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/15.
 */

public class Utility {
    public static boolean handleProvince(String responseText){
        if(!TextUtils.isEmpty(responseText)){
            try {
                JSONArray jsonArray=new JSONArray(responseText);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceCode(jsonObject.getString("id"));
                    province.setProvinceName(jsonObject.getString("name"));
//                    Log.d("test",province.getProvinceName());
                    province.save();

                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean handleCity(String responseText,String provinceCode){
        if(!TextUtils.isEmpty(responseText)){
            try {
                JSONArray jsonArray=new JSONArray(responseText);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(jsonObject.getString("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceCode(provinceCode);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    } public static boolean handleCounty(String responseText,String cityCode){
        if(!TextUtils.isEmpty(responseText)){
            try {
                JSONArray jsonArray=new JSONArray(responseText);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    County county=new County();
                    county.setCityCode(cityCode);
                    county.setCountyCode(jsonObject.getString("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static Weather handleWeather(String responseText){
        try {
            JSONObject jsonObject=new JSONObject(responseText);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
