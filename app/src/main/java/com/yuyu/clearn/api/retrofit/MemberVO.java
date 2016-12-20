package com.yuyu.clearn.api.retrofit;

import com.google.gson.annotations.SerializedName;

// Retrofit 라이브러리로 Restful 통신을 위해 구현한 클래스
public class MemberVO {

    @SerializedName("v_num")
    private int v_num;

    @SerializedName("v_ctime")
    private long v_ctime;

    @SerializedName("p_token")
    private String p_token;

    @SerializedName("ct_file")
    private String ct_file;

    public int getV_num() {
        return v_num;
    }

    public long getV_ctime() {
        return v_ctime;
    }

    public String getP_token() {
        return p_token;
    }

    public String getCt_file() {
        return ct_file;
    }

}