package com.yuyu.clearn.custom.speech.mainSpeech.third;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;
import com.yuyu.clearn.custom.speech.mainSpeech.MainSpeech;

public class MainThirdYesSpeech extends VideoActivity implements MainSpeech {

    @Override
    public String getMainSpeech() {
        videoDestroy(getRealm(), getUserVO());
        return SpeechFactory.DESTROY;
    }
}