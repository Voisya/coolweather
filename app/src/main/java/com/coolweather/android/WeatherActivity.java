package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Data;
import com.coolweather.android.gson.Index;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private Toast toast;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView airText;
    private TextView humidityText;
    private TextView indexText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        toast=Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT);
        drawerLayout=findViewById(R.id.drawer_layout);
        navButton=findViewById(R.id.nav_button);
        weatherLayout=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        airText=findViewById(R.id.air_text);
        humidityText=findViewById(R.id.humidity_text);
        indexText=findViewById(R.id.index_text);
        bingPicImg=findViewById(R.id.bing_pic_img);
        swipeRefresh=findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.cityid;
            showWeatherInfo(weather);
        }else{
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    public void requestWeather(final String weatherId){
        String weatherUrl="https://www.tianqiapi.com/api/?version=v1&appid=59472617&appsecret=yfDgnl3e&cityid="+weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId=weather.cityid;
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather){
        String cityName=weather.city;
        String updateTime=weather.updateTime.split(" ")[1];
        String degree=weather.data.get(0).tem;
        String weatherInfo=weather.data.get(0).wea;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(int i=1;i<weather.data.size();i++){
            Data data=weather.data.get(i);
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);
            dataText.setText(data.date);
            infoText.setText(data.wea);
            maxText.setText(data.tem1);
            minText.setText(data.tem2);
            forecastLayout.addView(view);
        }
        airText.setText(weather.data.get(0).airLevel);
        humidityText.setText(weather.data.get(0).humidity);
        StringBuilder sb=new StringBuilder();
        for(Index index:weather.data.get(0).index){
            //Log.d("aaa", index.title+":"+index.desc);
            if(index.title.charAt(0)=='<'){
                sb.append("减肥指数:"+index.desc+"\n\n");
            }else {
                sb.append(index.title + ":" + index.desc + "\n\n");
            }
        }
        indexText.setText(sb.toString());
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout!=null&&drawerLayout.isDrawerOpen(GravityCompat.START)){
            ChooseAreaFragment fragment=(ChooseAreaFragment)getSupportFragmentManager().findFragmentById(R.id.choose_area_fragment);
            if(fragment.onBackPress()==ChooseAreaFragment.LEVEL_PROVINCE) {
                drawerLayout.closeDrawers();
            }
        }else {
            if(null==toast.getView().getParent()){
                toast.show();
            }else {
                System.exit(0);
            }
        }
    }
}
