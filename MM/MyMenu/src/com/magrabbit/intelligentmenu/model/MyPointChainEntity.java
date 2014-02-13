package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 *
 */
public class MyPointChainEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("location_global")
	public List<MyPointLocationGlobal> mListLocationGlobal;

	@SerializedName("locations_chain")
	public List<MyPointChainListLocationEntity> mLocationChain;
	
	@SerializedName("error")
	public String mError;
	@SerializedName("message")
	public String mMessage;
}
