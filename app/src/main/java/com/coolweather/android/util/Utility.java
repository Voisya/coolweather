package com.coolweather.android.util;

import android.content.Context;
import android.content.res.AssetManager;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utility {

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            String weatherContent=jsonObject.toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
