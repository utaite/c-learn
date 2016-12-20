package com.yuyu.clearn.custom.speech.mainSpeech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class MainDestroySpeech extends VideoActivity implements MainSpeech {

    @Override
    public String getMainSpeech() {
        videoDestroy(getRealm(), getUserVO());
        return SpeechFactory.DESTROY;
    }
}