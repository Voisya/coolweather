package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Hours {
    public String day;
    public String wea;
    public String tem;
    public String win;
    @SerializedName("win_speed")
    public String winSpeed;
}
