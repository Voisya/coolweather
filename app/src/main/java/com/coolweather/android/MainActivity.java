package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Toast toast;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast=Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }else{
            try {
                showProgressDialog();
                String cityJson = Utility.getJson("city.json", this);
                JSONArray cityArray = new JSONArray(cityJson);
                for(int i=0;i<cityArray.length();i++){
                    JSONObject object=cityArray.getJSONObject(i);
                    City city=new City();
                    city.setWeaId(object.getString("id"));
                    city.setCityEn(object.getString("cityEn"));
                    city.setCityZh(object.getString("cityZh"));
                    city.setProvinceEn(object.getString("provinceEn"));
                    city.setProvinceZh(object.getString("provinceZh"));
                    city.setLeaderEn(object.getString("leaderEn"));
                    city.setLeaderZh(object.getString("leaderZh"));
                    city.setLat(object.getString("lat"));
                    city.setLon(object.getString("lon"));
                    city.save();
                }
                closeProgressDialog();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        ChooseAreaFragment fragment=(ChooseAreaFragment)getSupportFragmentManager().findFragmentById(R.id.choose_area_fragment);
        if(fragment.onBackPress()==ChooseAreaFragment.LEVEL_PROVINCE) {
            if (null == toast.getView().getParent()) {
                toast.show();
            } else {
                System.exit(0);
            }
        }
    }


    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
