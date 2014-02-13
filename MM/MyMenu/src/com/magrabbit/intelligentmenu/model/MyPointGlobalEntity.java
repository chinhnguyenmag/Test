package com.magrabbit.intelligentmenu.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author vule
 * 
 */
public class MyPointGlobalEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("user")
	public UserGlobalEntity mUserGlobal;

	@SerializedName("locations")
	public List<MyPointListLocationEntity> mListLocation;

	@SerializedName("error")
	public String mError;
	
	@SerializedName("message")
	public String mMessage;
}
