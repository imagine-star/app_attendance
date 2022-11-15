package com.jigong.app_attendance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	/**
	 * 第一个入参为json数据,用来解默认第一层
	 */
	public static JSONObject trasformation(String jsonData) {

		try {
			return new JSONObject(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 第一个入参为带名字的json数据,第二个参数为对对象名
	 */
	public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
		try {
			JSONObject jo = jsonObject.getJSONObject(key);
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 第一个入参为带名字的jsonobject,第二个参数为对对象名key,第三个参数为取不到值时默认值
	 *
	 */
	public static String getJsonValue(JSONObject jsonData, String key, String defaultValue) {
		if (jsonData == null) {
			if (defaultValue != null) {
				return defaultValue;
			} else {
				return "";
			}
		}
		if (!jsonData.isNull(key)) {
			try {
				String tempString = jsonData.getString(key);
				if ("".equals(tempString)) {
					if (defaultValue != null) {
						return defaultValue;
					}

				} else {
					return tempString;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (defaultValue != null) {
				return defaultValue;
			}
		}
		return "";

	}

	/**
	 * 第一个入参为带名字的jsonobject,第二个参数为对对象名key,第三个参数为取不到值时默认值
	 *
	 */
	public static double getDoubleValue(JSONObject jsonData, String key, double defaultValue) {
		if (!jsonData.isNull(key)) {
			try {

				return jsonData.getDouble(key);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultValue;

	}

	/**
	 * 第一个入参为带名字的jsonobject,第二个参数为对对象名key,第三个参数为取不到值时默认值
	 *
	 */
	public static int getIntegerValue(JSONObject jsonData, String key, int defaultValue) {
		if (!jsonData.isNull(key)) {
			try {
				return jsonData.getInt(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	/**
	 * 第一个入参为带名字的jsonobject,第二个参数为对对象名key,第三个参数为取不到值时默认值
	 *
	 */
	public static boolean getBooleanValue(JSONObject jsonData, String key, boolean defaultValue) {
		if (!jsonData.isNull(key)) {
			try {
				return jsonData.getBoolean(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	/**
	 * 第一个入参为json数据,第二个入参为jsonArray的名字
	 */
	public static JSONArray getJSONArray(JSONObject jsonData, String str) {
		try {
			return jsonData.getJSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 第一个入参为json数据,用来解默认第一层,显示服务器返回信息,用于一些不需要返回数据的接口,只显示是否成功 带回是否成功的标识位
	 */
	public static Boolean getState(String jsonData, Context mContext) {

		try {
			JSONObject jCode = new JSONObject(jsonData);
			String respCode = jCode.getString("respCode");
			// Log.d("123", "respCode:"+respCode);
			if (respCode.equals("1")) {
				// JSONObject jEntry=jCode.getJSONObject("entry");
				// String error=jEntry.getString("respMsg");
				// Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
				return true;
			} else if (respCode.equals("-99")) {

			} else {
				JSONObject jError = jCode.getJSONObject("entry");
				String error = jError.getString("respMsg");
				Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
				return false;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}
