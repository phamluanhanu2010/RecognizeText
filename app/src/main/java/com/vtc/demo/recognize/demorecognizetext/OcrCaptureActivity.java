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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.vtc.demo.recognize.demorecognizetext.core.MBDBBase;
import com.vtc.demo.recognize.demorecognizetext.core.MBDBProcessConnect;
import com.vtc.demo.recognize.demorecognizetext.i.CallBack;
import com.vtc.demo.recognize.demorecognizetext.model.VtcModelCard;
import com.vtc.demo.recognize.demorecognizetext.ui.camera.CameraSource;
import com.vtc.demo.recognize.demorecognizetext.ui.camera.CameraSourcePreview;
import com.vtc.demo.recognize.demorecognizetext.ui.camera.GraphicOverlay;
import com.vtc.demo.recognize.demorecognizetext.utils.PreferenceUtil;
import com.vtc.demo.recognize.demorecognizetext.utils.Utils;
import com.vtc.demo.recognize.demorecognizetext.utils.UtilsExcel;
import com.vtc.demo.recognize.demorecognizetext.utils.UtilsFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity {

    private Activity activity;

    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;


    private TextView tv_serial_num;
    private TextView tv_card_num;
    private TextView tv_date;
    private TextView tv_money;

    private TextView tv_date_export;


    private static String serialNum = "";
    private static String cardNum = "";
    private static String moneyNum = "";
    private static String dateNum = "";

    private MBDBProcessConnect mbdbProcessConnect;

    private VtcModelCard modelCard;

    private CheckBox btn_turn_on_off;

    private boolean isOn = Boolean.TRUE;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ocr_capture);

        activity = OcrCaptureActivity.this;

        mbdbProcessConnect = new MBDBProcessConnect(activity);
        modelCard = new VtcModelCard();

        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        Button btn_retry = (Button) findViewById(R.id.btn_retry);
        Button btn_focus = (Button) findViewById(R.id.btn_focus);

        tv_serial_num = (TextView) findViewById(R.id.tv_serial_num);
        tv_card_num = (TextView) findViewById(R.id.tv_card_num);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_date_export = (TextView) findViewById(R.id.tv_date_export);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);

        dateExport();

        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        Snackbar.make(mGraphicOverlay, "Tap to Speak. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();

        // Set up the Text To Speech engine.
        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Logger.Log("OnInitListener Text to speech engine started successfully.");
//                            tts.setLanguage(Locale.US);
                            tts.setLanguage(new Locale("vi", "VN"));
                        } else {
                            Logger.Log("OnInitListener Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearData();

                startCameraSource();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearData();

                if (mbdbProcessConnect == null) {
                    mbdbProcessConnect = new MBDBProcessConnect(activity);
                }

                if (modelCard == null) {
                    modelCard = new VtcModelCard();
                }

                if (!mbdbProcessConnect.initInsertCardFromDB(modelCard)) {
                    Toast.makeText(activity, "Thẻ có thể bị quét lỗi hoặc đã tồn tại trong danh sách.\nYêu cầu kiểm tra lại!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Lưu thẻ thành công", Toast.LENGTH_LONG).show();
                }

                modelCard = null;

                startCameraSource();
            }
        });

        btn_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCameraSource.autoFocus(new CameraSource.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success) {
////                        Logger.Log("-------------success--------------------- : " + success);
//                    }
//                });
//                mCameraSource.setAutoFocusMoveCallback(new CameraSource.AutoFocusMoveCallback() {
//                    @Override
//                    public void onAutoFocusMoving(final boolean start) {
//
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                if(start){
////                                    tv_focusing.setText("OK");
////                                }else {
////                                    tv_focusing.setText("Focusing......");
////                                }
////                            }
////                        });
////                        Logger.Log("-------------start--------------------- : " + start);
//                    }
//                });
//
//                mCameraSource.requestFocus();


//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/*");
//                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(UtilsExcel.PATH_FULL)));
//                startActivity(Intent.createChooser(sharingIntent, "Share File"));

//                UtilsExcel.saveExcelFile(activity, UtilsExcel.countRow(), serialNum, cardNum, moneyNum, dateNum);


                if (mCameraSource != null) {
                    mCameraSource.stop();
                }

                initShowMessageAlert();
            }
        });


        btn_turn_on_off = (CheckBox) findViewById(R.id.btn_turn_on_off);

        btn_turn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OcrCaptureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFlashLightOnOff(isOn);
                        }
                    });
                } catch (Exception ignored) {

                }
            }
        });
    }

    private void dateExport() {

        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tv_date_export != null) {
                        long time = new PreferenceUtil(activity).getValueLong("date");
                        if (time > 0) {
                            tv_date_export.setText(String.valueOf("Export lần cuối:\n" + Utils.getFomatTime(time)));
                        } else {
                            tv_date_export.setText("");
                        }
                    }
                }
            });
        }
    }

    private void onFlashLightOnOff(final boolean isStatus) {

        Logger.Log("-----------onFlashLightOnOff--------- : " + isStatus);

        if (isFlashSupported(OcrCaptureActivity.this)) {

            try {
                OcrCaptureActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (mCameraSource != null) {
                                if (isStatus) {
                                    Camera.Parameters p = mCameraSource.getmCamera().getParameters();
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    mCameraSource.getmCamera().setParameters(p);
                                    isOn = Boolean.FALSE;
                                    btn_turn_on_off.setChecked(Boolean.TRUE);
                                } else {
                                    Camera.Parameters p = mCameraSource.getmCamera().getParameters();
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    mCameraSource.getmCamera().setParameters(p);
                                    isOn = Boolean.TRUE;
                                    btn_turn_on_off.setChecked(Boolean.FALSE);
                                }
                            }
                        } catch (RuntimeException ignored) {

                        }
                    }
                });
            } catch (Exception ignored) {

            }
        } else {
            Toast.makeText(OcrCaptureActivity.this, "Không hỗ trợ đèn Flash", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * @param activity
     * @return true <b>if the device support camera flash</b><br/>
     * false <b>if the device doesn't support camera flash</b>
     */
    private boolean isFlashSupported(Activity activity) {

        PackageManager packageManager = activity.getPackageManager();
        // if device support camera flash?
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        }
        return false;
    }


    private void clearData() {
        serialNum = "";
        cardNum = "";
        moneyNum = "";
        dateNum = "";

        tv_serial_num.setText(String.valueOf("Serial: "));
        tv_card_num.setText(String.valueOf("Card: "));
        tv_date.setText(String.valueOf("Ngày hết hạn: "));
        tv_money.setText(String.valueOf("Số tiền: "));
    }

    private void initShowMessageAlert() {

        new AlertDialog.Builder(activity)
                .setTitle("Xác nhận")
                .setMessage("Sau khi xuất file dữ liệu được lưu xẽ bị xóa.")
                .setCancelable(false)
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new InitDataFromDB().execute();
                    }
                }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startCameraSource();
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private ProgressDialog progress;

    private Handler mHandler;

    /**
     * <d>Call when request Api server</d>
     * <d>show dialog process</d>
     */
    private void initShowDialogProcess() {

        if (mHandler != null) {
            mHandler = null;
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if (progress != null) {

                    if (progress.isShowing()) {
                        progress.cancel();
                    }
                    progress = null;
                }
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress = ProgressDialog.show(activity, "Thông báo", "Đang xử lý...", true);

                            mHandler = null;
                        }
                    });
                }
            }
        };

        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.sendToTarget();
        }
    }

    /**
     * <d>Call when request Api server</d>
     * <d>Dismiss dialog process</d>
     */
    private void initCloseDialogProcess() {

        if (progress != null && progress.isShowing()) {

            progress.dismiss();
            progress = null;
        }
    }

    private class InitDataFromDB extends AsyncTask<String, String, List<VtcModelCard>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initShowDialogProcess();
        }

        @Override
        protected List<VtcModelCard> doInBackground(String... params) {

//            UtilsExcel.initDataFileExcel(activity);

            if (mbdbProcessConnect == null) {
                mbdbProcessConnect = new MBDBProcessConnect(activity);
            }

            return mbdbProcessConnect.getAllCardFromDB();
        }

        @Override
        protected void onPostExecute(final List<VtcModelCard> result) {

            initCloseDialogProcess();

            if (result != null) {
                if (UtilsFile.initWrite(activity, result)) {

                    clearData();

                    if (mbdbProcessConnect != null) {
                        mbdbProcessConnect.deleteTable();
                    }



//                    activity.deleteDatabase(MBDBBase.DATABASE_NAME);

//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/*");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(UtilsFile.PATH_FULL)));
//                    startActivity(Intent.createChooser(sharingIntent, "Share File"));


//                    Toast.makeText(activity, "File Export được lưu trong đường dẫn: " + UtilsFile.PATH_FULL, Toast.LENGTH_LONG).show();

                    new PreferenceUtil(activity).setValueLong("date", System.currentTimeMillis());

                    dateExport();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Thông báo")
                            .setCancelable(false)
                            .setMessage("File Export được lưu trong đường dẫn: " + UtilsFile.PATH_FULL)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    startCameraSource();
                                }
                            })
                            .show();
                }
            } else {
                Toast.makeText(activity, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                startCameraSource();
            }
            super.onPostExecute(result);
        }
    }


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Logger.Log("Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     * <p/>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        OcrDetectorProcessor ocrDetectorProcessor = new OcrDetectorProcessor(mGraphicOverlay, new CallBack() {

            @Override
            public void handlerCallBack(final String serial, final String card, final String money, final String date, final String serialDisplay, final String cardDisplay, String moneyDisplay, String dateDisplay) {
                if (mCameraSource != null) {

                    mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data) {

                            try {
                                if (activity == null || activity.isFinishing()) {
                                    return;
                                }
                                if (data != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (modelCard == null) {
                                                modelCard = new VtcModelCard();
                                            }

                                            if (card != null && !card.isEmpty()) {
                                                cardNum = card;
                                                modelCard.setCard_number(cardNum);
                                            }

                                            if (serial != null && !serial.isEmpty()) {
                                                serialNum = serial;
                                                modelCard.setCard_serial(serialNum);
                                            }

                                            if (money != null && !money.isEmpty()) {
                                                moneyNum = money;
                                                modelCard.setCard_price(moneyNum);
                                            }

                                            if (date != null && !date.isEmpty()) {
                                                dateNum = date;
                                                modelCard.setCard_Expires(dateNum);
                                            }

                                            if (serialNum != null && !serialNum.isEmpty() && serialDisplay != null && !serialDisplay.isEmpty()) {
                                                tv_serial_num.setText(String.valueOf("Serial: " + serialDisplay));
                                            }

                                            if (cardNum != null && !cardNum.isEmpty() && cardDisplay != null && !cardDisplay.isEmpty()) {
                                                tv_card_num.setText(String.valueOf("Card: " + cardDisplay));
                                            }

                                            if (dateNum != null && !dateNum.isEmpty()) {
                                                tv_date.setText(String.valueOf("Ngày hết hạn: " + dateNum));
                                            }

                                            if (moneyNum != null && !moneyNum.isEmpty()) {
                                                tv_money.setText(String.valueOf("Số tiền: " + moneyNum));
                                            }

                                            if (mCameraSource != null) {
                                                mCameraSource.stop();
                                            }
                                        }
                                    });
                                }
                            } catch (NumberFormatException ignored) {

                            }
                        }
                    });
                }
            }

            @Override
            public void handlerCallBackRealTime(final String serial, final String card, final String money, final String date, final String serialDisplay, final String cardDisplay, String moneyDisplay, String dateDisplay) {
                Logger.Log("----------- : " + serial + " ------- : " + card + " ------- : " + money + " ------- : " + date);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (card != null && !card.isEmpty()) {
                            cardNum = card;
                        }

                        if (serial != null && !serial.isEmpty()) {
                            serialNum = serial;
                        }

                        if (money != null && !money.isEmpty()) {
                            moneyNum = money;
                        }

                        if (date != null && !date.isEmpty()) {
                            dateNum = date;
                        }

                        if (serialNum != null && !serialNum.isEmpty() && serialDisplay != null && !serialDisplay.isEmpty()) {
                            tv_serial_num.setText(String.valueOf("Serial: " + serialDisplay));
                        }

                        if (cardNum != null && !cardNum.isEmpty() && cardDisplay != null && !cardDisplay.isEmpty()) {
                            tv_card_num.setText(String.valueOf("Card: " + cardDisplay));
                        }

                        if (dateNum != null && !dateNum.isEmpty()) {
                            tv_date.setText(String.valueOf("Ngày hết hạn: " + dateNum));
                        }

                        if (moneyNum != null && !moneyNum.isEmpty()) {
                            tv_money.setText(String.valueOf("Số tiền: " + moneyNum));
                        }
                    }
                });
            }
        });
        textRecognizer.setProcessor(ocrDetectorProcessor);

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Logger.Log("Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Logger.Log(getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1024, 768)
                        .setRequestedFps(30.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Logger.Log("Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Logger.Log("Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Logger.Log("Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                Logger.Log("------------------------Restart Cam");
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Logger.Log("Unable to start camera source." + e.getMessage());
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Logger.Log("text data is being spoken! " + text.getValue());
                // Speak the string.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(text.getValue(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                }
            } else {
                Logger.Log("text data is null");
            }
        } else {
            Logger.Log("no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }
}
