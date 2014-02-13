package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @Description This model is used by both screen MyOrder Global and Chain to
 *              get the data for a order object
 */
public class MyOrderGlobalObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	@SerializedName("id")
//	private int mId;
	@SerializedName("chain_name")
	private String mName;
	@SerializedName("number")
	private int mNumber;
	@SerializedName("logo")
	private String mLogo;
	@SerializedName("address")
	private String mAddress;
	@SerializedName("city")
	private String mCity;
	@SerializedName("zip")
	private String mZip;
	@SerializedName("state")
	private String mState;
	@SerializedName("country")
	private String mCountry;
	@SerializedName("dinner_status")
	private String mStatus;

	public MyOrderGlobalObject(int mId, String mName, int mNumber,
			String mLogo, String mAddress, String mCity, String mZip,
			String mState, String mCountry, String mStatus) {
//		this.mId = mId;
		this.mName = mName;
		this.mNumber = mNumber;
		this.mLogo = mLogo;
		this.mAddress = mAddress;
		this.mCity = mCity;
		this.mZip = mZip;
		this.mState = mState;
		this.mCountry = mCountry;
		this.mStatus = mStatus;
	}

//	public int getId() {
//		return mId;
//	}
//
//	public void setId(int mId) {
//		this.mId = mId;
//	}

	public String getName() {
		if (this.mName != null) {
			return mName;
		} else {
			return "";
		}
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public int getNumber() {
		return mNumber;
	}

	public void setNumber(int mNumber) {
		this.mNumber = mNumber;
	}

	public String getLogo() {
		if (this.mLogo != null) {
			return mLogo;
		} else {
			return "";
		}
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public String getAddress() {
		if (this.mAddress != null) {
			return mAddress;
		} else {
			return "";
		}
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getCity() {
		if (this.mCity != null) {
			return mCity;
		} else {
			return "";
		}
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getZip() {
		if (this.mZip != null) {
			return mZip;
		} else {
			return "";
		}
	}

	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	public String getState() {
		if (this.mState != null) {
			return mState;
		} else {
			return "";
		}
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public String getCountry() {
		if (this.mCountry != null) {
			return mCountry;
		} else {
			return "";
		}
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getStatus() {
		if (this.mStatus != null) {
			return mStatus;
		} else {
			return "";
		}
	}

	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}

}
