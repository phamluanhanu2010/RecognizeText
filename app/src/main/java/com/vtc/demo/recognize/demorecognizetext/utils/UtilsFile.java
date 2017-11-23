package com.vtc.demo.recognize.demorecognizetext.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.vtc.demo.recognize.demorecognizetext.Logger;
import com.vtc.demo.recognize.demorecognizetext.R;
import com.vtc.demo.recognize.demorecognizetext.model.VtcModelCard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by hahoang on 10/9/17.
 *
 * @author Mr. Ha
 */

public class UtilsFile {

    private static final String FILE_NAME = "ListCard.txt";
    public static String PATH_FULL = "";

    public static boolean initWrite(Context context, List<VtcModelCard> cardList) {

        // check if available and not read only
        if (!UtilsExcel.isExternalStorageAvailable() || UtilsExcel.isExternalStorageReadOnly() || cardList == null || cardList.size() <= 0) {
            Logger.Log("Storage not available or read only");
            return false;
        }

        PATH_FULL = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(PATH_FULL, getAlbumName(context));
        if (!file.exists()) {
            file.mkdirs();
        }

        PATH_FULL = file.getAbsolutePath();

        file = new File(PATH_FULL, FILE_NAME);

        PATH_FULL = file.getAbsolutePath();

        try {

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for (VtcModelCard modelCard : cardList) {
                if (modelCard != null) {
                    String str = modelCard.getCard_serial() + modelCard.getCard_number() + modelCard.getCard_price() + modelCard.getCard_Expires();
                    bw.write(str);
                    bw.newLine();
                }
            }

            bw.flush();
            bw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private static String getAlbumName(Context context) {
        return context.getResources().getString(R.string.app_name);
    }
}
