package com.intexh.news.moudle.main.event;

/**
 * Created by AndroidIntexh1 on 2017/12/21.
 */

public class LocationEvent {
    public boolean isLocationSuccess() {
        return locationSuccess;
    }

    public void setLocationSuccess(boolean locationSuccess) {
        this.locationSuccess = locationSuccess;
    }

    boolean locationSuccess;
}
