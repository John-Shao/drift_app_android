package com.drift.foreamlib.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public class JSONObjectHelper implements Serializable{
    private String strValue = null;
    private final static String TAG = "JSONObjectHelper";

    //public JSONObjectHelper(JSONObject obj){
    //saveStr(obj);
    //}
    public JSONArray getJSonArray(JSONObject obj, String value) {
        saveStr(obj);
        try {
            return obj.getJSONArray(value);
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return null;
    }

    public JSONObject getJSonObject(JSONObject obj, String value) {
        saveStr(obj);
        try {
            return obj.getJSONObject(value);
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return null;
    }

    protected String getString(JSONObject obj, String value) {
        saveStr(obj);
        try {
            String result = obj.getString(value);
            if (result != null && result.toLowerCase(Locale.getDefault()).equals("null")) {
                return null;
            }
            return result;
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return null;
    }

    @Override
    public String toString() {
        if (strValue != null) {
            return strValue;
        } else {
            return super.toString();
        }
    }

    protected long getLong(JSONObject obj, String value, long defaultvaule) {
        saveStr(obj);
        try {
            return obj.getLong(value);
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return defaultvaule;
    }

    protected boolean getBoolean(JSONObject obj, String value, boolean defaultValue) {
        saveStr(obj);
        try {
            return obj.getBoolean(value);
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return defaultValue;
    }

    protected int getInt(JSONObject obj, String value, int defaultvaule) {
        saveStr(obj);
        try {
            return obj.getInt(value);
        } catch (JSONException e) {
            //Log.e(TAG,"Get "+value +" value fail!");
        }
        return defaultvaule;
    }

    /**
     * 缓存 str,为了toString()的返回。
     *
     * @param obj
     */
    private void saveStr(JSONObject obj) {
        if (strValue == null) {
            if (obj != null) {
                strValue = obj.toString();
            }
        }
    }
}
