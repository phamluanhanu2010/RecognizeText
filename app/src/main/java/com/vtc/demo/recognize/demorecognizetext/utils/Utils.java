package com.vtc.demo.recognize.demorecognizetext.utils;

import com.vtc.demo.recognize.demorecognizetext.Logger;
import com.vtc.demo.recognize.demorecognizetext.enums.DataTypeInput;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by macbookpro on 10/25/16.
 *
 * @author Mr. Ha
 */

public class Utils {

    /**
     * Check Type input data and validate Card
     *
     * @param dataTypeInput Type input data
     * @param card          data card
     */
    public static boolean checkValidateCardData(DataTypeInput dataTypeInput, String card) {

        if (card == null || card.isEmpty()) {
            return false;
        }

        switch (dataTypeInput) {
            case TYPE_INPUT_VIETTEL:

                if (validateNumberCardViettel(card)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VINA:

                if (validateNumberCardVina(card)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_MOBILE_PHONE:

                if (validateNumberCardMobiPhone(card)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VIETNAM_MOBILE:

                return false;

            default:
                return false;
        }
    }

    /**
     * Check Type input data and validate Serial
     *
     * @param dataTypeInput Type input data
     * @param serial        data card
     */
    public static boolean checkValidateSerialData(DataTypeInput dataTypeInput, String serial) {

        if (serial == null || serial.isEmpty()) {
            return false;
        }

        switch (dataTypeInput) {
            case TYPE_INPUT_VIETTEL:

                if (validateNumberSerialViettel(serial)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VINA:

                if (validateNumberSerialVina(serial)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_MOBILE_PHONE:

                if (validateNumberSerialMobiPhone(serial)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VIETNAM_MOBILE:

                return false;

            default:
                return false;
        }
    }


    public static String getFomatTime(long timeData) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale.getDefault());
        return df.format(timeData);
    }

    /**
     * Check Type input data and validate Card Viettel
     *
     * @param data data card
     *             <p>
     *             <p>validate number, length card (13) </p>
     */
    private static boolean validateNumberCardViettel(String data) {

        data = data.replace(" ", "");

        if (data.matches("^*[0-9]{13}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Serial Viettel
     *
     * @param data data serial
     *             <p>
     *             <p>validate number, length serial (11) </p>
     */
    private static boolean validateNumberSerialViettel(String data) {

        data = data.replace(" ", "");

        if (data.matches("^*[0-9]{11}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Card Mobi Phone
     *
     * @param data data card
     *             <p>
     *             <p>validate number, length card (12) </p>
     */
    private static boolean validateNumberCardVina(String data) {

        data = data.replace(" ", "");
        data = data.replace("-", "");

        if (data.matches("^*[0-9]{12}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Serial Mobi Phone
     *
     * @param data data serial
     *             <p>
     *             <p>validate number, length serial (15) </p>
     */
    private static boolean validateNumberSerialVina(String data) {

        data = data.replace(" ", "");
        data = data.replace("-", "");

        if (data.matches("^*[0-9]{15}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Card Mobi Phone
     *
     * @param data data card
     *             <p>
     *             <p>validate number, length card (12) </p>
     */
    private static boolean validateNumberCardMobiPhone(String data) {

        data = data.replace(" ", "");
        data = data.replace("-", "");

        Logger.Log("validateNumberCardMobiPhone---data------ : " + data);

        if (data.matches("^*[0-9]{12}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Serial Mobi Phone
     *
     * @param data data serial
     *             <p>
     *             <p>validate number, length serial (15) </p>
     */
    private static boolean validateNumberSerialMobiPhone(String data) {

        data = data.replace(" ", "");
        data = data.replace("-", "");

        Logger.Log("validateNumberSerialMobiPhone---data------ : " + data);

        if (data.matches("^*[0-9]{15}")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Serial Viettel
     *
     * @param data data serial
     *             <p>
     *             <p>validate number, length serial (11) </p>
     */
    public static boolean validateNumber(String data) {

        data = data.replace(" ", "");
        data = data.replace("-", "");

        if (data.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    /**
     * Check Type input data and validate Serial Viettel
     *
     * @param data data serial
     *             <p>
     *             <p>validate number, length serial (11) </p>
     */
    public static String filterData(String data) {

        if (data == null || data.isEmpty()) {
            return "";
        }

        data = data.replace(" ", "");
        data = data.replace("-", "");

        return data;
    }

    /**
     * Check Type input data and validate Serial
     *
     * @param dataTypeInput Type input data
     * @param serial        data card
     */
    public static boolean checkValidateSerialDatas(DataTypeInput dataTypeInput, String serial) {

        if (serial == null || serial.isEmpty() || dataTypeInput == null) {
            return false;
        }

        switch (dataTypeInput) {
            case TYPE_INPUT:

                if (validateNumberSerialViettel(serial)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VIETTEL:

                if (validateNumberSerialViettel(serial)) {
                    return true;
                }

                return false;
            case TYPE_INPUT_VINA:

                if (validateNumberSerialVina(serial)) {
                    return true;
                }

                return false;

            default:
                return false;
        }
    }

    public static boolean validateCurrencyVND(String money) {
//        String money = "23.234,00";
        Pattern p = Pattern.compile("^(?:0|[1-9]\\d{0,2}(?:\\.\\d{3})*)[DĐđ]$");
        Matcher m = p.matcher(money);
        if (m.matches()) {
            // valid
            Logger.Log("---------------------- : " + money + " valid");
            return true;
        }
        Logger.Log("---------------------- : " + money + " unvalid");
        // unvalid
        return false;
    }

    public static String validateDate(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }
        Logger.Log("---------validateDate------------- : " + date);
        if (date.contains("Hansudung") || date.contains("Hansidung")) {
            String str[] = date.split(":");

            Logger.Log("---------validateDate----------1--- : " + date);
            if (str.length > 0) {
                return validateDates(String.valueOf(str[1]).trim());
            }
        }
        return "";
    }

    private static String validateDates(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }
        Logger.Log("---------validateDate--------2----- : " + date);

        String datePattern = "\\d{2}/\\d{2}/\\d{4}";

        if (date.matches(datePattern)) {
            return date;
        }

        return "";
    }
}
