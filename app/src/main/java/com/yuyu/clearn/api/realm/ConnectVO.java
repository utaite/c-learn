package com.yuyu.clearn.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Realm DB를 사용하기 위해 구현한 클래스 // 접속 기록 저장
public class ConnectVO extends RealmObject {

    @PrimaryKey
    private int number;
    private int v_num;
    private String start_time;
    private String end_time;
    private String p_token;

    public void setV_num(int v_num) {
        this.v_num = v_num;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setP_token(String p_token) {
        this.p_token = p_token;
    }
}
