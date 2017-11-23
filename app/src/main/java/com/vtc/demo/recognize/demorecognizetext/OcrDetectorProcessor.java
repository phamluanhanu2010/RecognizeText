/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vtc.demo.recognize.demorecognizetext;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.vtc.demo.recognize.demorecognizetext.enums.DataTypeInput;
import com.vtc.demo.recognize.demorecognizetext.i.CallBack;
import com.vtc.demo.recognize.demorecognizetext.ui.camera.GraphicOverlay;
import com.vtc.demo.recognize.demorecognizetext.utils.Utils;


/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private CallBack callBack;

    private String serialNum = "";
    private String cardNum = "";
    private String money = "";
    private String dateNum = "";

    private String serialDisplay = "";
    private String cardDisplay = "";
//    private String moneyDisplay = "";
//    private String dateDisplay = "";

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, CallBack callBack) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.callBack = callBack;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {

                Logger.Log("OcrDetectorProcessor Text detected! " + item.getValue());

                String data = item.getValue().replace(" ", "");
                data = data.replace("-", "");

//                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
//                mGraphicOverlay.add(graphic);

                if (Utils.checkValidateSerialData(getDataTypeInput(), data)) {

                    serialNum = data;
                    serialDisplay = item.getValue();

                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);

//                    if (callBack != null) {
//                        callBack.handlerCallBackRealTime(serialNum, cardNum, money, dateNum);
//                    }
                }

                if (Utils.checkValidateCardData(getDataTypeInput(), data)) {

                    cardNum = data;
                    cardDisplay = item.getValue();

                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);

//                    if (callBack != null) {
//                        callBack.handlerCallBackRealTime("", cardNum);
//                    }
                }

//                if (Utils.validateCurrencyVND(data)) {
//
//                    money = data;
//                    moneyDisplay = item.getValue();
//
//                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
//                    mGraphicOverlay.add(graphic);
//
////                    if (callBack != null) {
////                        callBack.handlerCallBackRealTime("", cardNum);
////                    }
//                }
//
//                String strDate = Utils.validateDate(data);
//
//                if (!strDate.isEmpty()) {
//
//                    dateNum = strDate;
//                    dateDisplay = item.getValue();
//
//                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
//                    mGraphicOverlay.add(graphic);
//
////                    if (callBack != null) {
////                        callBack.handlerCallBackRealTime("", cardNum);
////                    }
//                }

                if (callBack != null) {
                    callBack.handlerCallBackRealTime(serialNum, cardNum, money, dateNum, serialNum, cardDisplay, "", "");
//                    callBack.handlerCallBackRealTime(serialNum, cardNum, money, dateNum, serialNum, cardDisplay, moneyDisplay, dateDisplay);
                }
            }
        }

        if (callBack != null && serialNum != null && !serialNum.isEmpty() && cardNum != null && !cardNum.isEmpty()) {
//        if (callBack != null && serialNum != null && !serialNum.isEmpty() && cardNum != null && !cardNum.isEmpty() && money != null && !money.isEmpty() && dateNum != null && !dateNum.isEmpty()) {

            callBack.handlerCallBack(serialNum, cardNum, money, dateNum, serialNum, cardDisplay, "", "");
//            callBack.handlerCallBack(serialNum, cardNum, money, dateNum, serialNum, cardDisplay, moneyDisplay, dateDisplay);
            serialNum = "";
            cardNum = "";
            money = "";
            dateNum = "";
            mGraphicOverlay.clear();
        }
    }

    public DataTypeInput getDataTypeInput() {
        return DataTypeInput.TYPE_INPUT_VIETTEL;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
