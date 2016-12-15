package com.yuyu.clearn.api;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// FCM 푸쉬 이벤트를 처리하기 위해 구현한 클래스
// 어플 첫 실행 시(토큰을 발급 받을 때) 동작
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // 어플 첫 실행 시 토큰을 발급
        // 토큰은 어플이 설치된 해당 디바이스를 식별하는 역할을 수행
        // 그렇게 발급받은 토큰을 SharedPreference 에 저장한 후 추후 로그인 할 때 사용
        Log.e("TTTTTOOOOKKKKEN", FirebaseInstanceId.getInstance().getToken());
        getSharedPreferences("token", MODE_PRIVATE).edit().putString("token", FirebaseInstanceId.getInstance().getToken()).apply();
    }

}