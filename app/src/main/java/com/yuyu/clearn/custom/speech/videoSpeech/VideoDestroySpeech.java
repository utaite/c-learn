package com.yuyu.clearn.custom.speech.videoSpeech;

import android.util.Log;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.api.retrofit.RestInterface;
import com.yuyu.clearn.custom.Constant;
import com.yuyu.clearn.custom.speech.SpeechFactory;

import rx.Subscriber;

public class VideoDestroySpeech extends VideoActivity implements VideoSpeech {

    @Override
    public String getVideoSpeech() {
        getSharedPreferences("VIDEO", MODE_PRIVATE).edit().putInt("NUMBER", getSharedPreferences("VIDEO", MODE_PRIVATE).getInt("NUMBER", 0) + 1).apply();
        RestInterface.getRestClient()
                .create(RestInterface.PostSave.class)
                .save("SAVE".toLowerCase(), getIntent().getIntExtra(Constant.V_NUM, -1), getV_ctime())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getTAG(), String.valueOf(e));
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        setQuiz_flag(getONE());
                        quizScreenView(getQuiz_flag());
                    }
                });
        return SpeechFactory.DESTROY;
    }
}