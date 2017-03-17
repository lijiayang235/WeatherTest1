package com.braveyet.weathertest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.braveyet.weathertest1.db.City;
import com.braveyet.weathertest1.db.County;
import com.braveyet.weathertest1.db.Province;
import com.braveyet.weathertest1.util.HttpUtil;
import com.braveyet.weathertest1.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/15.
 */

public class Choosearea extends Fragment {
    private static int LEVEL_PROVINCE=0;
    private static int LEVEL_CITY=1;
    private static int LEVEL_COUNTY=2;
    private TextView titleText;
    private Button backbutton;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private List<String>dataList=new ArrayList<>();
    private int currentLevel;
    private List<Province>provinceList;
    private List<City>cityList;
    private List<County>countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chooseareafragment,container);
        titleText= (TextView) view.findViewById(R.id.title_text);
        backbutton= (Button) view.findViewById(R.id.back_button);
        listView= (ListView) view.findViewById(R.id.listview);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){

                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    selectedCounty=countyList.get(position);
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    Log.d("mytest",selectedCounty.getWeatherId()+"intent");
                    intent.putExtra("weather_id",selectedCounty.getWeatherId());
                    startActivity(intent);
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("中国");
        backbutton.setVisibility(View.GONE);


        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            Log.d("test",dataList.toString());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromeServer(address,"province");
        }

    }



    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backbutton.setVisibility(View.VISIBLE);
        countyList= DataSupport.where("cityCode=?",String.valueOf(selectedCity.getCityCode())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            Log.d("mytest",dataList.toString());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            String cityCode=selectedCity.getCityCode();
            String provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromeServer(address,"county");
        }
    }
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backbutton.setVisibility(View.VISIBLE);
        cityList= DataSupport.where("provinceCode=?",String.valueOf(selectedProvince.getProvinceCode())).find(City.class);

        if(cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            String provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromeServer(address,"city");
        }
    }
    private void queryFromeServer(String address, final String type) {
        showDialog();
        HttpUtil.sendokhttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancleDialog();
                        Toast.makeText(getContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resonseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){

                    result= Utility.handleProvince(resonseText);
                }else if("city".equals(type)){
                    result=Utility.handleCity(resonseText,selectedProvince.getProvinceCode());
                }else if("county".equals(type)){
                    result=Utility.handleCounty(resonseText,selectedCity.getCityCode());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancleDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                               queryCounties();
                            }
                        }
                    });
                }
            }
        });


    }

    private void cancleDialog() {
        if(progressDialog!=null){
        progressDialog.dismiss();
        }
    }

    private void showDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("加载中");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();

    }

}
