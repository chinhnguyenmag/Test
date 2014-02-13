package com.magrabbit.intelligentmenu.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	private final String APP_SHARED_PREFS = "MyMenuConfiguration";
	private SharedPreferences mAppSharedPrefs;
	private Editor mPrefsEditor;

	public AppPreferences(Context context) {
		this.mAppSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
				Context.MODE_PRIVATE);
		this.mPrefsEditor = mAppSharedPrefs.edit();
	}

	public UserObject getUser() {
		try {

			String mUserID = mAppSharedPrefs.getString("UUID", "");
			String mUserName = mAppSharedPrefs.getString("UserName", "");
			String mEmail = mAppSharedPrefs.getString("Email", "");
			String mFirstName = mAppSharedPrefs.getString("FirstName", "");
			String mLastName = mAppSharedPrefs.getString("LastName", "");
			String mZip = mAppSharedPrefs.getString("ZipCode", "");

			String mAddress = mAppSharedPrefs.getString("Address", "");
			String mCity = mAppSharedPrefs.getString("City", "");
			String mState = mAppSharedPrefs.getString("State", "");

			String mPassword = mAppSharedPrefs.getString("Password", "");
			int mPoints = mAppSharedPrefs.getInt("Points", 0);
			int mTypeLogin = mAppSharedPrefs.getInt("TypeLogin", -1);
			String mUserStatus = mAppSharedPrefs.getString("Status", "");
			String mProfileAvatar = mAppSharedPrefs.getString("Avatar", "");
			int mDefaultsearchprofile = mAppSharedPrefs.getInt(
					"Defaultsearchprofile", 0);
			String mAccessToken = mAppSharedPrefs.getString("AccessToken", "");
			UserObject userObject = new UserObject(mUserID, mUserName, mEmail,
					mFirstName, mLastName, mZip, mAddress, mCity, mState,
					mPassword, mPoints, mTypeLogin, mUserStatus,
					mProfileAvatar, mDefaultsearchprofile, mAccessToken);
			return userObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean setUser(UserObject userObject) {
		mPrefsEditor.putString("UUID", userObject.getUserID());
		mPrefsEditor.putString("UserName", userObject.getUserName());
		mPrefsEditor.putString("Email", userObject.getEmail());
		mPrefsEditor.putString("FirstName", userObject.getFirstName());
		mPrefsEditor.putString("LastName", userObject.getLastName());
		mPrefsEditor.putString("ZipCode", userObject.getZip());
		mPrefsEditor.putString("Address", userObject.getAddress());
		mPrefsEditor.putString("City", userObject.getCity());
		mPrefsEditor.putString("State", userObject.getState());
		mPrefsEditor.putString("Password", userObject.getPassword());
		mPrefsEditor.putInt("Points", userObject.getPoints());
		mPrefsEditor.putInt("TypeLogin", userObject.getTypeLogin());
		mPrefsEditor.putString("Avatar", userObject.getProfileAvatar());
		mPrefsEditor.putString("Status", userObject.getUserStatus());
		mPrefsEditor.putInt("Defaultsearchprofile",
				userObject.getDefaultProfileId());
		mPrefsEditor.putString("AccessToken", userObject.getAccess_token());
		mPrefsEditor.commit();
		return true;
	}

	public String getAccessToken() {
		return mAppSharedPrefs.getString("AccessToken", "");
	}

	public boolean setServerURL(String serverUrl) {
		mPrefsEditor.putString("Server", serverUrl);
		mPrefsEditor.commit();
		return true;
	}

	public String getServerURL() {
		String server = mAppSharedPrefs.getString("Server", "");
		return server;
	}

	public boolean setDefaultsearchprofile(int defaultsearchprofile) {
		mPrefsEditor.putInt("Defaultsearchprofile", defaultsearchprofile);
		mPrefsEditor.commit();
		return true;
	}

	public int getDefaultsearchprofile() {
		int defaultsearchprofile = mAppSharedPrefs.getInt(
				"Defaultsearchprofile", 0);
		return defaultsearchprofile;
	}

	public boolean resetdata() {
		mPrefsEditor.clear();
		mPrefsEditor.commit();
		return true;
	}
}
