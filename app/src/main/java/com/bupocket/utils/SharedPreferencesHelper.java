package com.bupocket.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bupocket.BPApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedPreferencesHelper {
    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;
    private static final String APP_SP = "app_sp";
    private static final String TAG = SharedPreferencesHelper.class.getSimpleName();

    private SharedPreferencesHelper() {
    }

    public SharedPreferencesHelper(Context context, String FILE_NAME) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private static SharedPreferencesHelper instance = new SharedPreferencesHelper();
    private static SharedPreferences mSp = null;

    public static SharedPreferencesHelper getInstance() {
        if (mSp == null) {
            mSp = BPApplication.getContext().getSharedPreferences(APP_SP, Context.MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * 存储
     */
    public void put(String key, Object object) {
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
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    public void save(String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) return;
        Gson gson = new Gson();
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    public <T> List<T> getDataList(String tag) {
        List<T> datalist = new ArrayList<T>();
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }
}
