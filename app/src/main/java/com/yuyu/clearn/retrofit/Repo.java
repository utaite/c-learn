package com.yuyu.clearn.retrofit;

import com.google.gson.annotations.SerializedName;

public class Repo {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    @SerializedName("a_num")
    private int a_num;

    @SerializedName("m_token")
    private String m_token;

    @SerializedName("a_chapter")
    private String a_chapter;

    @SerializedName("a_finish")
    private String a_finish;

    @SerializedName("a_uri")
    private String a_uri;

    @SerializedName("a_ttime")
    private int a_ttime;

    @SerializedName("a_ctime")
    private int a_ctime;

    public int getA_num() {
        return a_num;
    }

    public void setA_num(int a_num) {
        this.a_num = a_num;
    }

    public String getM_token() {
        return m_token;
    }

    public void setM_token(String m_token) {
        this.m_token = m_token;
    }

    public String getA_chapter() {
        return a_chapter;
    }

    public void setA_chapter(String a_chapter) {
        this.a_chapter = a_chapter;
    }

    public String getA_finish() {
        return a_finish;
    }

    public void setA_finish(String a_finish) {
        this.a_finish = a_finish;
    }

    public String getA_uri() {
        return a_uri;
    }

    public void setA_uri(String a_uri) {
        this.a_uri = a_uri;
    }

    public int getA_ttime() {
        return a_ttime;
    }

    public void setA_ttime(int a_ttime) {
        this.a_ttime = a_ttime;
    }

    public int getA_ctime() {
        return a_ctime;
    }

    public void setA_ctime(int a_ctime) {
        this.a_ctime = a_ctime;
    }

}
