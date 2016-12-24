package com.yuyu.clearn.api.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

// Restful 통신에 필요한 데이터, 객체, 인터페이스 등의 자원을 모으기 위해 구현한 클래스
public class RestInterface {

    public static String BASE = "http://192.168.1.15/CLearn/", RESOURCES = "resources/", IMAGE = "image/", VIDEO = "video/";
    public static String REGISTER_URL = "member", FIND_ID = "idsearch", FIND_PW = "pwsearch", LOGIN_LOGO_IMG = "login_logo.png";
    public static String MAIN_SCREEN = "Main_Screen.mp4", VIDEO_SCREEN_KOREAN = "Video_Screen_Korean.mp4", VIDEO_SCREEN_MATH = "Video_Screen_Math.mp4";
    public static String QUIZ_ = "Quiz_", MP4 = ".mp4", YES = "Yes.mp4", NO = "No.mp4";

    private static Retrofit retrofit;

    // Retrofit 객체 생성 후 싱글톤으로 return
    public static void init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                .build();
    }

    public static Retrofit getRestClient() {
        return retrofit;
    }

    // FCM token을 사용한 JWT 형식의 로그인 연동
    // id와 password를 서버에 request 이후 일치하는 계정이 있다면 p_token과 v_num을 response
    public interface PostLogin {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<MemberVO> login(
                @Path("what") String what,
                @Field("p_id") String id,
                @Field("p_pw") String pw);
    }

    // 처음 로그인을 했을 경우 or 클라이언트의 토큰 값이 변경되었을 경우
    // 변경 전과 변경 후의 token을 전부 request
    public interface PostToken {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> token(
                @Path("what") String what,
                @Field("p_id") String id,
                @Field("p_pw") String pw,
                @Field("p_token") String afterToken,
                @Field("before_token") String beforeToken);
    }

    // 이전 로그인 액티비티에서 전달받은 값 v_num과 p_token을 서버에 request 이후
    // 일치하는 데이터의 여러 정보를 response
    public interface PostVideo {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<MemberVO> video(
                @Path("what") String what,
                @Field("v_num") int v_num,
                @Field("p_token") String p_token);
    }

    // 동영상 시청이 끝났을 경우 v_num을 request 이후
    // 해당 v_num을 가진 동영상의 v_finish를 Y로 update
    public interface PostFinish {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> finish(
                @Path("what") String what,
                @Field("v_num") int v_num);
    }

    // 동영상 시청이 중단되었을 경우 v_num과 v_ctime을 request 이후
    // 해당 v_num을 가진 동영상의 v_ctime을 update
    public interface PostSave {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> save(
                @Path("what") String what,
                @Field("v_num") int v_num,
                @Field("v_ctime") long v_ctime);
    }

    // 어플이 종료되었을 경우 p_token과 Realm DB의 모든 데이터를 request
    public interface PostConnectResult {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> connectResult(
                @Path("what") String what,
                @Field("p_token") String p_token,
                @Field("p_cresult") String result);
    }

    // 퀴즈가 종료되었을 경우 p_token과 Realm DB의 모든 데이터를 request
    public interface PostQuizResult {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> quizResult(
                @Path("what") String what,
                @Field("p_token") String p_token,
                @Field("p_qresult") String result);
    }

}
