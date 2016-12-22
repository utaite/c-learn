package com.yuyu.clearn.api.retrofit;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

// Retrofit 라이브러리로 Restful 통신을 위해 구현한 클래스
@Data
public class MemberVO {

    @SerializedName("v_num")
    private int v_num;

    @SerializedName("v_ctime")
    private long v_ctime;

    @SerializedName("p_token")
    private String p_token;

    @SerializedName("ct_file")
    private String ct_file;

}