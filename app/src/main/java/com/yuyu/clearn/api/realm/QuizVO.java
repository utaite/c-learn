package com.yuyu.clearn.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Realm DB를 사용하기 위해 구현한 클래스 // 퀴즈 점수 저장
public class QuizVO extends RealmObject {

    @PrimaryKey
    private int number;
    private int v_num;
    private String userAnswerList;
    private String quizAnswerList;
    private String resultList;
    private String p_token;

    public void setV_num(int v_num) {
        this.v_num = v_num;
    }

    public void setUserAnswerList(String userAnswerList) {
        this.userAnswerList = userAnswerList;
    }

    public void setQuizAnswerList(String quizAnswerList) {
        this.quizAnswerList = quizAnswerList;
    }

    public void setResultList(String resultList) {
        this.resultList = resultList;
    }

    public void setP_token(String p_token) {
        this.p_token = p_token;
    }
}
