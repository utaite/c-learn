package com.yuyu.clearn.custom.speech.videoSpeech;

import com.yuyu.clearn.R;
import com.yuyu.clearn.activity.VideoActivity;
import com.yuyu.clearn.custom.speech.SpeechFactory;

public class VideoResetSpeech extends VideoActivity implements VideoSpeech {

    @Override
    public String getVideoSpeech() {
        setV_ctime(0);
        video_view.seekTo(getV_ctime());
        return SpeechFactory.RESET;
    }
}