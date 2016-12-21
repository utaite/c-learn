package com.yuyu.clearn.activity;import android.content.Context;import android.media.MediaPlayer;import android.net.Uri;import android.os.Bundle;import android.os.Environment;import android.os.Handler;import android.os.Message;import android.support.v7.app.AppCompatActivity;import android.util.Log;import android.view.KeyEvent;import android.view.View;import com.google.vr.sdk.widgets.video.VrVideoView;import com.naver.speech.clientapi.SpeechRecognitionResult;import com.yuyu.clearn.R;import com.yuyu.clearn.api.realm.UserVO;import com.yuyu.clearn.api.reognizer.AudioWriterPCM;import com.yuyu.clearn.api.reognizer.NaverRecognizer;import com.yuyu.clearn.api.retrofit.MemberVO;import com.yuyu.clearn.api.retrofit.RestInterface;import com.yuyu.clearn.custom.Constant;import java.io.IOException;import java.lang.ref.WeakReference;import java.text.SimpleDateFormat;import java.util.Date;import java.util.List;import butterknife.BindView;import butterknife.ButterKnife;import io.realm.Realm;import io.realm.RealmConfiguration;import rx.Observable;import rx.Subscriber;public class VideoActivity extends AppCompatActivity {    @BindView(R.id.video_view)    public VrVideoView video_view;    private final String TAG = VideoActivity.class.getSimpleName();    private final int START_TIME = 1000, SEND_TIME = 2500, CONTROL_TIME = 20000;    private final int FLAG_1_TIME = 6000, FLAG_2_TIME = 16000, FLAG_3_TIME = 26000;    private final int MAIN_SCREEN = 0, VIDEO_SCREEN = 1, QUIZ_SCREEN = 2, ANSWER_SCREEN = 3;    private final String CLIENT_ID = "hgjHh11TeYg649dN5zT1", TEST = "Test", SPEECH_TEST = "/NaverSpeech" + TEST, DATE_TYPE = "yyyy-MM-dd:HH-mm-ss";    private final String VIDEO = "VIDEO", NUMBER = "NUMBER", FINISH = "FINISH", SAVE = "SAVE", RESULT = "RESULT", REALM_NAME = "CLearn.db";    private final String NONE = "", DESTROY = "나가기", PLAY = "시작", STOP = "멈춰", NEXT = "앞으로", PREV = "뒤로", RESET = "처음으로";    private final String YES = "응", NO = "아니", STUDY = "공부", QUIZ = "퀴즈";    private Realm realm;    private UserVO userVO;    private Thread thread;    private Context context;    private MediaPlayer mediaPlayer;    private VrVideoView.Options options;    private AudioWriterPCM audioWriterPCM;    private NaverRecognizer naverRecognizer;    private int status, main_flag, quiz_flag;    private long v_ctime, load_time;    private boolean isPause, quiz_finish, quiz_answer[];    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_video);        ButterKnife.bind(this);        context = this;        quiz_answer = new boolean[4];        options = new VrVideoView.Options();        options.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;        options.inputType = VrVideoView.Options.TYPE_MONO;        naverRecognizer = new NaverRecognizer(context, new RecognitionHandler(this), CLIENT_ID);        // 시작 시 풀 스크린 모드로 변경        video_view.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_STEREO);        video_view.fullScreenDialog.setCancelable(false);        video_view.fullScreenDialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {            // 뒤로가기 버튼이 터치 되었을 경우(컨트롤러 포함) 음성 인식 이벤트를 2초간 받음            if (keyEvent.getAction() == KeyEvent.ACTION_UP && !naverRecognizer.getSpeechRecognizer().isRunning()) {                naverRecognizer.recognize();                mediaPlayerInit(R.raw.start);                new Handler() {                    @Override                    public void handleMessage(Message msg) {                        naverRecognizer.getSpeechRecognizer().stop();                    }                }.sendEmptyMessageDelayed(0, SEND_TIME);            }            return false;        });        // 첫 로딩 시간을 고려해서 1초 대기        new Handler() {            @Override            public void handleMessage(Message msg) {                mainScreenViewFirst();            }        }.sendEmptyMessageDelayed(0, START_TIME);    }    @Override    public void onResume() {        super.onResume();        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION ^ View.SYSTEM_UI_FLAG_FULLSCREEN ^ View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);        naverRecognizer.getSpeechRecognizer().initialize();        video_view.resumeRendering();        video_view.seekTo(v_ctime);        if (isPause) {            video_view.pauseVideo();        } else {            video_view.playVideo();        }    }    @Override    public void onPause() {        super.onPause();        naverRecognizer.getSpeechRecognizer().release();        v_ctime = video_view.getCurrentPosition();        video_view.pauseRendering();    }    // 메인 화면 영상을 띄워주기 전 Realm DB 작업 시작    public void mainScreenViewFirst() {        Realm.init(context);        Observable.just(realm = Realm.getInstance(new RealmConfiguration.Builder()                .name(REALM_NAME)                .build()))                .subscribe(realm1 -> {                    realm1.beginTransaction();                    Observable.just(userVO = realm1.createObject(UserVO.class, getSharedPreferences(VIDEO, MODE_PRIVATE).getInt(NUMBER, 0)))                            .doOnUnsubscribe(() -> {                                mainScreenView(main_flag = 0);                                createThread();                            })                            .subscribe(userVO -> {                                userVO.setV_num(getIntent().getIntExtra(Constant.V_NUM, -1));                                userVO.setP_token(getIntent().getStringExtra(Constant.P_TOKEN));                                userVO.setStart_time(new SimpleDateFormat(DATE_TYPE).format(new Date()));                            });                });    }    public void mainScreenView(int main_flag) {        load_time = System.currentTimeMillis();        status = MAIN_SCREEN;        try {            video_view.loadVideo(Uri.parse(RestInterface.BASE + RestInterface.RESOURCES + RestInterface.VIDEO + RestInterface.MAIN_SCREEN), options);        } catch (IOException e) {            Log.e(TAG, String.valueOf(e));        }        mainScreenSeek(main_flag);    }    // 메인 화면 영상을 띄워줌    public void mainScreenSeek(int main_flag) {        load_time = System.currentTimeMillis();        status = MAIN_SCREEN;        video_view.seekTo(main_flag == 0 ? 0 : main_flag == 1 ? FLAG_1_TIME : main_flag == 2 ? FLAG_2_TIME : FLAG_3_TIME);    }    // 비디오 화면 영상을 띄워주기 전 v_num과 p_token을 request하여 그에 알맞는 정보를 response    public void videoScreenViewFirst() {        RestInterface.getRestClient()                .create(RestInterface.PostVideo.class)                .video(VIDEO.toLowerCase(), getIntent().getIntExtra(Constant.V_NUM, -1), getIntent().getStringExtra(Constant.P_TOKEN))                .subscribe(new Subscriber<MemberVO>() {                    @Override                    public void onCompleted() {                    }                    @Override                    public void onError(Throwable e) {                        Log.e(TAG, String.valueOf(e));                    }                    @Override                    public void onNext(MemberVO memberVO) {                        Observable.just(memberVO.getCt_file())                                .map(text -> Uri.parse(RestInterface.BASE + RestInterface.RESOURCES + RestInterface.VIDEO + memberVO.getCt_file()))                                .doOnUnsubscribe(() -> video_view.seekTo(v_ctime = memberVO.getV_ctime()))                                .subscribe(uri -> videoScreenView(uri));                    }                });    }    // 비디오 화면 영상을 띄워줌    public void videoScreenView(Uri uri) {        load_time = System.currentTimeMillis();        status = VIDEO_SCREEN;        try {            video_view.loadVideo(uri, options);        } catch (IOException e) {            Log.e(TAG, String.valueOf(e));        }    }    public void quizScreenView(int quiz_flag) {        quiz_finish = false;        load_time = System.currentTimeMillis();        status = QUIZ_SCREEN;        try {            video_view.loadVideo(Uri.parse(RestInterface.BASE + RestInterface.RESOURCES + RestInterface.VIDEO +                    (quiz_flag == 1 ? RestInterface.QUIZ_1 : quiz_flag == 2 ? RestInterface.QUIZ_2 : quiz_flag == 3 ?                            RestInterface.QUIZ_3 : RestInterface.QUIZ_4)), options);        } catch (IOException e) {            Log.e(TAG, String.valueOf(e));        }        video_view.seekTo(0);    }    public void answerScreenView(int quiz_flag, boolean quiz_answer) {        load_time = System.currentTimeMillis();        status = ANSWER_SCREEN;        try {            if (quiz_flag == 1 || quiz_flag == 2 || quiz_flag == 3 || quiz_flag == 4) {                video_view.loadVideo(Uri.parse(RestInterface.BASE + RestInterface.RESOURCES + RestInterface.VIDEO +                        (quiz_answer ? RestInterface.YES : RestInterface.NO)), options);            } else {                video_view.loadVideo(Uri.parse(RestInterface.BASE + RestInterface.RESOURCES + RestInterface.VIDEO +                        (quiz_answer ? RestInterface.NO : RestInterface.YES)), options);            }        } catch (IOException e) {            Log.e(TAG, String.valueOf(e));        }        video_view.seekTo(0);    }    // 미디어 플레이어 혹은 비디오 플레이어가 종료되면 실행되는 인터페이스인    // OnCompletionListener가 VrVideoView에 없는 관계로 직접 구현    // Thread를 돌려서 해당 동영상이 종료되면(총 재생 시간보다 현재 재생 시간이 많거나 같을 경우)    // 메인 -> 무한 반복 / 비디오 -> PostFinish 인터페이스를 사용해 v_num과 v_finish를 request    public void createThread() {        Runnable runnable = () -> {            boolean videoFinish = false;            while (!thread.isInterrupted()) {                v_ctime = video_view.getCurrentPosition();                if (status == MAIN_SCREEN) {                    if (v_ctime < FLAG_1_TIME) {                        main_flag = 0;                    } else if (v_ctime < FLAG_2_TIME) {                        main_flag = 1;                    } else if (v_ctime < FLAG_3_TIME) {                        main_flag = 2;                    } else {                        main_flag = 3;                    }                }                if (v_ctime >= video_view.getDuration() && load_time + 1000 <= System.currentTimeMillis() && !videoFinish) {                    switch (status) {                        case MAIN_SCREEN:                            mainScreenSeek(main_flag < 3 ? ++main_flag : (main_flag = 1));                            break;                        case VIDEO_SCREEN:                            videoFinish = true;                            RestInterface.getRestClient()                                    .create(RestInterface.PostFinish.class)                                    .finish(FINISH.toLowerCase(), getIntent().getIntExtra(Constant.V_NUM, -1))                                    .subscribe(new Subscriber<Void>() {                                        @Override                                        public void onCompleted() {                                        }                                        @Override                                        public void onError(Throwable e) {                                            Log.e(TAG, String.valueOf(e));                                        }                                        @Override                                        public void onNext(Void aVoid) {                                            mainScreenView(main_flag = 1);                                        }                                    });                            break;                        case QUIZ_SCREEN:                            if (quiz_finish) {                                answerScreenView(quiz_flag, quiz_answer[quiz_flag]);                            } else {                                video_view.seekTo(0);                            }                            break;                        case ANSWER_SCREEN:                            if (quiz_flag < 4) {                                quizScreenView(++quiz_flag);                            } else {                                mainScreenView(main_flag = 1);                            }                        default:                            break;                    }                } else if (videoFinish) {                    videoFinish = false;                }            }        };        thread = new Thread(runnable);        thread.start();    }    public void handleMessage(Message msg) {        switch (msg.what) {            case R.id.clientReady:                audioWriterPCM = new AudioWriterPCM(                        Environment.getExternalStorageDirectory().getAbsolutePath() + SPEECH_TEST);                audioWriterPCM.open(TEST);                break;            case R.id.audioRecording:                audioWriterPCM.write((short[]) msg.obj);                break;            case R.id.finalResult:                recognizerResult(msg);                break;            case R.id.recognitionError:                if (audioWriterPCM != null) {                    audioWriterPCM.close();                }                break;            case R.id.clientInactive:                if (audioWriterPCM != null) {                    audioWriterPCM.close();                }                break;            default:                break;        }    }    public void recognizerResult(Message msg) {        SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;        List<String> results = speechRecognitionResult.getResults();        boolean isSpeechEvent = false;        // 음성 인식 이벤트 처리        for (String result : results) {            if (!getSpeech(status, main_flag, result).equals(NONE)) {                isSpeechEvent = true;                break;            }        }        if (!isSpeechEvent) {            mediaPlayerInit(R.raw.re);        }    }    //현재 재생 시간을 PostSave 인터페이스를 사용해 v_num과 v_ctime을 request    public void videoSave() {        RestInterface.getRestClient()                .create(RestInterface.PostSave.class)                .save(SAVE.toLowerCase(), getIntent().getIntExtra(Constant.V_NUM, -1), v_ctime)                .subscribe(new Subscriber<Void>() {                    @Override                    public void onCompleted() {                    }                    @Override                    public void onError(Throwable e) {                        Log.e(TAG, String.valueOf(e));                    }                    @Override                    public void onNext(Void aVoid) {                        mainScreenView(main_flag = 1);                    }                });    }    // Realm DB 작업 종료 후 p_token과 함께 request    // OnCompletionListener 역할을 대신 수행하는 Thread 및 어플을 종료    public void videoDestroy(Realm realm, UserVO userVO) {        getSharedPreferences(VIDEO, MODE_PRIVATE).edit().putInt(NUMBER, getSharedPreferences(VIDEO, MODE_PRIVATE).getInt(NUMBER, 0) + 1).apply();        Observable.just(realm)                .doOnUnsubscribe(() -> Observable.just(realm)                        .map(realm2 -> realm2.where(UserVO.class).findAll().toString())                        .subscribe(realm2 -> {                            RestInterface.getRestClient()                                    .create(RestInterface.PostResult.class)                                    .result(RESULT.toLowerCase(), getIntent().getStringExtra(Constant.P_TOKEN), realm2)                                    .subscribe(new Subscriber<Void>() {                                        @Override                                        public void onCompleted() {                                        }                                        @Override                                        public void onError(Throwable e) {                                            Log.e(TAG, String.valueOf(e));                                        }                                        @Override                                        public void onNext(Void aVoid) {                                            mediaPlayerInit(R.raw.destroy);                                            thread.interrupt();                                            video_view.fullScreenDialog.dismiss();                                            video_view.pauseRendering();                                            video_view.shutdown();                                            finish();                                        }                                    });                        }))                .subscribe(realm1 -> {                    Log.e(TAG, "subscribe");                    userVO.setEnd_time(new SimpleDateFormat(DATE_TYPE).format(new Date()));                    realm1.insert(userVO);                    realm1.commitTransaction();                });    }    public void mediaPlayerInit(int resId) {        Observable.just(mediaPlayer)                .filter(mediaPlayer1 -> mediaPlayer1 != null)                .subscribe(mediaPlayer1 -> {                    mediaPlayer1.stop();                    mediaPlayer1.release();                });        Observable.just(mediaPlayer = MediaPlayer.create(context, resId))                .subscribe(mediaPlayer1 -> mediaPlayer1.setOnPreparedListener(MediaPlayer::start));    }    private static class RecognitionHandler extends Handler {        private final WeakReference<VideoActivity> weakReference;        RecognitionHandler(VideoActivity activity) {            weakReference = new WeakReference<>(activity);        }        @Override        public void handleMessage(Message msg) {            VideoActivity activity = weakReference.get();            if (activity != null) {                activity.handleMessage(msg);            }        }    }    public String getSpeech(int status, int main_flag, String result) {        switch (status) {            case MAIN_SCREEN:                if (main_flag == 1) {                    if (result.contains(YES)) {                        videoScreenViewFirst();                        return YES;                    }                } else if (main_flag == 2) {                    if (result.contains(YES)) {                        quizScreenView(quiz_flag = 1);                        return YES;                    }                } else if (main_flag == 3) {                    if (result.contains(YES)) {                        videoDestroy(realm, userVO);                        return DESTROY;                    }                }                if (result.contains(DESTROY)) {                    videoDestroy(realm, userVO);                    return DESTROY;                } else if (result.contains(STUDY)) {                    videoScreenViewFirst();                    return STUDY;                } else if (result.contains(QUIZ)) {                    quizScreenView(quiz_flag = 1);                    return QUIZ;                }                break;            case VIDEO_SCREEN:                if (result.contains(DESTROY)) {                    videoSave();                    return DESTROY;                } else if (result.contains(PLAY)) {                    isPause = true;                    video_view.playVideo();                    return PLAY;                } else if (result.contains(STOP)) {                    isPause = false;                    video_view.pauseVideo();                    return STOP;                } else if (result.contains(NEXT)) {                    video_view.seekTo(v_ctime += CONTROL_TIME);                    return NEXT;                } else if (result.contains(PREV)) {                    video_view.seekTo(v_ctime += CONTROL_TIME);                    return PREV;                } else if (result.contains(RESET)) {                    video_view.seekTo(v_ctime = 0);                    return RESET;                }                break;            case QUIZ_SCREEN:                quiz_finish = true;                if (result.contains(YES)) {                    quiz_answer[quiz_flag] = true;                    return YES;                } else if (result.contains(NO)) {                    quiz_answer[quiz_flag] = false;                    return NO;                }                break;            default:                break;        }        return NONE;    }}