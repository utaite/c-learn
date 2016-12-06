package com.yuyu.clearn.api;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioWriterPCM {

    private final static String TAG = AudioWriterPCM.class.getSimpleName();

    private String path, filename;
    private FileOutputStream speechFile;

    public AudioWriterPCM(String path) {
        this.path = path;
    }

    public void open(String sessionId) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        filename = directory + "/" + sessionId + ".pcm";
        try {
            speechFile = new FileOutputStream(new File(filename));
        } catch (FileNotFoundException e) {
            Log.e(TAG, String.valueOf(e));
            speechFile = null;
        }
    }

    public void close() {
        if (speechFile == null)
            return;
        try {
            speechFile.close();
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public void write(short[] data) {
        if (speechFile == null)
            return;
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < data.length; i++) {
            buffer.putShort(data[i]);
        }
        buffer.flip();
        try {
            speechFile.write(buffer.array());
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }
}
