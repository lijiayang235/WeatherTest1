package com.braveyet.weathertest1;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.braveyet.weathertest1.gson.Forecast;
import com.braveyet.weathertest1.gson.Weather;
import com.braveyet.weathertest1.util.HttpUtil;
import com.braveyet.weathertest1.util.Utility;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView titleUpdate;
    private TextView nowTemp;
    private TextView nowInfo;
    private String mWeatherId;
    private ScrollView weatherLayout;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    public SwipeRefreshLayout swipeRefresh;
    private ImageView bingPic;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View view=getWindow().getDecorView();
            view.setSystemUiVisibility(view.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|view.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdate= (TextView) findViewById(R.id.title_update_time);
        nowTemp= (TextView) findViewById(R.id.now_temp);
        nowInfo= (TextView) findViewById(R.id.now_info);
        weatherLayout= (ScrollView) findViewById(R.id.weather_layout);
        forecastLayout= (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText= (TextView) findViewById(R.id.aqi);
        pm25Text= (TextView) findViewById(R.id.pm25);
        carWashText= (TextView) findViewById(R.id.carwash_text);
        sportText= (TextView) findViewById(R.id.sport_text);
        comfortText= (TextView) findViewById(R.id.comfort_text);
        swipeRefresh= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        bingPic= (ImageView) findViewById(R.id.bing_pic);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton= (Button) findViewById(R.id.nav);


        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String responseText=prefs.getString("weather",null);
        if(!TextUtils.isEmpty(responseText)){

            Weather weather= Utility.handleWeather(responseText);

            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            mWeatherId=getIntent().getStringExtra("weather_id");

            weatherLayout.setVisibility(View.GONE);
            requestWeatherInfo(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeatherInfo(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bing_pic=prefs.getString("bing_pic",null);
        if(bing_pic!=null){
            Glide.with(this).load(bing_pic).into(bingPic);
        }else{
            loadBingPic();
        }

    }

    private void loadBingPic() {
        String address="http://guolin.tech/api/bing_pic";
        HttpUtil.sendokhttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",responseText);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseText).into(bingPic);
                    }
                });

            }
        });
    }

    public  void requestWeatherInfo(final String weatherId) {
        String weatherUrl= "http://guolin.tech/api/weather?cityid="+weatherId+"&key=19dbb264703e45ff96e4373c0a67268b";
        HttpUtil.sendokhttp(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeather(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId=weatherId;
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"天气不存在，获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });


            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime;
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdate.setText(updateTime);
        nowInfo.setText(weatherInfo);
        nowTemp.setText(degree);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.forecast_time);
            TextView infoText = (TextView) view.findViewById(R.id.forecast_info);
            TextView maxText = (TextView) view.findViewById(R.id.max_info);
            TextView minText = (TextView) view.findViewById(R.id.min_info);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }

}
