package com.vtc.demo.recognize.demorecognizetext;

import android.util.Log;

/**
 * Created by Mr Ha on 10/17/16.
 *
 * @author Mr. Ha
 */
public class Logger {

    public static void Log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i("Ha : ", "------ : " + msg);
        }
    }
}
