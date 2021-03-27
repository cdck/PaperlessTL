package com.xlk.paperlesstl.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/8/5.
 */

public class SharedPreferenceHelper {

    public static final String key_remember = "remember";
    public static final String key_user = "user";
    public static final String key_password = "password";
    public static final String key_administrator_login = "administrator_login";

    /**
     * 邮箱账号
     */
    public static final String key_email_account = "email_account";
    /**
     * 邮箱密码
     */
    public static final String key_email_password = "email_password";
    /**
     * 发送者名
     */
    public static final String key_email_name = "email_name";
    /**
     * 发送者邮箱
     */
    public static final String key_email_mailbox = "email_mailbox";

    //文件名
    private static final String FILE_NAME = "paperless_share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context 上下文
     * @param key     键
     * @param object  值
     */
    public static void setData(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context      上下文
     * @param key          键
     * @param defaultValue 默认值
     */
    public static Object getData(Context context, String key, Object defaultValue) {
        String type = defaultValue.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultValue);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultValue);
        }
        return null;
    }
}
