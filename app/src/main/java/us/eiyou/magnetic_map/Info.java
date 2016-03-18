package us.eiyou.magnetic_map;

import cn.bmob.v3.BmobObject;

/**
 * Created by Au on 2016/3/5.
 */
public class Info extends BmobObject {
    Double latitude;
    Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCichang() {
        return cichang;
    }

    public void setCichang(String cichang) {
        this.cichang = cichang;
    }

    String cichang;
}
