package com.yuyu.clearn.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Realm DB를 사용하기 위해 구현한 클래스
public class UserVO extends RealmObject {

    @PrimaryKey
    private int number;
    private int v_num;
    private String start_time;
    private String end_time;
    private String p_token;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getV_num() {
        return v_num;
    }

    public void setV_num(int v_num) {
        this.v_num = v_num;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getP_token() {
        return p_token;
    }

    public void setP_token(String p_token) {
        this.p_token = p_token;
    }
}
