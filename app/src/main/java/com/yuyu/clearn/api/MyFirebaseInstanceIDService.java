package com.yuyu.clearn.api;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// FCM 푸쉬 이벤트를 처리하기 위해 구현한 클래스
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        getSharedPreferences("token", MODE_PRIVATE).edit().putString("token", FirebaseInstanceId.getInstance().getToken()).apply();
    }

}