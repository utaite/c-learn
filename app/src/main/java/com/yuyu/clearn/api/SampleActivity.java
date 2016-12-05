package com.yuyu.clearn.api;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.yuyu.clearn.R;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleActivity extends Activity {

    @BindView(R.id.txt_result)
    TextView txtResult;
    @BindView(R.id.btn_start)
    Button btnStart;

    private static final String TAG = SampleActivity.class.getSimpleName();
    private static final String CLIENT_ID = "hgjHh11TeYg649dN5zT1";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                txtResult.setText((String) (msg.obj));
                break;

            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for (String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                txtResult.setText(strBuf.toString());
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                txtResult.setText("Error code : " + msg.obj.toString());
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        btnStart.setOnClickListener(v -> {
            if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                txtResult.setText("Connecting...");
                btnStart.setText(R.string.str_stop);
                naverRecognizer.recognize();
            } else {
                Log.e(TAG, "stop and wait Final Result");
                btnStart.setEnabled(false);
                naverRecognizer.getSpeechRecognizer().stop();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<SampleActivity> mActivity;

        RecognitionHandler(SampleActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SampleActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
