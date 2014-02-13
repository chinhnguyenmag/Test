package com.magrabbit.intelligentmenu.utils;

/**
 * 
 * @author baonguyen
 * @Description Code Result to transfer Intent
 */
public class CodeResult {
	public static final int CODE_RESULT_MYSEARCH_PROFILE = 101;
	public static final int CODE_RESULT_MYACCOUNT = 102;
	public static final int CODE_RESULT_MYINSTRUCTION = 103;
	public static final int CODE_RESULT_MYINVITE_FRIEND = 104;
	public static final int CODE_RESULT_STARTOVE = 105;
	public static final int CODE_RESULT_RESTAURANT_DETAILS = 106;

	// This code result is to prevent loading map after back from
	// MySearchActivity because when click Back press it return the same result
	// code (RESULT_CANCEL) with GPS Settings Page
	public static final int CODE_RESULT_MYSEARCH_BACK = 107;
}
