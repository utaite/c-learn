package com.yuyu.clearn.retrofit;

import com.google.gson.annotations.SerializedName;

public class Child {

    @SerializedName("c_num")
    private String c_num;

    @SerializedName("c_name")
    private String c_name;

    @SerializedName("c_age")
    private String c_age;

    @SerializedName("c_gender")
    private String c_gender;

    @SerializedName("c_pic")
    private String c_pic;

    @SerializedName("c_birth")
    private String c_birth;

    @SerializedName("p_num")
    private String p_num;

    public String getC_num() {
        return c_num;
    }

    public void setC_num(String c_num) {
        this.c_num = c_num;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_age() {
        return c_age;
    }

    public void setC_age(String c_age) {
        this.c_age = c_age;
    }

    public String getC_gender() {
        return c_gender;
    }

    public void setC_gender(String c_gender) {
        this.c_gender = c_gender;
    }

    public String getC_pic() {
        return c_pic;
    }

    public void setC_pic(String c_pic) {
        this.c_pic = c_pic;
    }

    public String getC_birth() {
        return c_birth;
    }

    public void setC_birth(String c_birth) {
        this.c_birth = c_birth;
    }

    public String getP_num() {
        return p_num;
    }

    public void setP_num(String p_num) {
        this.p_num = p_num;
    }

}
