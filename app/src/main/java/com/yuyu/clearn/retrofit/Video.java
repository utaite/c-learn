package com.yuyu.clearn.retrofit;

import com.google.gson.annotations.SerializedName;

// Retrofit 라이브러리로 Restful 통신을 위해 구현한 클래스
public class Video {

    @SerializedName("v_num")
    private int v_num;

    @SerializedName("m_token")
    private String m_token;

    @SerializedName("v_finish")
    private int v_finish;

    @SerializedName("v_uri")
    private String v_uri;

    @SerializedName("v_ctime")
    private int v_ctime;

    public int getV_num() {
        return v_num;
    }

    public String getM_token() {
        return m_token;
    }

    public int getV_finish() {
        return v_finish;
    }

    public String getV_uri() {
        return v_uri;
    }

    public int getV_ctime() {
        return v_ctime;
    }

}
