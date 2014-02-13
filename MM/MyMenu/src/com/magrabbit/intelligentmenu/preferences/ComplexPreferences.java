package com.magrabbit.intelligentmenu.preferences;

import java.lang.reflect.Type;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ComplexPreferences {

	private static ComplexPreferences complexPreferences;
	private final String COMPLEX_SHARED_PREFS = "ComplexConfiguration";
	private final String KEY_OBJECT = "item";
	private Context context;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private static Gson GSON = new Gson();
	Type typeOfObject = new TypeToken<Object>() {
	}.getType();

	private ComplexPreferences(Context context, int mode) {
		this.context = context;

		preferences = context.getSharedPreferences(COMPLEX_SHARED_PREFS, mode);
		editor = preferences.edit();
	}

	public static ComplexPreferences getComplexPreferences(Context context,
			int mode) {

		if (complexPreferences == null) {
			complexPreferences = new ComplexPreferences(context, mode);
		}

		return complexPreferences;
	}

	public void putObject(Object object) {
		if (object != null) {
			editor.putString(KEY_OBJECT, GSON.toJson(object));
			editor.commit();
		}

	}

	public <T> T getObject(Class<T> a) {

		String gson = preferences.getString(KEY_OBJECT, null);
		if (gson == null) {
			return null;
		} else {
			try {
				return GSON.fromJson(gson, a);
			} catch (Exception e) {
				throw new IllegalArgumentException("Object storaged with key "
						+ KEY_OBJECT + " is instanceof other class");
			}
		}
	}

	public void clearData() {
		editor.putString(KEY_OBJECT, GSON.toJson(null));
		editor.clear();
		editor.commit();

	}

}
