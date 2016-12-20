package com.yuyu.clearn.custom.speech.mainSpeech.first;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;
import com.yuyu.clearn.custom.speech.mainSpeech.MainSpeech;

public class MainFirstYesSpeech extends VideoActivity implements MainSpeech {

    @Override
    public String getMainSpeech() {
        videoScreenViewFirst();
        return SpeechFactory.YES;
    }
}