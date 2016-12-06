package com.yuyu.clearn.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.yuyu.clearn.R;
import com.yuyu.clearn.api.AudioWriterPCM;
import com.yuyu.clearn.api.NaverRecognizer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.video_view)
    VrVideoView video_view;
    @BindView(R.id.sound_png)
    ImageView sound_png;
    @BindView(R.id.wait_gif)
    ImageView wait_gif;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "hgjHh11TeYg649dN5zT1";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private Options options;

    private boolean isPaused;
    private long time;
    private String uri = "http://192.168.43.79/test/Miku.mp4";

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                for (String result : results) {
                    if (result.contains("종료")) {
                        finish();
                    } else if (result.contains("재생")) {
                        isPaused = true;
                        video_view.playVideo();
                    } else if (result.contains("정지")) {
                        isPaused = false;
                        video_view.pauseVideo();
                    }
                }
                Glide.with(this).load(android.R.color.transparent).into(wait_gif);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        sound_png.getDrawable().setAlpha(150);
        sound_png.setVisibility(View.GONE);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        options = new Options();
        options.inputFormat = Options.FORMAT_DEFAULT;
        options.inputType = Options.TYPE_MONO;
        try {
            video_view.loadVideo(Uri.parse(uri), options);
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
        video_view.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    Glide.with(this).load(android.R.color.transparent).into(wait_gif);
                    sound_png.setVisibility(View.VISIBLE);
                    naverRecognizer.recognize();
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            sound_png.setVisibility(View.GONE);
                            Glide.with(getApplicationContext()).load(R.drawable.wait_gif).asGif().into(wait_gif);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }.sendEmptyMessageDelayed(0, 2000);
                }
            }
            return true;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        naverRecognizer.getSpeechRecognizer().release();
        video_view.pauseRendering();
        time = video_view.getCurrentPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        naverRecognizer.getSpeechRecognizer().initialize();
        video_view.resumeRendering();
        video_view.seekTo(time);
        if (isPaused) {
            video_view.pauseVideo();
        } else {
            video_view.playVideo();
        }
    }

    @Override
    public void onDestroy() {
        video_view.shutdown();
        super.onDestroy();
    }

    private static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}