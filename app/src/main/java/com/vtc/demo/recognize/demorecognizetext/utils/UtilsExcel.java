package com.vtc.demo.recognize.demorecognizetext.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.vtc.demo.recognize.demorecognizetext.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by hahoang on 10/5/17.
 *
 * @author Mr. Ha
 */

public class UtilsExcel {

    private static final String FILE_NAME = "ListCard.xls";
    public static String PATH_FULL = "";

    private static HSSFWorkbook wb;
    private static HSSFSheet mySheet;

    private static int countCell = 0;

    public static void initDataFileExcel(Context context) {

        countCell = readCountExcelFile(context);

        if (countCell <= 0) {
            /* Chưa có bản khi nào trong File Excel thì khởi tạo*/
            if (wb == null) {
                wb = new HSSFWorkbook();
            }
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            mySheet = wb.createSheet("Danh sách thẻ");

            UtilsExcel.saveExcelFile(countCell, "Số Serial", "Số thẻ", "Mệnh giá", "Ngày hết hạn");

            initWrite(context);
        } else {

            /* Số lượng bản ghi -1 = Index row*/
            countCell -= 1;
        }
    }

    public static int countRow() {
        return countCell += 1;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean saveExcelFile(int indexRow, String serialNum, String cardNum, String price, String date) {

        if (wb == null || mySheet == null) {
            return false;
        }

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        if (indexRow == 0) {
            cs.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        } else {
            if (indexRow % 2 == 0) {
                cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            } else {
                cs.setFillForegroundColor(HSSFColor.WHITE.index);
            }
        }

        Logger.Log("-----------getNumberOfSheets------ : " + wb.getNumberOfSheets() + " ------------ : " + indexRow);

        // Generate column headings
        Row row = mySheet.createRow(indexRow);

        Cell c = row.createCell(0);
        c.setCellValue(serialNum);
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue(cardNum);
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue(price);
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue(date);
        c.setCellStyle(cs);

        mySheet.setColumnWidth(0, (15 * 500));
        mySheet.setColumnWidth(1, (15 * 500));
        mySheet.setColumnWidth(2, (15 * 500));
        mySheet.setColumnWidth(3, (15 * 500));

        return true;
    }

    public static boolean initWrite(Context context) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Logger.Log("Storage not available or read only");
            return false;
        }

        boolean success = false;

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), FILE_NAME);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);

            PATH_FULL = file.getAbsolutePath();

            Logger.Log("Writing file : " + PATH_FULL);
            success = true;
        } catch (IOException e) {
            Logger.Log(e.getMessage());
        } catch (Exception e) {
            Logger.Log(e.getMessage());
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ignored) {
            }
        }
        return success;
    }


//    public static boolean saveExcelFile(Context context, int indexRow, String serialNum, String cardNum, String price, String date) {
//
//        // check if available and not read only
//        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Logger.Log("Storage not available or read only");
//            return false;
//        }
//
//        boolean success = false;
//
//        //Cell style for header row
//        CellStyle cs = wb.createCellStyle();
//        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//
//        if (indexRow == 0) {
//            cs.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
//        } else {
//            if (indexRow % 2 == 0) {
//                cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
//            } else {
//                cs.setFillForegroundColor(HSSFColor.WHITE.index);
//            }
//        }
//
//        Logger.Log("-----------getNumberOfSheets------ : " + wb.getNumberOfSheets() + " ------------ : " + indexRow);
//
//        // Generate column headings
//        Row row = mySheet.createRow(indexRow);
//
//        Cell c = row.createCell(0);
//        c.setCellValue(serialNum);
//        c.setCellStyle(cs);
//
//        c = row.createCell(1);
//        c.setCellValue(cardNum);
//        c.setCellStyle(cs);
//
//        c = row.createCell(2);
//        c.setCellValue(price);
//        c.setCellStyle(cs);
//
//        c = row.createCell(3);
//        c.setCellValue(date);
//        c.setCellStyle(cs);
//
//        mySheet.setColumnWidth(0, (15 * 500));
//        mySheet.setColumnWidth(1, (15 * 500));
//        mySheet.setColumnWidth(2, (15 * 500));
//        mySheet.setColumnWidth(3, (15 * 500));
//
//        // Create a path where we will place our List of objects on external storage
//        File file = new File(context.getExternalFilesDir(null), FILE_NAME);
//        FileOutputStream os = null;
//
//        try {
//            os = new FileOutputStream(file);
//            wb.write(os);
//
//            PATH_FULL = file.getAbsolutePath();
//
//            Logger.Log("Writing file : " + PATH_FULL);
//            success = true;
//        } catch (IOException e) {
//            Logger.Log(e.getMessage());
//        } catch (Exception e) {
//            Logger.Log(e.getMessage());
//        } finally {
//            try {
//                if (null != os)
//                    os.close();
//            } catch (Exception ignored) {
//            }
//        }
//        return success;
//    }


    public static void readExcelFile(Context context) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Logger.Log("Storage not available or read only");
            return;
        }

        try {
            // Creating Input Stream 
            File file = new File(context.getExternalFilesDir(null), FILE_NAME);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object 
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System 
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook 
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();

                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Logger.Log("Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int readCountExcelFile(Context context) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Logger.Log("Storage not available or read only");
            return 0;
        }

        int count = 0;

        try {
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), FILE_NAME);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            wb = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            mySheet = wb.getSheetAt(0);

            for (Row r : mySheet) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

}
