package com.magrabbit.intelligentmenu.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.utils.SocialUtil;

public class TwitterAccount {
	public TwittSharing mTwitter;
	private String mUserName;
	private String mAccessToken;
	private String mSecreteKey;
	private String mUserID;
	private String mProfileImage;
	private static TwitterAccount instance;

	private TwitterAccount() {
		this.mUserID = "";
		this.mUserName = "";
		this.mAccessToken = "";
		this.mSecreteKey = "";
		this.mProfileImage = "";
	}

	public void CleanUp() {
		this.mUserName = "";
		this.mUserName = null;
		this.mAccessToken = "";
		this.mAccessToken = null;
		this.mSecreteKey = "";
		this.mSecreteKey = null;
		this.mProfileImage = null;
		this.mProfileImage = "";
	}

	public static TwitterAccount getInstance() {
		if (instance == null) {
			instance = new TwitterAccount();
		}
		return instance;
	}

	public TwittSharing getTwitter() {
		return mTwitter;
	}

	public void setTwitter(TwittSharing mTwitter) {
		this.mTwitter = mTwitter;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String userName) {
		this.mUserName = userName;
	}

	public String getSecreteKey() {
		return mSecreteKey;
	}

	public void setSecreteKey(String secreteKey) {
		this.mSecreteKey = secreteKey;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}

	public static void setInstance(TwitterAccount instance) {
		TwitterAccount.instance = instance;
	}

	public String getUserID() {
		return mUserID;
	}

	public void setUserID(String userID) {
		this.mUserID = userID;
	}

	public void loadFromSettings(Context context) {

	}

	public void save2Settings(Context context) {

	}

	public void emptySettings(Context context) {

	}

	public void ClearSettings(Context context) {
		Editor editor = context.getSharedPreferences(
				SocialUtil.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public boolean checkHasToken(Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(
				SocialUtil.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String token = savedSession.getString(SocialUtil.TWITTER_AUTH_KEY, "");
		return !token.equals("");

	}

	public String getProfileImage() {
		return mProfileImage;
	}

	public void setProfileImage(String profileImage) {
		this.mProfileImage = profileImage;
	}

}
