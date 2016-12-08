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

import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.yuyu.clearn.R;
import com.yuyu.clearn.api.AudioWriterPCM;
import com.yuyu.clearn.api.NaverRecognizer;
import com.yuyu.clearn.retrofit.Video;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class VideoActivity extends AppCompatActivity {

    public interface PostVideo {
        @FormUrlEncoded
        @POST("/api/video")
        Call<Video> video(@Field("a_num") int a_num,
                          @Field("m_token") String m_token);
    }

    public interface PostFinish {
        @FormUrlEncoded
        @POST("/api/finish")
        Call<Video> finish(@Field("a_num") int a_num);
    }

    public interface PostSave {
        @FormUrlEncoded
        @POST("/api/save")
        Call<Video> save(@Field("a_num") int a_num,
                         @Field("a_ctime") long a_ctime);
    }

    @BindView(R.id.video_view)
    VrVideoView video_view;

    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final String CLIENT_ID = "hgjHh11TeYg649dN5zT1";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private Options options;

    private boolean isPaused;
    private long a_ctime;

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
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        options = new Options();
        options.inputFormat = Options.FORMAT_DEFAULT;
        options.inputType = Options.TYPE_MONO;
        video_view.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !naverRecognizer.getSpeechRecognizer().isRunning()) {
                naverRecognizer.recognize();
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }.sendEmptyMessageDelayed(0, 2000);
            }
            return true;
        });
        video_view.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_STEREO);
        video_view.fullScreenDialog.setCancelable(false);
        Call<Video> repos = new Retrofit.Builder()
                .baseUrl("http://utaitebox.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostVideo.class)
                .video(getIntent().getIntExtra("a_num", -1), getIntent().getStringExtra("m_token"));
        repos.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Video repo = response.body();
                try {
                    video_view.loadVideo(Uri.parse(repo.getA_uri()), options);
                    video_view.seekTo(repo.getA_ctime());
                    new Thread(() -> {
                        while (true) {
                            a_ctime = video_view.getCurrentPosition();
                            if (a_ctime == video_view.getDuration()) {
                                new Retrofit.Builder()
                                        .baseUrl("http://utaitebox.com")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                        .create(PostFinish.class)
                                        .finish(getIntent().getIntExtra("a_num", -1));
                                finish();
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    Log.e(TAG, String.valueOf(e));
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e(TAG, String.valueOf(t));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        naverRecognizer.getSpeechRecognizer().release();
        video_view.pauseRendering();
        a_ctime = video_view.getCurrentPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        naverRecognizer.getSpeechRecognizer().initialize();
        video_view.resumeRendering();
        video_view.seekTo(a_ctime);
        if (isPaused) {
            video_view.pauseVideo();
        } else {
            video_view.playVideo();
        }
    }

    @Override
    public void onDestroy() {
        video_view.shutdown();
        new Retrofit.Builder()
                .baseUrl("http://utaitebox.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostSave.class)
                .save(getIntent().getIntExtra("a_num", -1), a_ctime);
        super.onDestroy();
    }

    private static class RecognitionHandler extends Handler {
        private final WeakReference<VideoActivity> mActivity;

        RecognitionHandler(VideoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}