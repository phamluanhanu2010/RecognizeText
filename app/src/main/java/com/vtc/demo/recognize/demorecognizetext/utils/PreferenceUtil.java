/*
 * Created by Hadvlop@gmail.com on 11/2/16 11:16 AM
 * Copyright © 2016, All Rights Reserved.
 *
 * Last modified 10/10/16 2:48 PM
 */

package com.vtc.demo.recognize.demorecognizetext.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mr. Ha on 5/16/16.
 *
 * @author Mr. Ha
 */
public class PreferenceUtil {

    private SharedPreferences IShare = null;

    public static final String KEY_DATE_EXPORT = "fuck_you_1";

    public PreferenceUtil(Context context) {
        if (context != null)
            IShare = context.getSharedPreferences(context.getApplicationInfo().packageName, Context.MODE_PRIVATE);
    }

    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    ////// Remove Share Preferences
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    public void PreferenceUtilRemove(Context context) {
        try {
            if (context != null)
                IShare = context.getSharedPreferences(context.getApplicationInfo().packageName, Context.MODE_PRIVATE);
            if (IShare != null) {
                IShare.edit().clear().apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }


    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    ////// Remove Data When Logout User
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    public void RemoveDataWhenLogOut() {
        removeValue(KEY_DATE_EXPORT);
    }

    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    ////// Get Set By Other Key
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    public void setValueLong(String key, long val) {
        try {
            if (IShare != null) {
                IShare.edit().putLong(key, val).apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

    public void setValueString(String key, String val) {
        try {
            if (IShare != null) {
                IShare.edit().putString(key, val).apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

    public void setValueBoolean(String key, boolean val) {
        try {
            if (IShare != null) {
                IShare.edit().putBoolean(key, val).apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

    public void setValueInteger(String key, int val) {
        try {
            if (IShare != null) {
                IShare.edit().putInt(key, val).apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

    public int getValueInteger(String key) {
        try {
            if (IShare != null) {
                return IShare.getInt(key, 0);
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
        return 0;
    }

    public long getValueLong(String key) {
        try {
            if (IShare != null) {
                return IShare.getLong(key, -1);
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
        return -1;
    }

    public boolean getValueBoolean(String key) {
        try {
            if (IShare != null) {
                return IShare.getBoolean(key, false);
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
        return false;
    }

    public String getValueString(String key) {
        try {
            if (IShare != null) {
                return String.valueOf(IShare.getString(key, "")).trim();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
        return "";
    }

    public String getValueString(String key, String strDefault) {
        try {
            if (IShare != null) {
                return String.valueOf(IShare.getString(key, strDefault)).trim();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
        return "";
    }

    public void removeValue(String key) {
        try {
            if (IShare != null) {
                IShare.edit().remove(key).apply();
            }
        } catch (NullPointerException | ClassCastException ignored) {
        }
    }

}
