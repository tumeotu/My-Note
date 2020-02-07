package com.example.myapplication;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class ComponentNote  implements Serializable {

    private String name;
    private String timewrite;
    private  String tag;
    private  String data;
    private Integer iD;

    public Integer getiD() {
        return iD;
    }

    public void setiD(Integer iD) {
        this.iD = iD;
    }

    public ComponentNote(String name, String timewrite, String tag, Integer iD, String data) {
        this.name = name;
        this.timewrite = timewrite;
        this.tag = tag;
        this.iD=iD;
        this.data=data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimewrite() {
        return timewrite;
    }

    public void setTimewrite(String timewrite) {
        this.timewrite = timewrite;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
