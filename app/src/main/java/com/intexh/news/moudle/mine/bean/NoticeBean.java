package com.intexh.news.moudle.mine.bean;

/**
 * Created by AndroidIntexh1 on 2017/11/24.
 */

public class NoticeBean {

    /**
     * message_id : 1
     * message_content : 这是系统消息1
     * message_time : 0
     */

    private String message_id;
    private String message_content;
    private String message_time;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }
}
