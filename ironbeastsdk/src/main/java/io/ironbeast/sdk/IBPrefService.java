package io.ironbeast.sdk;

import android.content.Context;
import android.content.SharedPreferences;

class IBPrefService {

    public IBPrefService(Context context) {
        mContext = context;
    }

    public static IBPrefService getInstance(Context context) {
        synchronized (sInstanceLock) {
            if (null == sInstance) {
                sInstance = new IBPrefService(context);
            }
        }
        return sInstance;
    }

    public String load(String key, String defVal) {
        SharedPreferences pr = mContext.getSharedPreferences(Consts.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (null != pr) {
            return pr.getString(key, defVal);
        }
        return defVal;
    }

    public boolean load(String key, boolean defVal) {
        return Boolean.parseBoolean(load(key, String.valueOf(defVal)));
    }

    public int load(String key, int defVal) {
        try {
            return Integer.parseInt(load(key, String.valueOf(defVal)));
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public long load(String key, long defVal) {
        try {
            return Long.parseLong(load(key, String.valueOf(defVal)));
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public String load(String key) {
        return load(key, "");
    }

    public <T> void save(String key, T value) {
        SharedPreferences pr = mContext.getSharedPreferences(Consts.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (null != pr) {
            SharedPreferences.Editor editor = pr.edit();
            editor.putString(key, value.toString());
            editor.apply();
        }
    }

    private static final Object sInstanceLock = new Object();
    static IBPrefService sInstance;
    Context mContext;
}
