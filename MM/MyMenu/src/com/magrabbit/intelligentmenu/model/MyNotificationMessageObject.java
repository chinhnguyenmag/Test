package com.magrabbit.intelligentmenu.model;

/**
 * @author vule
 *
 */
public class MyNotificationMessageObject {

	private int mResLogo;

	private String mResName;

	private String mTitle;

	private int mLogoType;

	private int mAvatar;

	private String mMessage;

	private String mTime;

	private String mDate;

	public MyNotificationMessageObject() {
	}

	public MyNotificationMessageObject(int mResLogo, String mResName,
			String mTitle, int mIcon, int mAvatar, String mMessage,
			String mTime, String mDate) {
		super();
		this.mResLogo = mResLogo;
		this.mResName = mResName;
		this.mTitle = mTitle;
		this.mLogoType = mIcon;
		this.mAvatar = mAvatar;
		this.mMessage = mMessage;
		this.mTime = mTime;
		this.mDate = mDate;
	}

	public int getResLogo() {
		return mResLogo;
	}

	public void setResLogo(int mResLogo) {
		this.mResLogo = mResLogo;
	}

	public String getResName() {
		return mResName;
	}

	public void setResName(String mResName) {
		this.mResName = mResName;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public int getIcon() {
		return mLogoType;
	}

	public void setIcon(int mIcon) {
		this.mLogoType = mIcon;
	}

	public int getAvatar() {
		return mAvatar;
	}

	public void setAvatar(int mAvatar) {
		this.mAvatar = mAvatar;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

}
