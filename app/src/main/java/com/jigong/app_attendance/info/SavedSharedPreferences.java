package com.jigong.app_attendance.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SavedSharedPreferences {

	public static final String SHARED_PREFS = "app_config";
	
	private static SavedSharedPreferences mInstance;
	private Context mContext;
	private static final String onLine="onLine";
	private SavedSharedPreferences() {
	}
	
	public static SavedSharedPreferences getInstance() {
		if (null == mInstance) {
			mInstance = new SavedSharedPreferences();
		}
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public int getIntValue(String valueName) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getInt(valueName, 0);
	}
	
	public int getIntValue(String valueName, int defaultValue) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getInt(valueName, defaultValue);
	}

	public void setIntValue(String valueName, int value) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(valueName, value);
		editor.commit();
	}
	
	public int increaseIntValue(String valueName) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		int count = preferences.getInt(valueName, 0);
		editor.putInt(valueName, ++count);
		editor.commit();
		return count;
	}

	public int decreaseIntValue(String valueName) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		int count = preferences.getInt(valueName, 0);
		editor.putInt(valueName, --count);
		editor.commit();
		return count;
	}

	public void clearIntValue(String valueName) {
		setIntValue(valueName, 0);
	}
	
	public String getStringValue(String valueName) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getString(valueName, "");
	}
	
	public String getStringValue(String valueName, String defaultValue) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getString(valueName, defaultValue);
	}
	
	public void setStringValue(String valueName, String value) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(valueName, value);
		editor.commit();
	}
	
	@SuppressLint("WorldReadableFiles")
	private SharedPreferences getSharedPreferences() {
		assert (null != mContext);
		//MODE_WORLD_READABLE 模式已经被废弃，改成MODE_PRIVATE，否则7.0编译报错
		return mContext.getSharedPreferences(SHARED_PREFS,
				Context.MODE_PRIVATE);
	}
	
	public void setLongValue(String valueName, long value) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(valueName, value);
		editor.commit();
	}
	
	public long getLongValue(String valueName, long defaultValue) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getLong(valueName, defaultValue);
	}
	
	public long getLongValue(String valueName) {
		SharedPreferences preferences = getSharedPreferences();
		return preferences.getLong(valueName, 0l);
	}
	
	public void setBooleanValue(String valueName, boolean value) {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(valueName, value);
		editor.commit();
	}
	public Boolean getBooleanValue(String valueName, boolean defaultValue) {
		SharedPreferences preferences = getSharedPreferences();
		return  preferences.getBoolean(valueName, defaultValue);
	}

	public void clearAll() {
		SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
}
