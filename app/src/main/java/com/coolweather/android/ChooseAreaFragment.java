package com.coolweather.android;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coolweather.android.db.City;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;

    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    private String selectedProvince;
    private String selectedCity;

    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=view.findViewById(R.id.title_text);
        backButton=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=dataList.get(i);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=dataList.get(i);
                    City city=DataSupport.where("provinceZh=? and cityZh=?",selectedProvince,selectedCity).findFirst(City.class);
                    String weatherId=city.getWeaId();
                    if(getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity=(WeatherActivity)getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    public int onBackPress(){
        if(currentLevel==LEVEL_CITY){
            queryProvinces();
            return LEVEL_CITY;
        }else if(currentLevel==LEVEL_PROVINCE){
            return LEVEL_PROVINCE;
        }
        return LEVEL_PROVINCE;
    }

    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        Cursor cursor = null;
        try {
            cursor = DataSupport.findBySQL("select distinct provinceZh from City");
            if (cursor != null) {
                dataList.clear();
                while (cursor.moveToNext()) {//Log.d("aaa", "queryProvinces"+cursor.getColumnIndex("provincezh"));
                    dataList.add(cursor.getString(cursor.getColumnIndex("provincezh")));
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_PROVINCE;
            } else {
                Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    private void queryCities(){
        titleText.setText(selectedProvince);
        backButton.setVisibility(View.VISIBLE);
        Cursor cursor=DataSupport.findBySQL("select distinct cityZh from City where provinceZh=?",selectedProvince);
        if(cursor!=null){
            dataList.clear();
            while(cursor.moveToNext()){
                dataList.add(cursor.getString(cursor.getColumnIndex("cityzh")));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
        }
    }


}
