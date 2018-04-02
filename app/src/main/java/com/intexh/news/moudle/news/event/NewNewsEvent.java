package com.intexh.news.moudle.news.event;

/**
 * Created by AndroidIntexh1 on 2017/12/7.
 */

public class NewNewsEvent {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    int number;

    public NewNewsEvent() {

    };


}
