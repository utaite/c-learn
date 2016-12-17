package com.yuyu.clearn.api.reognizer;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// 네이버 음성 인식 라이브러리로 이벤트를 처리하기 위해 구현한 클래스
public class AudioWriterPCM {

    private final String TAG = AudioWriterPCM.class.getSimpleName();

    private String path;
    private FileOutputStream speechFile;

    public AudioWriterPCM(String path) {
        this.path = path;
    }

    public void open(String sessionId) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filename = directory + "/" + sessionId + ".pcm";
        try {
            speechFile = new FileOutputStream(new File(filename));
        } catch (FileNotFoundException e) {
            Log.e(TAG, String.valueOf(e));
            speechFile = null;
        }
    }

    public void close() {
        if (speechFile == null) {
            return;
        }
        try {
            speechFile.close();
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public void write(short[] data) {
        if (speechFile == null) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (short aData : data) {
            buffer.putShort(aData);
        }
        buffer.flip();
        try {
            speechFile.write(buffer.array());
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }
}
