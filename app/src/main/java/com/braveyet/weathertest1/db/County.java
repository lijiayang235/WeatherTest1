package com.braveyet.weathertest1.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/15.
 */

public class County extends DataSupport {
    int id;
    String countyCode;
    String countyName;
    String weatherId;
    String cityCode;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public int getId() {
        return id;
    }



    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }
}
