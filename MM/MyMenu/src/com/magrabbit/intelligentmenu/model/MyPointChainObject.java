package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointChainObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("location_global")
	private List<LocationGlobalEntity> mListGlobalInfos;
	@SerializedName("locations_chain")
	private List<ChainRestaurantEntity> mListChainInfos;
	
	public MyPointChainObject(List<LocationGlobalEntity> mListUserInfos,
			List<ChainRestaurantEntity> mListChains) {
		// Add list of chains
		if (this.mListChainInfos == null) {
			this.mListChainInfos = new ArrayList<ChainRestaurantEntity>();
		}
		this.mListChainInfos.clear();
		this.mListChainInfos.addAll(mListChains);
		
		// Add list of user info
		if (this.mListGlobalInfos == null) {
			this.mListGlobalInfos = new ArrayList<LocationGlobalEntity>();
		}
		this.mListGlobalInfos.clear();
		this.mListGlobalInfos.addAll(mListUserInfos);
	}

	public List<LocationGlobalEntity> getListLocationInfos() {
		return mListGlobalInfos;
	}

	public void setListLocationInfos(List<LocationGlobalEntity> mListLocationInfos) {
		if (this.mListGlobalInfos == null) {
			this.mListGlobalInfos = new ArrayList<LocationGlobalEntity>();
		}
		this.mListGlobalInfos.clear();
		this.mListGlobalInfos.addAll(mListLocationInfos);
	}

	public List<ChainRestaurantEntity> getListChainInfos() {
		return mListChainInfos;
	}

	public void setListChainInfos(List<ChainRestaurantEntity> mListChainInfos) {
		if (this.mListChainInfos == null) {
			this.mListChainInfos = new ArrayList<ChainRestaurantEntity>();
		}
		this.mListChainInfos.clear();
		this.mListChainInfos.addAll(mListChainInfos);
	}
	
	
}
