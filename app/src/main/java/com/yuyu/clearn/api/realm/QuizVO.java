package com.yuyu.clearn.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

// Realm DB를 사용하기 위해 구현한 클래스 // 퀴즈 점수 저장
@Data
public class QuizVO extends RealmObject {

    @PrimaryKey
    private int number;
    private int v_num;
    private String userAnswerList;
    private String quizAnswerList;
    private String resultList;
    private String p_token;

}
