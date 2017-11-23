/*
 * Created by Hadvlop@gmail.com on 11/1/16 1:13 PM
 * Copyright © 2016, All Rights Reserved.
 *
 * Last modified 10/12/16 11:25 AM
 */

package com.vtc.demo.recognize.demorecognizetext.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.vtc.demo.recognize.demorecognizetext.model.VtcModelCard;

import java.util.List;

/**
 * Created by Mr. Ha on 25/05/2016.
 *
 * @author Mr. Ha
 */

public class MBDBProcessConnect extends MBDBBase {

    private final String TAG = this.getClass().getName();

    private Context context;

    public MBDBProcessConnect(Context context) {
        super(context);
        this.context = context;
    }

    public boolean initInsertCardFromDB(VtcModelCard vtcModelCard) {

        /**
         * Check Card
         *
         * if exist initInsertCardFromDB(vtcModelCard), else false;
         *
         * */

        if (getCheckCardFromDB(vtcModelCard))
            return setCardItemIntoDB(vtcModelCard);
        else
            return false;
    }

    /**
     * Insert Card
     * <p>
     * return true == success, return false == not success
     */
    private boolean setCardItemIntoDB(VtcModelCard vtcModelCard) {

        ContentValues insertCard = new ContentValues();
        insertCard.put(MBDBBase.Card_Serial, vtcModelCard.getCard_serial());
        insertCard.put(MBDBBase.Card_Number, vtcModelCard.getCard_number());
        insertCard.put(MBDBBase.Card_Price, vtcModelCard.getCard_price());
        insertCard.put(MBDBBase.Card_Expires, vtcModelCard.getCard_Expires());

        return OpenDB().insert(MBDBBase.DATABASE_TABLE_CARD, null, insertCard) > 0;
    }

    /**
     * Check Card
     * <p>
     * if exist return VtcModelCard list, else return null
     */
    private boolean getCheckCardFromDB(VtcModelCard vtcModelCard) {

        String QUERY_GET_CARD = "SELECT " + MBDBBase.Card_id + " FROM " + MBDBBase.DATABASE_TABLE_CARD + " WHERE " + MBDBBase.Card_Serial + " = ? OR " + MBDBBase.Card_Number + " = ? LIMIT 1";

        Cursor cursor = OpenDB().rawQuery(QUERY_GET_CARD, new String[]{vtcModelCard.getCard_serial(), vtcModelCard.getCard_number()});

        if (cursor == null) {
            return Boolean.TRUE;
        }

        boolean isOK;

        if (cursor.getCount() > 0) {
            isOK = Boolean.FALSE;
        } else {
            isOK = Boolean.TRUE;
        }

        cursor.close();

        return isOK;
    }

    /**
     * Get All Card
     * <p>
     * if exist return VtcModelCard list, else return null
     */

    public List<VtcModelCard> getAllCardFromDB() {

        String QUERY_GET_CARD = "SELECT * FROM " + MBDBBase.DATABASE_TABLE_CARD;

        Cursor cursor = OpenDB().rawQuery(QUERY_GET_CARD, new String[]{});

        List<VtcModelCard> cardList = new VtcModelCard().getDataChatCursor(cursor);

        cursor.close();

        return cardList;
    }

    public boolean deleteTable() {

        return OpenDB().delete(DATABASE_TABLE_CARD, "", new String[]{}) > 0;
    }

}
