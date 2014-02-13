package com.magrabbit.intelligentmenu.model;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.plus.PlusClient;
import com.magrabbit.intelligentmenu.utils.SocialUtil;

public class GoogleAccount {
	private final String GOOGLE_SHARED_PREFS = "GoogleConfiguration";
	private SharedPreferences mGoogleSharedPrefs;
	private Editor mPrefsEditor;

	public String mAccessToken = "";
	public String mSecret = "";
	// The core Google+ client.
	public PlusClient mPlusClient;

	private static GoogleAccount instance;

	public static GoogleAccount getInstance(Context context) {
		if (instance == null) {
			instance = new GoogleAccount(context);
		}
		return instance;
	}

	public GoogleAccount() {

	}

	public GoogleAccount(Context context) {
		mGoogleSharedPrefs = context.getSharedPreferences(GOOGLE_SHARED_PREFS,
				Context.MODE_PRIVATE);
		mPrefsEditor = mGoogleSharedPrefs.edit();
	}

	public boolean setToken(String token) {
		mPrefsEditor.putString(OAuth.OAUTH_TOKEN, token);
		mPrefsEditor.commit();
		return true;
	}

	public boolean setSecret(String secret) {
		mPrefsEditor.putString(OAuth.OAUTH_TOKEN_SECRET, secret);
		mPrefsEditor.commit();
		return true;
	}

	public PlusClient getPlusClient() {
		return mPlusClient;
	}

	public void setPlusClient(PlusClient mPlusClient) {
		this.mPlusClient = mPlusClient;
	}

	public OAuthConsumer getConsumer() {
		String token = mGoogleSharedPrefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = mGoogleSharedPrefs.getString(OAuth.OAUTH_TOKEN_SECRET,
				"");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				SocialUtil.GOOGLE_CONSUMER_KEY,
				SocialUtil.GOOGLE_CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	public boolean isOAuthSuccessful() {
		String token = mGoogleSharedPrefs.getString(OAuth.OAUTH_TOKEN, null);
		String secret = mGoogleSharedPrefs.getString(OAuth.OAUTH_TOKEN_SECRET,
				null);
		if (token != null && secret != null)
			return true;
		else
			return false;
	}

	public void clearGoogleData() {
		mPrefsEditor.clear();
		mPrefsEditor.commit();
	}
}
