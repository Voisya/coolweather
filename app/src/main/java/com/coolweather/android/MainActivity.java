package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

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
}
