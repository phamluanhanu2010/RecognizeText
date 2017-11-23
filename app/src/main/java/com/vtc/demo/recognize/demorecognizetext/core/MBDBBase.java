/*
 * Created by Hadvlop@gmail.com on 11/1/16 1:13 PM
 * Copyright © 2016, All Rights Reserved.
 *
 * Last modified 10/11/16 10:11 AM
 */

package com.vtc.demo.recognize.demorecognizetext.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.vtc.demo.recognize.demorecognizetext.BuildConfig;
import com.vtc.demo.recognize.demorecognizetext.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mr. Ha on 25/05/2016.
 *
 * @author Mr. Ha
 */

public class MBDBBase {

    private static final String TAG = "MBDBBase";

    private Context mcontext;

    public static final String DATABASE_NAME = "card.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase mDatabase = null;
    private static DatabaseHelper myDataHelPer = null;

    // 1-----------------------Table Chats-------------------//

    public static final String Card_id = "id";
    public static final String Card_Serial = "card_serial";
    public static final String Card_Number = "card_num";
    public static final String Card_Price = "card_price";
    public static final String Card_Expires = "card_expires";

    public static final String DATABASE_TABLE_CARD = "chats";

    private static final String DATABASE_CREATE_TABLE_CARD = "CREATE TABLE ["
            + DATABASE_TABLE_CARD + "](["
            + Card_id + "] INTEGER PRIMARY KEY AUTOINCREMENT, ["
            + Card_Serial + "] TEXT ,["
            + Card_Number + "] INTEGER ,["
            + Card_Price + "] TEXT ,["
            + Card_Expires + "] TEXT );";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context mContext;

//		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
//			super(context, name, factory, version);
//			mContext = context;
//		}

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TABLE_CARD);

            databaseManager.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CARD);

            onCreate(db);

            databaseManager.onUpgrade(db, oldVersion, newVersion);
        }

        MBDBBase databaseManager;
        private AtomicInteger counter = new AtomicInteger(0);

        public DatabaseHelper(Context context, String name, int version, MBDBBase databaseManager) {
            super(context, name, null, version);
            this.databaseManager = databaseManager;
            mContext = context;
        }

        public void addConnection() {
            counter.incrementAndGet();
        }

        public void removeConnection() {
            counter.decrementAndGet();
        }

        public int getCounter() {
            return counter.get();
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            databaseManager.onOpen(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            databaseManager.onDowngrade(db, oldVersion, newVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            databaseManager.onConfigure(db);
        }
    }


    /**
     * See SQLiteOpenHelper documentation
     */
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * See SQLiteOpenHelper documentation
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Optional.
     * *
     */
    public void onOpen(SQLiteDatabase db) {
    }

    /**
     * Optional.
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Optional
     */
    public void onConfigure(SQLiteDatabase db) {
    }

    public synchronized SQLiteDatabase OpenDB() {
        synchronized (this) {
            if (!isOpen()) {
                myDataHelPer.addConnection();
                if (mDatabase == null || !mDatabase.isOpen()) {
                    synchronized (lockObject) {
                        mDatabase = myDataHelPer.getWritableDatabase();
                    }
                }
            }
            if (BuildConfig.DEBUG) {
//                exportDB();
            }
            return getMyDatabase();
        }
    }

    private final ConcurrentHashMap<String, DatabaseHelper> dbMap = new ConcurrentHashMap<String, DatabaseHelper>();

    private final Object lockObject = new Object();

    public MBDBBase(Context context) {

//		String dbPath = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getPath();
        synchronized (lockObject) {
            myDataHelPer = dbMap.get(DATABASE_NAME);

            if (myDataHelPer == null) {

                myDataHelPer = new DatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION, this);

                dbMap.put(DATABASE_NAME, myDataHelPer);
            }

            if (!isOpen()) {
                mDatabase = myDataHelPer.getWritableDatabase();
            }

            mcontext = context.getApplicationContext();
        }
    }

    public SQLiteDatabase getMyDatabase() {
        return mDatabase;
    }

    public boolean isOpen() {
        return (mDatabase != null && mDatabase.isOpen());
    }

    public boolean close() {
        myDataHelPer.removeConnection();

        if (myDataHelPer.getCounter() == 0) {

            synchronized (lockObject) {

                if (mDatabase.inTransaction()) mDatabase.endTransaction();

                if (mDatabase.isOpen()) mDatabase.close();

                mDatabase = null;

            }

            return true;
        } else {
            dbMap.remove(DATABASE_NAME);
        }
        return false;
    }

    @SuppressWarnings("serial")
    public static class SQLiteAssetException extends SQLiteException {

        public SQLiteAssetException() {
        }

        public SQLiteAssetException(String error) {
            super(error);
        }
    }

    public void exportDB() {
        String currentDBPath = "/data/" + BuildConfig.APPLICATION_ID + "/databases/" + DATABASE_NAME;
        String backupDBPath = File.separator + "MTIM" + File.separator + "logs" + File.separator + DATABASE_NAME;

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;

        File currentDB = new File(data, currentDBPath);

        File backupDB = new File(sd, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Logger.Log("DB Exported!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}