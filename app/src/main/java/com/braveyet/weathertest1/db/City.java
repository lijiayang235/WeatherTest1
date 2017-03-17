package com.braveyet.weathertest1.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/15.
 */

public class City extends DataSupport {
    int id;
    String cityCode;
    String cityName;
    String provinceCode;

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }
}
