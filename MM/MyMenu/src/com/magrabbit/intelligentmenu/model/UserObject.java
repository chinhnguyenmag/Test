package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.google.gson.annotations.SerializedName;

public class UserObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String mUserID = "";
	@SerializedName("email")
	public String mEmail = "";
	@SerializedName("first_name")
	public String mFirstName = "";
	@SerializedName("last_name")
	public String mLastName = "";
	@SerializedName("password")
	public String mPassword = "";
	@SerializedName("points")
	public int mPoints;
	@SerializedName("userPhotoImageURL")
	public String mProfileAvatar = "";
	@SerializedName("username")
	public String mUserName = "";
	@SerializedName("dinner_status")
	public String mUserStatus = "";
	@SerializedName("zip")
	public String mZip = "";
	@SerializedName("address")
	public String mAddress = "";
	@SerializedName("city")
	public String mCity = "";
	@SerializedName("state")
	public String mState = "";
	@SerializedName("defaultsearchprofile")
	public int mDefaultProfileId;

	public String mAccess_token = "";
	public int mTypeLogin = -1;

	// The core Google+ client.
	public PlusClient mPlusClient;
	public ConnectionResult mConnectionResult;

	private static AppPreferences mAppPreferences;
	private static UserObject instance = null;

	public static UserObject getInstance(Context context) {
		if (instance == null) {
			instance = new UserObject();
			mAppPreferences = new AppPreferences(context);
		}
		return instance;
	}

	public UserObject() {
		super();
	}

	public UserObject(String mUserID, String mUserName, String mEmail,
			String mFirstName, String mLastName, String mZip, String mAddress,
			String mCity, String mState, String mPassword, int mPoints,
			int mTypeLogin, String mUserStatus, String mProfileAvatar,
			int mDefaultsearchprofile, String accessToken) {
		super();
		this.mUserID = mUserID;
		this.mEmail = mEmail;
		this.mUserName = mUserName;
		this.mFirstName = mFirstName;
		this.mLastName = mLastName;
		this.mZip = mZip;

		this.mAddress = mAddress;
		this.mCity = mCity;
		this.mState = mState;

		this.mPassword = mPassword;
		this.mPoints = mPoints;
		this.mTypeLogin = mTypeLogin;
		this.mUserStatus = mUserStatus;
		this.mProfileAvatar = mProfileAvatar;
		this.mDefaultProfileId = mDefaultsearchprofile;
		this.mAccess_token = accessToken;
		instance = this;
	}

	public UserObject(String mUserID, String mUserName, String mEmail,
			String mFirstName, String mLastName, String mZip, String mPassword,
			int mPoints, int mTypeLogin, String mUserStatus,
			String mProfileAvatar, String accessToken) {
		super();
		this.mUserID = mUserID;
		this.mEmail = mEmail;
		this.mUserName = mUserName;
		this.mFirstName = mFirstName;
		this.mLastName = mLastName;
		this.mZip = mZip;
		this.mPassword = mPassword;
		this.mPoints = mPoints;
		this.mTypeLogin = mTypeLogin;
		this.mUserStatus = mUserStatus;
		this.mProfileAvatar = mProfileAvatar;
		this.mAccess_token = accessToken;
		instance = this;
	}

	public void setUserObject(UserObject obj) {
		this.mUserID = obj.mUserID;
		this.mEmail = obj.mEmail;
		this.mFirstName = obj.mFirstName;
		this.mLastName = obj.mLastName;
		this.mPassword = obj.mPassword;
		this.mPoints = obj.mPoints;
		this.mProfileAvatar = obj.mProfileAvatar;
		this.mUserName = obj.mUserName;
		this.mZip = obj.mZip;
		this.mAddress = obj.mAddress;
		this.mCity = obj.mCity;
		this.mState = obj.mState;
		this.mAccess_token = obj.mAccess_token;
		this.mTypeLogin = obj.mTypeLogin;
		this.mUserStatus = obj.mUserStatus;
		this.mDefaultProfileId = obj.mDefaultProfileId;
		instance = this;
	}

	public String getUserID() {
		return mUserID;
	}

	public void setUserID(String mUserID) {
		this.mUserID = mUserID;
	}

	public String getFirstName() {
		if (mFirstName == null) {
			return "";
		}
		return mFirstName;
	}

	public String getEmail() {
		if (mEmail == null) {
			return "";
		}
		return mEmail;
	}

	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	public String getLastName() {
		if (mLastName == null) {
			return "";
		}
		return mLastName;
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public int getPoints() {
		return mPoints;
	}

	public void setPoints(int mPoints) {
		this.mPoints = mPoints;
	}

	public String getUserName() {
		if (mUserName == null) {
			return "";
		}
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getZip() {
		if (mZip == null) {
			return "";
		}
		return mZip;
	}

	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	public String getAccess_token() {
		if (mAccess_token == null || mAccess_token.length() == 0) {
			if (mAppPreferences != null) {
				mAppPreferences.getAccessToken();
			}
		}
		return mAccess_token;
	}

	public void setAccess_token(String mAccess_token) {
		this.mAccess_token = mAccess_token;
	}

	public int getTypeLogin() {
		return mTypeLogin;
	}

	public void setTypeLogin(int mTypeLogin) {
		this.mTypeLogin = mTypeLogin;
	}

	public String getProfileAvatar() {
		if (mProfileAvatar == null) {
			return "";
		}
		return mProfileAvatar;
	}

	public void setProfileAvatar(String mProfileAvatar) {
		this.mProfileAvatar = mProfileAvatar;
	}

	public PlusClient getPlusClient() {
		return mPlusClient;
	}

	public void setPlusClient(PlusClient mPlusClient) {
		this.mPlusClient = mPlusClient;
	}

	public String getUserStatus() {
		if (mUserStatus == null)
			mUserStatus = "";
		return mUserStatus;
	}

	public void setUserStatus(String mUserStatus) {
		this.mUserStatus = mUserStatus;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getCity() {
		if (mCity == null)
			mCity = "";
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getState() {
		if (mState == null)
			mState = "";
		return mState;
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public int getDefaultProfileId() {
		return mDefaultProfileId;
	}

	public void setDefaultProfileId(int mDefaultProfileId) {
		this.mDefaultProfileId = mDefaultProfileId;
	}

	public ConnectionResult getConnectionResult() {
		return mConnectionResult;
	}

	public void setConnectionResult(ConnectionResult mConnectionResult) {
		this.mConnectionResult = mConnectionResult;
	}

	public boolean resetUser(Context context) {
		try {
			this.mUserID = "";
			this.mEmail = "";
			this.mFirstName = "";
			this.mLastName = "";
			this.mPassword = "";
			this.mPoints = 0;
			this.mProfileAvatar = "";
			this.mUserName = "";
			this.mZip = "";
			this.mAccess_token = "";
			this.mTypeLogin = 0;
			instance = null;
			if (mPlusClient != null) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void restoreUser() {
		try {
			if (mAppPreferences != null) {
				setUserObject(mAppPreferences.getUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
