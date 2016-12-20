package com.yuyu.clearn.custom.speech.videoSpeech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class VideoStopSpeech extends VideoActivity implements VideoSpeech {

    @Override
    public String getVideoSpeech() {
        setIsPause(false);
        video_view.pauseVideo();
        return SpeechFactory.STOP;
    }
}