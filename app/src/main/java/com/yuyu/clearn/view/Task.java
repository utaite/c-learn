package com.yuyu.clearn.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.yuyu.clearn.R;

// AsyncTask를 사용하기 위해 구현한 클래스
public class Task extends AsyncTask<Void, Void, Void> {

    private String msg;
    private ProgressDialog asyncDialog;

    public Task(Context context) {
        asyncDialog = new ProgressDialog(context);
        msg = context.getString(R.string.task_0);
    }


    @Override
    public void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage(msg);
        asyncDialog.show();
        asyncDialog.setCancelable(false);
        asyncDialog.setCanceledOnTouchOutside(false);
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... arg0) {
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        asyncDialog.dismiss();
        super.onPostExecute(result);
    }
}