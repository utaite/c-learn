package com.yuyu.clearn.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.yuyu.clearn.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.id_edit)
    AutoCompleteTextView id_edit;
    @BindView(R.id.pw_edit)
    EditText pw_edit;
    @BindView(R.id.check_btn)
    AppCompatCheckBox check_btn;

    private String id, pw;
    private long currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        id = getSharedPreferences("login", MODE_PRIVATE).getString("id", null);
        pw = getSharedPreferences("login", MODE_PRIVATE).getString("pw", null);
        if(id != null && pw != null) {
            id_edit.setText(id);
            pw_edit.setText(pw);
            check_btn.setChecked(true);
            loginMethod();
        }
        id_edit.setOnEditorActionListener((v, actionId, event) -> {
            pw_edit.requestFocus();
            return true;
        });
        pw_edit.setOnEditorActionListener((v, actionId, event) -> {
            loginMethod();
            return true;
        });
    }

    @OnClick({R.id.login_btn, R.id.register_btn, R.id.find_btn, R.id.check_btn, R.id.check_txt})
    public void onClickMethod(View view) {
        int vid = view.getId();
        if (vid == R.id.login_btn) {
            loginMethod();
        } else if (vid == R.id.register_btn) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com")));
        } else if (vid == R.id.find_btn) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.co.kr")));
        } else if (vid == R.id.check_txt) {
            check_btn.setChecked(!check_btn.isChecked());
        }
    }

    @Override
    public void onBackPressed() {
        if (currentTime + 2000 < System.currentTimeMillis()) {
            currentTime = System.currentTimeMillis();
            Toast.makeText(this, getString(R.string.onBackPressed), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    public void keyboardDown() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(id_edit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pw_edit.getWindowToken(), 0);
    }

    public void loginMethod() {
        id_edit.setError(null);
        pw_edit.setError(null);
        id = id_edit.getText().toString();
        pw = pw_edit.getText().toString();
        if (TextUtils.isEmpty(id)) {
            id_edit.setError(getString(R.string.error_field_required));
            id_edit.requestFocus();
        } else if (TextUtils.isEmpty(pw)) {
            pw_edit.setError(getString(R.string.error_field_required));
            pw_edit.requestFocus();
        } else {
            keyboardDown();
            getSharedPreferences("login", MODE_PRIVATE).edit().putString("id", (check_btn.isChecked()) ? id : null).apply();
            getSharedPreferences("login", MODE_PRIVATE).edit().putString("pw", (check_btn.isChecked()) ? pw : null).apply();
            Snackbar.make(findViewById(R.id.login_activity), "LOGIN COMPLETE: " + check_btn.isChecked(), Snackbar.LENGTH_SHORT).show();
        }
    }

}

