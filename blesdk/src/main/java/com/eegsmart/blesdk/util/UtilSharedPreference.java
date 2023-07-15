package com.eegsmart.blesdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * SharedPreference 的工具类
 */
public class UtilSharedPreference {
    /**
     * SharedPreference
     */
    private static final String PREFERENCE_FILE_NAME = "UMindMirror";

    /**
     * 向SharedPreference存入String
     * @param context Context
     * @param key key
     * @param value 存入的字符串
     */
    public static void saveString(final Context context, final String key, final String value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 向SharedPreferences存入int
     * @param context Context
     * @param key key
     * @param value int
     */
    public static void saveInt(final Context context, String key, int value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 向SharedPreferences存入boolean
     * @param context
     * @param key
     * @param value true or false
     */
    public static void saveBoolean(final Context context, String key, Boolean value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 向SharedPreferences获取存入的String
     * @param context context
     * @param key key
	 * @param deau 默认
     * @return 返回String
     */
    public static String getStringValue(final Context context, final String key, final String deau) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preference.getString(key, deau);
    }

    /**
     * 获取Boolean
     * @param context
     * @param key
     * @return 默认返回false
     */
    public static boolean getBooleanValue(final Context context, final String key) {
        SharedPreferences preference = context.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preference.getBoolean(key, false);
    }

    /**
     * 获取int
     * @param context
     * @param key
	 * @param deau 默认的返回值
     * @return
     */
    public static int getIntValue(final Context context, final String key, final int deau) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preference.getInt(key, deau);
    }

    /**
     * 保存一个object的值
     * @param context
     * @param key
     * @param value (int,boolean,float,long,string)
     */
    public static void saveOBj(final Context context, final String key, final Object value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Integer) {
            editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Float) {
            editor.putFloat(key, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            editor.putLong(key, ((Long) value).longValue());
        }
        editor.commit();
    }

    class Type<T> {
        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

    }

    /**
     * 删除值
     * @param context
     * @param key
     */
    public static void remove(final Context context , String key){
        if(context == null)
            return;
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.remove(key);
        editor.commit();
    }
}
