package com.yuyu.clearn.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuyu.clearn.R;
import com.yuyu.clearn.retrofit.Member;
import com.yuyu.clearn.view.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {

    // FCM token을 사용한 JWT 형식의 로그인 연동
    // id와 password를 서버에 request 이후 일치하는 계정이 있다면 p_token과 v_num을 response 받음
    public interface PostLogin {
        @FormUrlEncoded
        @POST("api/login")
        Call<Member> login(@Field("p_id") String id,
                           @Field("p_pw") String pw);
    }

    // 처음 로그인을 했을 경우 or 클라이언트의 토큰 값이 변경되었을 경우
    // 변경 전과 변경 후의 token을 전부 request
    public interface PostToken {
        @FormUrlEncoded
        @POST("api/token")
        Call<Void> token(@Field("p_id") String id,
                         @Field("p_pw") String pw,
                         @Field("p_token") String afterToken,
                         @Field("before_token") String beforeToken);
    }

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

    public static final String BASE = "http://192.168.43.79/CLearn";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String CHECK = "CHECK", SAVE = "SAVE", NONE = "NONE";

    private Toast mToast;
    private Context context;
    private String id, pw, status;
    private long currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        buttonSetting(login_btn);
        buttonSetting(find_btn);
        buttonSetting(register_btn);
        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);
        // 아이디 저장, 자동 로그인이 활성화 되어있는지 status로 확인 후 분기에 맞게 실행
        status = getSharedPreferences("login", MODE_PRIVATE).getString("status", NONE);
        id = getSharedPreferences("login", MODE_PRIVATE).getString("id", null);
        pw = getSharedPreferences("login", MODE_PRIVATE).getString("pw", null);
        if (status.equals(CHECK)) {
            id_edit.setText(id);
            pw_edit.setText(pw);
            check_btn.setChecked(true);
            loginMethod();
        } else if (status.equals(SAVE)) {
            id_edit.setText(id);
            save_btn.setChecked(true);
        }
        // 키보드 버튼 옵션 설정
        id_edit.setOnEditorActionListener((v, actionId, event) -> {
            pw_edit.requestFocus();
            return true;
        });
    }

    // OnClick 메소드 설정
    @OnClick({R.id.login_btn, R.id.register_btn, R.id.find_btn})
    public void onButtonMethod(View view) {
        int vid = view.getId();
        if (vid == R.id.login_btn) {
            loginMethod();
        } else if (vid == R.id.register_btn) {
            checkMethod(BASE);
        } else if (vid == R.id.find_btn) {
            checkMethod(BASE);
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
        if (currentTime + 2000 < System.currentTimeMillis()) {
            currentTime = System.currentTimeMillis();
            mToast.setText(getString(R.string.onBackPressed));
            mToast.show();
        } else {
            super.onBackPressed();
        }
    }

    // 버튼 커스텀 설정
    public void buttonSetting(Button btn) {
        btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
        btn.animate().translationY(btn.getBottom() + 100 * (context.getResources().getDisplayMetrics().density)).setInterpolator(new AccelerateInterpolator()).setDuration(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                btn.setVisibility(View.VISIBLE);
                btn.animate().translationY(0 - 5 * (context.getResources().getDisplayMetrics().density)).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }

    // 키보드 내리기
    public void keyboardDown() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(id_edit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pw_edit.getWindowToken(), 0);
    }

    // 인터넷 연결 확인 후 연결되지 않았다면 다음 액티비티로 넘어가지 않음
    public void checkMethod(String uri) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            mToast.setText(getString(R.string.internet_error));
            mToast.show();
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    }

    // PostLogin 인터페이스를 사용해 id와 password를 서버로 request
    // 이후 response 받은 값을 확인하여 로그인 처리
    public void loginMethod() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        keyboardDown();
        id_edit.setError(null);
        pw_edit.setError(null);
        id = id_edit.getText().toString();
        pw = pw_edit.getText().toString();
        getSharedPreferences("login", MODE_PRIVATE).edit().putString("status", check_btn.isChecked() ? CHECK : save_btn.isChecked() ? SAVE : null).apply();
        getSharedPreferences("login", MODE_PRIVATE).edit().putString("id", check_btn.isChecked() ? id : save_btn.isChecked() ? id : null).apply();
        getSharedPreferences("login", MODE_PRIVATE).edit().putString("pw", check_btn.isChecked() ? pw : null).apply();
        if (TextUtils.isEmpty(id)) {
            id_edit.setError(getString(R.string.error_field_required));
            id_edit.requestFocus();
        } else if (TextUtils.isEmpty(pw)) {
            pw_edit.setError(getString(R.string.error_field_required));
            pw_edit.requestFocus();
        } else if (activeNetwork == null) {
            mToast.setText(getString(R.string.internet_error));
            mToast.show();
        } else {
            Task task = new Task(context);
            task.onPreExecute();
            Call<Member> loginCall = new Retrofit.Builder()
                    .baseUrl(BASE + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PostLogin.class)
                    .login(id, pw);
            loginCall.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Member repo = response.body();
                    String beforeToken = repo.getP_token();
                    String afterToken = getSharedPreferences("token", MODE_PRIVATE).getString("token", null);
                    if (repo.getV_num() == -1) {
                        task.onPostExecute(null);
                        mToast.setText(getString(R.string.login_error));
                        mToast.show();
                    } else if (!beforeToken.equals(afterToken)) {
                        task.onPostExecute(null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
                            dialog.setMessage("기존 디바이스와 연결을 끊고\n새로운 디바이스와 계정을 연동하시겠습니까?").setCancelable(
                                    false).setPositiveButton("네",
                                    (dialog1, btnId) -> {
                                        dialog1.dismiss();
                                        task.onPreExecute();
                                        // 로그인에 성공했으나 가져온 토큰의 값이 저장된 토큰의 값과 다를경우
                                        // (처음 로그인을 했을 경우 or 클라이언트의 토큰 값이 변경되었을 경우)
                                        // response 받은 v_num과 저장되어있던 p_token을 request로 update 이후
                                        // 다음 액티비티로 전달하고 실행
                                        Call<Void> tokenCall = new Retrofit.Builder()
                                                .baseUrl(BASE + "/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build()
                                                .create(PostToken.class)
                                                .token(id, pw, beforeToken, afterToken);
                                        tokenCall.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                task.onPostExecute(null);
                                                Intent intent = new Intent(context, VideoActivity.class);
                                                intent.putExtra("v_num", repo.getV_num());
                                                intent.putExtra("p_token", afterToken);
                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                Log.e(TAG, String.valueOf(t));
                                            }
                                        });
                                    }).setNegativeButton("아니오",
                                    (dialog12, id1) -> dialog12.cancel()).show();
                    } else {
                        // 로그인에 성공하면 response 받은 v_num과 p_token을 다음 액티비티로 전달하고 실행
                        task.onPostExecute(null);
                        Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra("v_num", repo.getV_num());
                        intent.putExtra("p_token", beforeToken);
                        startActivity(intent);
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Log.e(TAG, String.valueOf(t));
                }
            });
        }
    }
}