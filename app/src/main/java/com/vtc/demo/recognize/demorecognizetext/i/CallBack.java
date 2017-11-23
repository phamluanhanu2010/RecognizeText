package com.vtc.demo.recognize.demorecognizetext.i;

import com.google.android.gms.vision.text.TextBlock;

/**
 * Created by macbookpro on 10/17/16.
 */
public interface CallBack {
    void handlerCallBack(String serial, String card, String money, String date, String serialDisplay, String cardDisplay, String moneyDisplay, String dateDisplay);

    void handlerCallBackRealTime(String serial, String card, String money, String date, String serialDisplay, String cardDisplay, String moneyDisplay, String dateDisplay);
}
