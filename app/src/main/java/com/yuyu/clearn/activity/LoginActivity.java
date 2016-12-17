package com.yuyu.clearn.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.yuyu.clearn.R;
import com.yuyu.clearn.api.retrofit.Member;
import com.yuyu.clearn.api.retrofit.RestInterface;
import com.yuyu.clearn.view.Custom;
import com.yuyu.clearn.view.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.id_edit)
    AutoCompleteTextView id_edit;
    @BindView(R.id.pw_edit)
    EditText pw_edit;
    @BindView(R.id.check_btn)
    AppCompatCheckBox check_btn;
    @BindView(R.id.save_btn)
    AppCompatCheckBox save_btn;
    @BindView(R.id.login_btn)
    Button login_btn;
    @BindView(R.id.find_btn)
    Button find_btn;
    @BindView(R.id.register_btn)
    Button register_btn;

    private final String TAG = LoginActivity.class.getSimpleName();
    private final String LOGIN = "LOGIN", TOKEN = "TOKEN", STATUS = "STATUS", ID = "ID", PW = "PW", CHECK = "CHECK", SAVE = "SAVE";
    private Task task;
    private Context context;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        preferences = getSharedPreferences(LOGIN, MODE_PRIVATE);
        buttonCustom(context, Typeface.createFromAsset(getAssets(), Custom.FONT), login_btn, find_btn, register_btn);
        // 키보드 버튼 옵션 설정
        id_edit.setOnEditorActionListener((v, actionId, event) -> {
            pw_edit.requestFocus();
            return true;
        });
        // 아이디 저장, 자동 로그인이 활성화 되어있는지 STATUS로 확인 후 분기에 맞게 실행
        Observable.just(preferences.getString(STATUS, null))
                .filter(s -> s != null)
                .flatMap(s -> Observable.just(s)
                        .groupBy(status -> status.equals(CHECK)))
                .subscribe(group -> {
                    id_edit.setText(preferences.getString(ID, null));
                    pw_edit.setText(group.getKey() ? preferences.getString(PW, null) : null);
                    check_btn.setChecked(group.getKey());
                    save_btn.setChecked(!group.getKey());
                    if (group.getKey()) {
                        loginPrepare();
                    }
                });
    }

    // OnClick 메소드 설정
    @OnClick({R.id.login_btn, R.id.register_btn, R.id.find_btn})
    public void onButtonMethod(View view) {
        int vid = view.getId();
        if (vid == R.id.login_btn) {
            loginPrepare();
        } else if (vid == R.id.register_btn || vid == R.id.find_btn) {
            if (networkCheck(context)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Custom.BASE + (vid == R.id.register_btn ? "A" : "B"))));
            } else {
                TastyToast.makeText(context, getString(R.string.login_internet_err), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }
    }

    // 아이디 저장 설정 / 자동 로그인과 중복되지 않음
    @OnClick({R.id.check_btn, R.id.check_txt})
    public void onCheckMethod(View view) {
        if (view.getId() == R.id.check_txt) {
            check_btn.setChecked(!check_btn.isChecked());
        }
        if (check_btn.isChecked()) {
            save_btn.setChecked(false);
        }
    }

    // 자동 로그인 설정 / 아이디 저장과 중복되지 않음
    @OnClick({R.id.save_btn, R.id.save_txt})
    public void onSaveMethod(View view) {
        if (view.getId() == R.id.save_txt) {
            save_btn.setChecked(!save_btn.isChecked());
        }
        if (save_btn.isChecked()) {
            check_btn.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (Custom.CURRENT_TIME + Custom.BACK_TIME < System.currentTimeMillis()) {
            Custom.CURRENT_TIME = System.currentTimeMillis();
            TastyToast.makeText(context, getString(R.string.onBackPressed), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
        } else {
            super.onBackPressed();
        }
    }

    public void loginPrepare() {
        if (!networkCheck(context)) {
            TastyToast.makeText(context, getString(R.string.login_internet_err), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else {
            String[] txtValue = new String[2];
            Observable.just(id_edit, pw_edit)
                    // EditText 공백 여부 확인
                    .filter(editText -> {
                        if (TextUtils.isEmpty(editText.getText().toString())) {
                            editText.setError(getString(R.string.login_required_err));
                            editText.requestFocus();
                        }
                        return !TextUtils.isEmpty(editText.getText().toString());
                    })
                    .map(editText -> editText.getText().toString())
                    .doOnUnsubscribe(() -> {
                        if (txtValue[0] != null && txtValue[1] != null) {
                            task = new Task(context);
                            task.onPreExecute();
                            Call<Member> loginCall = new Retrofit.Builder()
                                    .baseUrl(Custom.BASE + "/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(RestInterface.PostLogin.class)
                                    .login(txtValue[0], txtValue[1]);
                            loginCall.enqueue(new Callback<Member>() {
                                @Override
                                public void onResponse(Call<Member> call, Response<Member> response) {
                                    task.onPostExecute(null);
                                    Log.e(txtValue[0], txtValue[1]);
                                    loginComplete(response.body(), txtValue[0], txtValue[1]);
                                }

                                @Override
                                public void onFailure(Call<Member> call, Throwable t) {
                                    task.onPostExecute(null);
                                    Log.e(TAG, String.valueOf(t));
                                    TastyToast.makeText(context, getString(R.string.login_server_err), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                }
                            });
                        }
                    })
                    .subscribe(s -> {
                        if (s.equals(id_edit.getText().toString())) {
                            txtValue[0] = s;
                            preferences.edit().putString(STATUS, check_btn.isChecked() ? CHECK : save_btn.isChecked() ? SAVE : null).apply();
                            preferences.edit().putString(ID, check_btn.isChecked() ? s : save_btn.isChecked() ? s : null).apply();
                        } else {
                            txtValue[1] = s;
                            preferences.edit().putString(PW, check_btn.isChecked() ? s : null).apply();
                        }
                    });
        }
    }

    public void loginComplete(Member repo, String txtValue0, String txtValue1) {
        int v_num = repo.getV_num();
        String beforeToken = repo.getP_token();
        String afterToken = getSharedPreferences(TOKEN, MODE_PRIVATE).getString(TOKEN, txtValue0);
        if (v_num == -1) {
            TastyToast.makeText(context, getString(R.string.login_failed), TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            // 로그인에 성공했으나 가져온 토큰의 값이 저장된 토큰의 값과 다를경우
            // (처음 로그인을 했을 경우 or 클라이언트의 토큰 값이 변경되었을 경우)
            // 계정 연동을 시키고 로그인 화면 재실행
        } else if (!beforeToken.equals(afterToken)) {
            new MaterialDialog.Builder(context)
                    .content(beforeToken.equals(txtValue0) ? getString(R.string.login_token_new) : getString(R.string.login_token_change))
                    .positiveText(getString(R.string.login_yes))
                    .negativeText(getString(R.string.login_no))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                        task.onPreExecute();
                        Call<Void> tokenCall = new Retrofit.Builder()
                                .baseUrl(Custom.BASE + "/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(RestInterface.PostToken.class)
                                .token(txtValue0, txtValue1, afterToken, beforeToken);

                        tokenCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                task.onPostExecute(null);
                                TastyToast.makeText(context, getString(R.string.login_success), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, String.valueOf(t));
                            }
                        });
                    })
                    .onNegative((dialog, which) -> dialog.cancel()).show();

        } else if (v_num == 0) {
            TastyToast.makeText(context, getString(R.string.login_video_err), TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);

        } else {
            // 로그인에 성공하면 response 받은 v_num과 p_token을 다음 액티비티로 전달하고 실행
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra(Custom.V_NUM, v_num);
            intent.putExtra(Custom.P_TOKEN, beforeToken);
            startActivity(intent);
            finish();
        }
    }

    // 버튼 커스텀 설정
    public void buttonCustom(Context context, Typeface typeface, Button... btns) {
        Observable.from(btns)
                .subscribe(btn -> {
                    btn.setTypeface(typeface);
                    btn.animate()
                            .translationY(btn.getBottom() + 100 * (context.getResources().getDisplayMetrics().density))
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(0)
                            .setListener(new Animator.AnimatorListener() {

                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    btn.setVisibility(View.VISIBLE);
                                    btn.animate()
                                            .translationY(0 - 5 * (context.getResources().getDisplayMetrics().density))
                                            .setInterpolator(new DecelerateInterpolator())
                                            .setDuration(500)
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });
                });
    }

    // 네트워크 연결 여부 확인
    public boolean networkCheck(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }

}