package com.magrabbit.intelligentmenu.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyNotificationMessageListEntity {

	@SerializedName("msg_id")
	public int mMsgId;

	@SerializedName("msg_subject")
	public String mMsgSubject;

	@SerializedName("location_id")
	public int mRestaurantId;

	@SerializedName("alert_type")
	public String mAlertType;

	@SerializedName("alert_logo")
	public String mAlertLogo;

	@SerializedName("message")
	public String mMessage;

	@SerializedName("update_day")
	public String mUpdateDay;

	@SerializedName("update_time")
	public String mUpdateTime;

	@SerializedName("from_user")
	public String mFromUser;

	@SerializedName("avatar")
	public String mAvatar;

	@SerializedName("msg_type")
	public String mMsgType;

	@SerializedName("points")
	public float mPoint;

	@SerializedName("is_received")
	public int mReceived;

}
