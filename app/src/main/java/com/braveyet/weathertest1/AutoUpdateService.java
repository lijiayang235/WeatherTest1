package com.braveyet.weathertest1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.braveyet.weathertest1.gson.Weather;
import com.braveyet.weathertest1.util.HttpUtil;
import com.braveyet.weathertest1.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBing_pic();

        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        int hour=8*60*60*1000;
        long triggertime= SystemClock.elapsedRealtime()+hour;
        manager.cancel(pi);


        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggertime,pi);





        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String response=prefs.getString("weather",null);
        if(response!=null){
            Weather weather= Utility.handleWeather(response);
            String weatherId=weather.basic.weatherId;

            String weatherUrl= "http://guolin.tech/api/weather?cityid="+weatherId+"&key=19dbb264703e45ff96e4373c0a67268b";
            HttpUtil.sendokhttp(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weather",responseText);
                    editor.apply();

                }
            });
        }



    }

    private void updateBing_pic() {
        String bingPic="";
        HttpUtil.sendokhttp(bingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseTest=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",responseTest);
                editor.apply();
            }
        });
    }
}
