package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    public String day;
    public String date;
    public String week;
    public String wea;
    @SerializedName("wea_img")
    public String weaImg;
    public String air;
    public String humidity;
    @SerializedName("air_level")
    public String airLevel;
    @SerializedName("air_tips")
    public String airTips;
    public Alarm alarm;
    public String tem1;
    public String tem2;
    public String tem;
    public List<String> win;
    public String win_speed;
    public List<Hours> hours;
    public List<Index> index;
}
