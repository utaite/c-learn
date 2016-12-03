package com.yuyu.clearn.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yuyu.clearn.R;
import com.yuyu.clearn.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.activity_main, new MainFragment()).commit();
    }
}
