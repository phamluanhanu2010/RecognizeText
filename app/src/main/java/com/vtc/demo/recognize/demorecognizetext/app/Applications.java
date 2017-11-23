package com.vtc.demo.recognize.demorecognizetext.app;

import android.app.Application;

import com.vtc.demo.recognize.demorecognizetext.Logger;

/**
 * Created by hahoang on 10/4/17.
 *
 * @author Mr. Ha
 */

public class Applications extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace();
        Logger.Log("-----------------handleUncaughtException--------------------- " + e.getMessage());
    }
}
