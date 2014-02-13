package com.magrabbit.intelligentmenu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyNotificationRestaurantListEntity implements Parcelable {

	@SerializedName("id")
	public int mId;

	@SerializedName("alert_type")
	public String mAlertType;

	@SerializedName("alert_logo")
	public String mAlertLogo;

	@SerializedName("msg_subject")
	public String mMsgSubject;

	@SerializedName("message")
	public String mMessage;

	@SerializedName("update_day")
	public String mUpdateDate;

	@SerializedName("update_time")
	public String mUpdateTimes;

	@SerializedName("status")
	public int mStatus;

	public String getmUpdateTimes() {
		if (mUpdateTimes != null) {
			return mUpdateTimes;
		} else
			return "";
	}

	public void setmUpdateTimes(String mUpdateTimes) {
		this.mUpdateTimes = mUpdateTimes;
	}

	public String getmUpdateDate() {
		if (mUpdateDate != null) {
			return mUpdateDate;
		} else
			return "";
	}

	public void setmUpdateDate(String mUpdateDate) {
		this.mUpdateDate = mUpdateDate;
	}

	public String getAlertType() {
		if (mAlertType != null) {
			return mAlertType;
		} else
			return "";
	}

	public String getAlertLogo() {
		if (mAlertLogo != null) {
			return mAlertLogo;
		} else
			return "";
	}

	public String getMsgSubject() {
		if (mMsgSubject != null) {
			return mMsgSubject;
		} else
			return "";
	}

	public String getMessage() {
		if (mMessage != null) {
			return mMessage;
		} else
			return "";
	}

	public void setAlertType(String mAlertType) {
		this.mAlertType = mAlertType;
	}

	public void setAlertLogo(String mAlertLogo) {
		this.mAlertLogo = mAlertLogo;
	}

	public void setMsgSubject(String mMsgSubject) {
		this.mMsgSubject = mMsgSubject;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}
	
	

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}



	public static final Parcelable.Creator<MyNotificationRestaurantListEntity> CREATOR = new Parcelable.Creator<MyNotificationRestaurantListEntity>() {

		@Override
		public MyNotificationRestaurantListEntity createFromParcel(Parcel source) {
			return new MyNotificationRestaurantListEntity(source);
		}

		@Override
		public MyNotificationRestaurantListEntity[] newArray(int size) {
			return new MyNotificationRestaurantListEntity[size];
		}
	};

	public MyNotificationRestaurantListEntity() {
	}

	public MyNotificationRestaurantListEntity(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mAlertType);
		dest.writeString(mAlertLogo);
		dest.writeString(mMsgSubject);
		dest.writeString(mMessage);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTimes);
		dest.writeInt(mStatus);
	}

	public void readFromParcel(Parcel source) {
		mId = source.readInt();
		mAlertType = source.readString();
		mAlertLogo = source.readString();
		mMsgSubject = source.readString();
		mMessage = source.readString();
		mUpdateDate = source.readString();
		mUpdateTimes = source.readString();
		mStatus = source.readInt();
	}

}
