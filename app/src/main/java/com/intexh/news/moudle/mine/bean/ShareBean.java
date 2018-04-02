package com.intexh.news.moudle.mine.bean;

import android.graphics.Bitmap;

/**
 * Created by frank on 16-12-26.
 */
public class ShareBean {
    private String title;
    private String text;
    private String pic;
    private String T1;
    private String url;
    private String imgPath;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }



    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }


    private boolean isHome ;
    private  Bitmap imgLogo;

    public Bitmap getImgLogo() {
        return imgLogo;
    }

    public void setImgLogo(Bitmap imgLogo) {
        this.imgLogo = imgLogo;
    }



    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ShareBean{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", pic='" + pic + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
