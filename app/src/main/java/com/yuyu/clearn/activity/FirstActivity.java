package com.yuyu.clearn.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.yuyu.clearn.R;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AhoyOnboarderActivity {

    private Toast mToast;
    private long currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);
        // 이미 튜토리얼을 거친 유저인지 start로 확인 후 분기에 맞게 실행
        if (!getSharedPreferences("first", MODE_PRIVATE).getBoolean("start", false)) {
            // 튜토리얼을 거치지 않았다면 초기 화면(사용법)을 보여줌
            // 튜토리얼로 보여줄 View 객체 생성 및 값 설정
            AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(getString(R.string.start_1_1), getString(R.string.start_1_2), R.drawable.unity_1);
            AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(getString(R.string.start_2_1), getString(R.string.start_2_2), R.drawable.unity_2);
            AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(getString(R.string.start_3_1), getString(R.string.start_3_2), R.drawable.unity_3);
            ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
            ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
            ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
            List<AhoyOnboarderCard> pages = new ArrayList<>();
            pages.add(ahoyOnboarderCard1);
            pages.add(ahoyOnboarderCard2);
            pages.add(ahoyOnboarderCard3);
            for (AhoyOnboarderCard page : pages) {
                page.setTitleColor(R.color.white);
                page.setDescriptionColor(R.color.grey_200);
                page.setTitleTextSize(dpToPixels(10, this));
                page.setDescriptionTextSize(dpToPixels(7, this));
            }
            setFinishButtonTitle(getString(R.string.finish_title));
            showNavigationControls(true);
            setGradientBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            }
            setFont(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
            setOnboardPages(pages);
        } else {
            // 튜토리얼을 이미 거쳤다면 바로 로그인 액티비티로 이동
            intentMethod();
        }
    }

    @Override
    public void onFinishButtonPressed() {
        // Finish 버튼을 눌렀다면 튜토리얼을 완료한 상태로 저장
        getSharedPreferences("first", MODE_PRIVATE).edit().putBoolean("start", true).apply();
        intentMethod();
    }

    @Override
    public void onBackPressed() {
        if (currentTime + 2000 < System.currentTimeMillis()) {
            currentTime = System.currentTimeMillis();
            mToast.setText(getString(R.string.onBackPressed));
            mToast.show();
        } else {
            super.onBackPressed();
        }
    }

    public void intentMethod() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
