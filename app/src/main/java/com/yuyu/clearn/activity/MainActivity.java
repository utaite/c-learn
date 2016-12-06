package com.yuyu.clearn.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;
import com.yuyu.clearn.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.video_view)
    VrVideoView video_view;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_IS_PAUSED = "";
    private static final String STATE_PROGRESS_TIME = "";

    private Options options;
    private boolean isPaused;
    private String uri = "http://192.168.43.79/test/Miku.mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        options = new Options();
        options.inputFormat = Options.FORMAT_DEFAULT;
        options.inputType = Options.TYPE_MONO;
        try {
            video_view.loadVideo(Uri.parse(uri), options);
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
        video_view.setOnClickListener(view -> {
            isPaused = !isPaused;
            if (isPaused) {
                video_view.pauseVideo();
            } else {
                video_view.playVideo();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(STATE_PROGRESS_TIME, video_view.getCurrentPosition());
        savedInstanceState.putBoolean(STATE_IS_PAUSED, isPaused);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        video_view.seekTo(savedInstanceState.getLong(STATE_PROGRESS_TIME));
        isPaused = savedInstanceState.getBoolean(STATE_IS_PAUSED);
        if (isPaused) {
            video_view.pauseVideo();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        video_view.pauseRendering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        video_view.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        video_view.shutdown();
        super.onDestroy();
    }
}