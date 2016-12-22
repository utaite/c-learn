package com.yuyu.clearn.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

// Realm DB를 사용하기 위해 구현한 클래스 // 접속 기록 저장
@Data
public class ConnectVO extends RealmObject {

    @PrimaryKey
    private int number;
    private int v_num;
    private String start_time;
    private String end_time;
    private String p_token;

}
