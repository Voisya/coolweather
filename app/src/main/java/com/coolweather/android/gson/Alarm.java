package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Alarm {
    @SerializedName("alarm_type")
    public String alarmType;
    @SerializedName("alarm_level")
    public String alarmLevel;
    @SerializedName("alarm_content")
    public String alarmContent;
}
