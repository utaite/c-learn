package com.yuyu.clearn.custom.speech.mainSpeech.second;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;
import com.yuyu.clearn.custom.speech.mainSpeech.MainSpeech;

public class MainSecondYesSpeech extends VideoActivity implements MainSpeech {

    @Override
    public String getMainSpeech() {
        setQuiz_flag(getONE());
        quizScreenView(getQuiz_flag());
        return SpeechFactory.YES;
    }

}