package com.yuyu.clearn.custom.speech.videoSpeech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class VideoPlaySpeech extends VideoActivity implements VideoSpeech {

    @Override
    public String getVideoSpeech() {
        setIsPause(true);
        video_view.playVideo();
        return SpeechFactory.PLAY;
    }
}