package com.yuyu.clearn.retrofit;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("v_num")
    private int v_num;

    @SerializedName("m_token")
    private String m_token;

    @SerializedName("v_finish")
    private String v_finish;

    @SerializedName("v_uri")
    private String v_uri;

    @SerializedName("v_ctime")
    private int v_ctime;

    public int getV_num() {
        return v_num;
    }

    public void setV_num(int v_num) {
        this.v_num = v_num;
    }

    public String getM_token() {
        return m_token;
    }

    public void setM_token(String m_token) {
        this.m_token = m_token;
    }

    public String getV_finish() {
        return v_finish;
    }

    public void setV_finish(String v_finish) {
        this.v_finish = v_finish;
    }

    public String getV_uri() {
        return v_uri;
    }

    public void setV_uri(String v_uri) {
        this.v_uri = v_uri;
    }

    public int getV_ctime() {
        return v_ctime;
    }

    public void setV_ctime(int v_ctime) {
        this.v_ctime = v_ctime;
    }

}
