package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author baonguyen
 * @Description: Show all information about transaction history included in
 *               diners' money transferring, and reward point from restaurants
 */
public class MyPointTransactionObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("location")
	private List<LocationTransactionEntity> mListLocationInfos;
	@SerializedName("points")
	private List<PointTransactionEntity> mListPointInfos;

	// Contructor with no input parameters
	public MyPointTransactionObject() {

	}

	// Contructor with input parameters
	public MyPointTransactionObject(
			List<LocationTransactionEntity> mListLocationInfos,
			List<PointTransactionEntity> mListPointInfos) {

		// Add list of Location Transaction Info
		if (this.mListLocationInfos == null) {
			this.mListLocationInfos = new ArrayList<LocationTransactionEntity>();
		}
		this.mListLocationInfos.clear();
		this.mListLocationInfos.addAll(mListLocationInfos);

		// Add list of Point Transaction Info
		if (this.mListPointInfos == null) {
			this.mListPointInfos = new ArrayList<PointTransactionEntity>();
		}
		this.mListPointInfos.clear();
		this.mListPointInfos.addAll(mListPointInfos);
	}

	public List<LocationTransactionEntity> getListLocationInfos() {
		return mListLocationInfos;
	}

	public void setListLocationInfos(
			List<LocationTransactionEntity> mListLocationInfos) {
		if (this.mListLocationInfos == null) {
			this.mListLocationInfos = new ArrayList<LocationTransactionEntity>();
		}
		this.mListLocationInfos.clear();
		this.mListLocationInfos.addAll(mListLocationInfos);
	}

	public List<PointTransactionEntity> getListPointInfos() {
		return mListPointInfos;
	}

	public void setListPointInfos(List<PointTransactionEntity> mListPointInfos) {
		if (this.mListPointInfos == null) {
			this.mListPointInfos = new ArrayList<PointTransactionEntity>();
		}
		this.mListPointInfos.clear();
		this.mListPointInfos.addAll(mListPointInfos);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}