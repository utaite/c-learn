package com.yuyu.clearn.custom.speech.mainSpeech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class MainQuizSpeech extends VideoActivity implements MainSpeech {
    @Override
    public String getMainSpeech() {
        setQuiz_flag(getONE());
        quizScreenView(getQuiz_flag());
        return SpeechFactory.QUIZ;
    }
}