package com.vtc.demo.recognize.demorecognizetext.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Create Mr. Ha on 6/2/16.
 *
 * @author Mr. Ha
 */
public enum DataTypeInput {

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    ////  TYPE INPUT DATA
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    TYPE_INPUT(0),

    TYPE_INPUT_VIETTEL(1000),

    TYPE_INPUT_VINA(1001),

    TYPE_INPUT_MOBILE_PHONE(1002),

    TYPE_INPUT_VIETNAM_MOBILE(1003);

    private static final Map<Integer, DataTypeInput> typesByValue = new HashMap<>();

    private final int valuesType;

    DataTypeInput(int value) {
        this.valuesType = value;
    }

    public int getValuesType() {
        return valuesType;
    }

    static {
        for (DataTypeInput type : DataTypeInput.values()) {
            typesByValue.put(type.valuesType, type);
        }
    }

    public static DataTypeInput forValue(int value) {
        DataTypeInput type = typesByValue.get(value);
        if (type == null)
            return DataTypeInput.TYPE_INPUT_VIETTEL;
        return typesByValue.get(value);
    }
}
