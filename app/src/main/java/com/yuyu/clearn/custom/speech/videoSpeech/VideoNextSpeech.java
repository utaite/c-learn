package com.yuyu.clearn.custom.speech.videoSpeech;

import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class VideoNextSpeech extends VideoActivity implements VideoSpeech {

    @Override
    public String getVideoSpeech() {
        setV_ctime(getV_ctime() + getCONTROL_TIME());
        video_view.seekTo(getV_ctime());
        return SpeechFactory.NEXT;
    }
}