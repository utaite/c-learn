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

    public void setV_num(int v_num) {
        this.v_num = v_num;
    }

    public long getV_ctime() {
        return v_ctime;
    }

    public void setV_ctime(long v_ctime) {
        this.v_ctime = v_ctime;
    }

    public String getP_token() {
        return p_token;
    }

    public void setP_token(String p_token) {
        this.p_token = p_token;
    }

    public String getCt_file() {
        return ct_file;
    }

    public void setCt_file(String ct_file) {
        this.ct_file = ct_file;
    }
}