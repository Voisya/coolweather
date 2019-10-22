package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String cityid;
    @SerializedName("update_time")
    public String updateTime;
    public String city;
    public String cityEn;
    public String country;
    public String countryEn;
    public List<Data> data;
}
