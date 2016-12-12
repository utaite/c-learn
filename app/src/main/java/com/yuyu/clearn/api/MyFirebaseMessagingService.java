package com.yuyu.clearn.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yuyu.clearn.R;
import com.yuyu.clearn.activity.LoginActivity;

// FCM 푸쉬 이벤트를 처리하기 위해 구현한 클래스
// 해당 토큰으로 푸쉬 이벤트가 왔을 때 동작
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // 푸쉬 이벤트가 왔을 때 해당 데이터를 수신하는 역할을 수행
    // 여기서는 데이터 중, title 이라는 Key 값의 Value 를 title 변수에 대입, message 라는 Key 값의 Value 를 message 변수에 대입하여 사용
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        sendNotification(title, message);
    }

    // 수신한 데이터를 이용하여 Notification 을 생성
    // 클릭 시 Login Activity로 이동
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 6888, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(6888, notificationBuilder.build());
    }

}
