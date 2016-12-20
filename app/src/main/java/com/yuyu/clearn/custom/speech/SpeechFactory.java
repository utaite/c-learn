package com.yuyu.clearn.custom.speech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.mainSpeech.MainDestroySpeech;
import com.yuyu.clearn.custom.speech.mainSpeech.MainQuizSpeech;
import com.yuyu.clearn.custom.speech.mainSpeech.MainStudySpeech;
import com.yuyu.clearn.custom.speech.mainSpeech.first.MainFirstYesSpeech;
import com.yuyu.clearn.custom.speech.mainSpeech.second.MainSecondYesSpeech;
import com.yuyu.clearn.custom.speech.mainSpeech.third.MainThirdYesSpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoDestroySpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoNextSpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoPlaySpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoPrevSpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoResetSpeech;
import com.yuyu.clearn.custom.speech.videoSpeech.VideoStopSpeech;

public class SpeechFactory extends VideoActivity implements SpeechInter {

    public static String NONE = "", DESTROY = "종료", PLAY = "재생", STOP = "정지", NEXT = "앞으로", PREV = "뒤로", RESET = "처음으로";
    public static String YES = "응", NO = "아니", STUDY = "수업", QUIZ = "퀴즈";

    @Override
    public String getSpeech(int status, int main_flag, String result) {
        if (status == 0) {

            if (main_flag == 1) {
                if (result.contains(YES)) {
                    return new MainFirstYesSpeech().getMainSpeech();
                }

            } else if (main_flag == 2) {
                if (result.contains(YES)) {
                    return new MainSecondYesSpeech().getMainSpeech();
                }

            } else if (main_flag == 3) {
                if (result.contains(YES)) {
                    return new MainThirdYesSpeech().getMainSpeech();
                }
            }

            if (result.contains(DESTROY)) {
                return new MainDestroySpeech().getMainSpeech();

            } else if (result.contains(STUDY)) {
                return new MainStudySpeech().getMainSpeech();

            } else if (result.contains(QUIZ)) {
                return new MainQuizSpeech().getMainSpeech();
            }

        } else if (status == 1) {

            if (result.contains(DESTROY)) {
                return new VideoDestroySpeech().getVideoSpeech();

            } else if (result.contains(PLAY)) {
                return new VideoPlaySpeech().getVideoSpeech();

            } else if (result.contains(STOP)) {
                return new VideoStopSpeech().getVideoSpeech();

            } else if (result.contains(NEXT)) {
                return new VideoNextSpeech().getVideoSpeech();

            } else if (result.contains(PREV)) {
                return new VideoPrevSpeech().getVideoSpeech();

            } else if (result.contains(RESET)) {
                return new VideoResetSpeech().getVideoSpeech();
            }

        } else if (status == 2) {
            // TODO
        }
        return NONE;
    }
}