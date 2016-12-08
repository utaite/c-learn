package com.yuyu.clearn.retrofit;

import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("a_num")
    private int a_num;

    @SerializedName("m_token")
    private String m_token;

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

}