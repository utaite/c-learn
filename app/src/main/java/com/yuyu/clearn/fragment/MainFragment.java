package com.yuyu.clearn.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.vr.sdk.widgets.video.VrVideoView;
import com.yuyu.clearn.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    @BindView(R.id.video_view)
    VrVideoView video_view;

    private static final String TAG = MainFragment.class.getSimpleName();

    private VrVideoView.Options options;
    private int count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        options = new VrVideoView.Options();
        options.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
        options.inputType = VrVideoView.Options.TYPE_MONO;
        video_view.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_STEREO);
        try {
            video_view.loadVideoFromAsset("TEST1.mp4", options);
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
        video_view.fullScreenDialog.setCancelable(false);
        video_view.fullScreenDialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.KEYCODE_BACK) {
                try {
                    video_view.loadVideoFromAsset((count % 3 == 1) ? "TEST2.mp4" : (count % 3 == 2) ? "TEST3.mp4" : "TEST1.mp4" , options);
                    count++;
                    Log.e("COUNT", count + "");
                } catch (IOException e) {
                    Log.e(TAG, String.valueOf(e));
                }
            }
            return false;
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        video_view.pauseRendering();
    }

    @Override
    public void onResume() {
        super.onResume();
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getActivity().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        video_view.resumeRendering();
    }

}