package com.vtc.demo.recognize.demorecognizetext.model;

import android.content.Context;
import android.database.Cursor;

import com.vtc.demo.recognize.demorecognizetext.Logger;
import com.vtc.demo.recognize.demorecognizetext.core.MBDBBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hahoang on 10/6/17.
 *
 * @author Mr. Ha
 */

public class VtcModelCard {

    private int id = 0;
    private String card_serial = "";
    private String card_number = "";
    private String card_price = "";
    private String card_Expires = "";

    public List<VtcModelCard> getDataChatCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.getCount() > 0) {
                List<VtcModelCard> cards = new ArrayList<>();

                while (cursor.moveToNext()) {
                    VtcModelCard modelCard = new VtcModelCard();

                    modelCard.setId(cursor.getInt(cursor.getColumnIndex(MBDBBase.Card_id)));
                    modelCard.setCard_serial(cursor.getString(cursor.getColumnIndex(MBDBBase.Card_Serial)));
                    modelCard.setCard_number(cursor.getString(cursor.getColumnIndex(MBDBBase.Card_Number)));
                    modelCard.setCard_price(cursor.getString(cursor.getColumnIndex(MBDBBase.Card_Price)));
                    modelCard.setCard_Expires(cursor.getString(cursor.getColumnIndex(MBDBBase.Card_Expires)));

//                    UtilsExcel.saveExcelFile(UtilsExcel.countRow(), modelCard.getCard_serial(), modelCard.getCard_number(), modelCard.getCard_price(), modelCard.getCard_Expires());

                    cards.add(modelCard);
                }

//                UtilsExcel.initWrite(context);

                return cards;

            }
        } catch (Exception e) {
            Logger.Log(" getDataChatCursor Exception " + e.getMessage());
        } finally {
            cursor.close();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCard_serial() {
        return card_serial;
    }

    public void setCard_serial(String card_serial) {
        this.card_serial = card_serial;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_price() {
        return card_price;
    }

    public void setCard_price(String card_price) {
        this.card_price = card_price;
    }

    public String getCard_Expires() {
        return card_Expires;
    }

    public void setCard_Expires(String card_Expires) {
        this.card_Expires = card_Expires;
    }
}
