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

public class RestInterface {

    public static String BASE = "http://192.168.219.103/CLearn/", RESOURCES = "resources/";

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
    // id와 password를 서버에 request 이후 일치하는 계정이 있다면 p_token과 v_num을 response 받음
    public interface PostLogin {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Member> login(
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
    // 일치하는 데이터의 여러 정보를 response 받음
    public interface PostVideo {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Member> video(
                @Path("what") String what,
                @Field("v_num") int v_num,
                @Field("p_token") String p_token);
    }

    // 동영상 시청이 끝났을 경우 v_num을 request하여
    // 해당 v_num을 가진 동영상의 v_finish를 Y로 update함
    public interface PostFinish {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> finish(
                @Path("what") String what,
                @Field("v_num") int v_num);
    }

    // 동영상 시청이 중단되었을 경우 v_num과 v_ctime을 request하여
    // 해당 v_num을 가진 동영상의 v_ctime을 update함
    public interface PostSave {
        @FormUrlEncoded
        @POST("api/{what}")
        Observable<Void> save(
                @Path("what") String what,
                @Field("v_num") int v_num,
                @Field("v_ctime") long v_ctime,
                @Field("result") String result);
    }

}
