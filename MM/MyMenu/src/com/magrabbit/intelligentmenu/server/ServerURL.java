package com.magrabbit.intelligentmenu.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.magrabbit.intelligentmenu.model.MenuItem;
import com.magrabbit.intelligentmenu.model.MyOrderObject;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.utils.Constants;

public class ServerURL {
	// public static String URL = "http://dev.magrabbit.com:9000";
	// public static String URL = "http://54.245.230.7:9000";
	public static String URL = "http://50.23.223.155:3000";
	private static final String KEY_LOGIN_NORMAL = "/users/login.json";
	private static final String KEY_LOGIN_SOCIAL = "/users/check_token.json";
	private static final String KEY_SIGN_UP = "/users/register.json";
	private static final String KEY_SIGN_UP_SOCIAL = "/users/register_social.json";
	private static final String KEY_FORGOT = "/users/forgot.json";
	private static final String KEY_NORMALSEARCH = "/locations/normal_search.json?";
	private static final String KEY_LOGOUT = "/users/logout.json";
	private static final String KEY_ADVANCEDSEARCH = "/locations/advance.json?";
	private static String KEY_LOCATION = "/locations/";
	private static final String KEY_AVATAR = "/users/avatar.json";
	private static final String KEY_UPDATE_USER = "/users/settings.json";
	private static final String KEY_ITEM = "/items/";
	private static final String KEY_MY_POINT = "/points/";
	private static final String KEY_GET_ALLSEARCHPROFILE = "/users/searchprofile.json?";
	private static final String KEY_GET_RUNDEFAULTSEARCHPROFILE = "/locations/run_default.json?";
	private static final String KEY_POST_ADDSEARCHPROFILE = "/users/addsearchprofile.json";
	private static final String KEY_POST_EDITSEARCHPROFILE = "/users/editsearchprofile.json";
	private static final String KEY_GET_DELETESEARCHPROFILE = "/users/deletesearchprofile.json?";
	private static final String KEY_POST_SETSEARCHPROFILE = "/users/set_default.json";
	private static final String KEY_GET_ALL_MENUTYPE = "/locations/menutype.json?";
	private static final String KEY_GET_ALL_ITEMTYPE = "/locations/itemtype.json?";
	private static final String KEY_POST_SHARE_POINT_SMS = "/invited/invite_sms_share.json";
	private static final String KEY_POST_SHARE_POINT_EMAIL = "/invited/invite_email_share.json";
	private static final String KEY_POST_SHARE_POINT_FRIEND = "/points/point_share_friend.json";
	private static final String KEY_INVITE_FIREND_SMS = "/invited/invite_sms.json";
	private static final String KEY_INVITE_FIREND_EMAIL = "/invited/invite_email.json";
	private static final String KEY_MYFRIENDS = "/invited/";
	private static final String KEY_MYNOTIFICATION_GLOBAL = "/message_global.json?";
	private static final String KEY_MYNOTIFICATION_CHAIN = "/message_restaurant.json?";
	private static final String KEY_MYNOTIFICATION_RESTAURANT = "/get_message.json?";
	private static final String KEY_MYNOTIFICATION_MESSAGE = "/get_detail_message.json?";
	private static final String KEY_MYNOTIFICATION_REPLY_MESSAGE = "/points/reply_message.json";
	private static final String KEY_UPDATE_RESTAURANT_COMMENT = "/locations/:location_id/update_comment.json";
	private static final String KEY_UPDATE_MENUITEM_COMMENT = "/items/:item_id/update_comment.json";
	private static final String KEY_GET_MYORDER_GLOBAL = "/order/order_global.json?";
	private static final String KEY_GET_MYORDER_CHAIN = "/order/order_chain.json?";
	private static final String KEY_GET_MYORDER_BY_LOCATION = "/order/orders.json?";
	private static final String KEY_GET_MYORDER_DETAILS = "/order/35/details.json?";
	private static final String KEY_GET_MYFAVORITE = "/favourite_global.json?";
	private static final String KEY_GET_MYFAVORITE_CHAIN = "/favourite_restaurant.json?";
	private static final String KEY_GET_MYFAVORITE_RESTAURANT = "/favourite_items.json?";
	private static final String KEY_CATEGORY = "/category/";
	private static final String KEY_MY_FEEDBACK = "/users/feedback.json";
	private static final String KEY_GET_CURRENT_ORDER = "/order/my_order.json";
	private static final String KEY_UPDATE_ORDER = "/order/update_order.json";
	private static final String KEY_MENUITEM_NEW_ORDER = "/order/new_order.json";
	private static final String KEY_MENUITEM_UPDATE_ORDER = "/order/update_item.json";
	private static final String KEY_MYINSTRUCTION = "/instruction/get_instruction_categories.json?";
	private static final String KEY_MYINSTRUCTION_ITEMS = "/instruction/get_instruction_items.json?";
	private static final String KEY_POST_SERVER_RATING = "/order/server_rating.json";
	private static final String KEY_GET_ALL_UNREAD_MSG = "/get_unread_by_chain.json?";
	private static final String KEY_PAY_ORDER = "/order/pay_order.json";
	private static final String KEY_GET_DELETE_NOTIFICATION_BY_RESTAURANT = "/delete_message_by_restaurant.json";
	private static final String KEY_GET_DELETE_NOTIFICATION_BY_MESSAGE = "/delete_message.json";

	/**
	 * Get URL Category
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlCategory(int location_id, String access_token) {
		return URL + KEY_LOCATION + location_id + "/menu.json"
				+ "?access_token=" + access_token;
	}

	/**
	 * get Entity Category
	 * 
	 * @return
	 */
	public static StringEntity getEntityListCategory(String access_token) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL login
	 * 
	 * @return
	 */
	public static String getUrlLogin() {
		return URL + KEY_LOGIN_NORMAL;
	}

	/**
	 * get Entity login
	 * 
	 * @return
	 */
	public static StringEntity getEntityLogin(String username, String password,
			boolean isEmail) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				if (isEmail) {
					jObject.put("email", username);
				} else {
					jObject.put("username", username);
				}

				jObject.put("password", password);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Check token
	 * 
	 * @return
	 */
	public static String getUrlCheckToken() {
		return URL + KEY_LOGIN_SOCIAL;
	}

	/**
	 * get Entity check token
	 * 
	 * @return
	 */
	public static StringEntity getEntityCheckToken(String uid, int typeLogin,
			String username, String email) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("uid", uid);

				switch (typeLogin) {
				case Constants.LOGIN_VIA_FACEBOOK:
					jObject.put("provider", Constants.PROVIDER_FACEBOOK);
					break;
				case Constants.LOGIN_VIA_TWITTER:
					jObject.put("provider", Constants.PROVIDER_TWITTER);
					jObject.put("username", username);
					break;
				case Constants.LOGIN_VIA_GOOGLE:
					jObject.put("provider", Constants.PROVIDER_GOOGLE);
					break;
				}

				if (email.length() > 0) {
					jObject.put("email", email);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL forgot password
	 * 
	 * @return
	 */
	public static String getUrlForgotPassword() {
		return URL + KEY_FORGOT;
	}

	/**
	 * get Entity forgot password
	 * 
	 * @return
	 */
	public static StringEntity getEntityForgotPass(String email) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("email", email);

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	public static String getUrlAdvancedSearch() {
		return URL + KEY_ADVANCEDSEARCH;
	}

	/**
	 * Get URL for AdvancedSearch function
	 * 
	 * @param restaurant_rating_min
	 * @param restaurant_rating_max
	 * @param item_price_min
	 * @param item_price_max
	 * @param point_offered_min
	 * @param point_offered_max
	 * @param item_rating_min
	 * @param item_rating_max
	 * @param radius
	 * @param item_type
	 * @param menu_type
	 * @param Keyword
	 * @param server_rating_min
	 * @param server_rating_max
	 * @return
	 */
	public static StringEntity getEntityAdvancedSearch(Context context,
			double latitude, double longitude, int restaurant_rating_min,
			int restaurant_rating_max, int item_price_min, int item_price_max,
			int point_offered_min, int point_offered_max, int item_rating_min,
			int item_rating_max, String radius, List<Integer> item_type,
			List<Integer> menu_type, String Keyword, int server_rating_min,
			int server_rating_max) {
		try {
			JSONObject jObject = new JSONObject();
			try {

				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());
				jObject.put("latitude", latitude);
				jObject.put("longitude", longitude);
				jObject.put("restaurant_rating_min", restaurant_rating_min);
				jObject.put("restaurant_rating_max", restaurant_rating_max);
				jObject.put("item_price_min", item_price_min);
				jObject.put("item_price_max", item_price_max);
				jObject.put("point_offered_min", point_offered_min);
				jObject.put("point_offered_max", point_offered_max);
				jObject.put("item_rating_min", item_rating_min);
				jObject.put("item_rating_max", item_rating_max);
				jObject.put("radius", radius);

				// Follow Web Service
				jObject.put("item_type", item_type.toString());
				jObject.put("menu_type", menu_type.toString());
				jObject.put("keyword", Keyword);
				jObject.put("server_rating_min", server_rating_min);
				jObject.put("server_rating_max", server_rating_max);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Sign Up
	 * 
	 * @return
	 */
	public static String getUrlSignUpSocial() {
		return URL + KEY_SIGN_UP_SOCIAL;
	}

	/**
	 * get Entity Sign Up.
	 * 
	 * @return
	 */

	public static StringEntity getEntitySignUpSocial(String id,
			String provider, String username, String password, String email,
			String firstName, String lastName, String address, String zipCode) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("uid", id);
				jObject.put("provider", provider);
				jObject.put("username", username);
				jObject.put("password", password);
				jObject.put("email", email);
				jObject.put("first_name", firstName);
				jObject.put("address", "");
				jObject.put("last_name", lastName);
				jObject.put("zip", zipCode);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Sign Up
	 * 
	 * @return
	 */
	public static String getUrlSignUp() {
		return URL + KEY_SIGN_UP;
	}

	/**
	 * get Entity Sign Up.
	 * 
	 * @return
	 */

	public static StringEntity getEntitySignUp(String username,
			String password, String email, String firstName, String lastName,
			String address, String zipCode) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("username", username);
				jObject.put("password", password);
				jObject.put("email", email);
				jObject.put("first_name", firstName);
				jObject.put("address", "");
				jObject.put("last_name", lastName);
				jObject.put("zip", zipCode);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	public static String getUrlNormalSearch() {
		return URL + KEY_NORMALSEARCH;
	}

	/**
	 * Get URL Normal Search
	 * 
	 * @return
	 */

	public static StringEntity getEntityNormalSearch(String accessToken,
			String latitude, String longitude, String restaurantName) {

		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", accessToken);
				jObject.put("latitude", latitude);
				jObject.put("longitude", longitude);
				// jObject.put("latitude", "29.760074");
				// jObject.put("longitude", "-95.360237");
				jObject.put("name", restaurantName);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Avatar
	 * 
	 * @return
	 */
	public static String getUrlAvatar() {
		return URL + KEY_AVATAR;
	}

	/**
	 * Get URL Logout
	 * 
	 * @return
	 */
	public static String getUrlLogout() {
		return URL + KEY_LOGOUT;
	}

	/**
	 * get Entity Logout
	 * 
	 * @return
	 */
	public static StringEntity getEntityLogout(Context context) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("access_token", UserObject.getInstance(context)
					.getAccess_token());

			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Update User
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUpdateUser() {
		return URL + KEY_UPDATE_USER;
	}

	/**
	 * get Entity Update user
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateUser(Context mContext,
			String first_name, String last_name, String zip,String username)
			throws UnsupportedEncodingException {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(mContext)
						.getAccess_token());
				jObject.put("first_name", first_name);
				jObject.put("last_name", last_name);
				jObject.put("zip", zip);
				jObject.put("username", username);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to add comment and rating of customer
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddComment(int item_id) {
		return URL + KEY_ITEM + item_id + "/comment.json";
	}

	/**
	 * get Entity Comment And Rating menu item
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddComment(String access_token,
			int rating, String comment, int category_id, int menu_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("rating", rating);
				jObject.put("comment", comment);
				jObject.put("category_id", category_id);
				jObject.put("menu_id", menu_id);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Location Comment
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlLocationComent(int location_id) {
		return URL + KEY_LOCATION + location_id + "/comment.json";
	}

	/**
	 * get Entity Comment And Rate Location
	 * 
	 * @return
	 */
	public static StringEntity getEntityCommentAndRateLocationSave(
			String access_token,

			int rating, String comment) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("rating", rating);
				jObject.put("comment", comment);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get comment and rating of customer
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListCommentsByMenuItem(int item_id,
			String access_token, int category_id, int menu_id, int limit,
			int offset) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_ITEM + item_id
					+ "/comments.json?");
			strUrl.append("access_token=" + access_token);
			strUrl.append("&category_id=" + category_id);
			strUrl.append("&menu_id=" + menu_id);
			strUrl.append("&limit=" + limit);
			strUrl.append("&offset=" + offset);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to add next time of customer
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddNextTime(int item_id) {
		return URL + KEY_ITEM + item_id + "/nexttime.json";
	}

	/**
	 * get Entity add next time
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddNextTime(String access_token,
			int nexttime, int menu_id, int category_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("nexttime", nexttime);
				jObject.put("menu_id", menu_id);
				jObject.put("category_id", category_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to add favorite of customer
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddFavorite(int item_id) {
		return URL + KEY_ITEM + item_id + "/add_favourite.json";
	}

	/**
	 * get Entity add favorite
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddFavorite(String access_token,
			int is_favourite, int category_id, int menu_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("is_favourite", is_favourite);
				jObject.put("category_id", category_id);
				jObject.put("menu_id", menu_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to add favorite of location
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddFavoriteLocation(int location_id) {
		return URL + KEY_LOCATION + location_id + "/favourite.json";
	}

	/**
	 * get Entity add favorite location
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddFavoriteLocation(
			String access_token, int favItem) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("is_favourite", favItem);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get comment and rating of restaurant
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListCommentsByLocation(int location_id,
			String access_token, int limit, int offset) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_LOCATION
					+ location_id + "/comments.json?");
			strUrl.append("access_token=" + access_token);
			strUrl.append("&limit=" + limit);
			strUrl.append("&offset=" + offset);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL location by ID.
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlLocationByID(int location_id, String access_token) {
		return URL + KEY_LOCATION + location_id + ".json?access_token="
				+ access_token;
	}

	// ===========================MYPOINT========
	/**
	 * Get URL to get global point of My point
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlGlobalPoint(String access_token) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_MY_POINT
					+ "/my_global_point.json?");
			strUrl.append("access_token=" + access_token);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get chain point of My point
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlChainPoint(String access_token, String chain_name) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_MY_POINT
					+ "mypoint.json?");
			strUrl.append("access_token=" + access_token);
			strUrl.append(
					"&chain_name=" + URLEncoder.encode(chain_name, "UTF-8"))
					.toString().replace("+", "%20");
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get restaurant point of My point
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlRestaurantPoint(String access_token,
			int location_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_MY_POINT
					+ "point_detail.json?");
			strUrl.append("access_token=" + access_token);
			strUrl.append("&location_id=" + location_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to Share points
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlSharePoint() {
		return URL + KEY_MY_POINT + "/sharetouser.json";
	}

	/**
	 * get Entity Share point of my point
	 * 
	 * @return
	 */
	public static StringEntity getEntitySharePoint(String access_token,
			String to_userl, int point) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("to_userl", to_userl);
				jObject.put("point", point);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get My Search
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlGetAllSearchProfile(Context context) {
		return URL + KEY_GET_ALLSEARCHPROFILE + "access_token="
				+ UserObject.getInstance(context).getAccess_token();
	}

	/**
	 * Get URL Normal Search
	 * 
	 * @return
	 */

	public static String getUrlRunDefaultSearch(String accessToken,
			double latitude, double longitude, int search_profile_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_GET_RUNDEFAULTSEARCHPROFILE);
			strUrl.append("access_token=" + accessToken);
			strUrl.append("&latitude=" + latitude);
			strUrl.append("&longitude=" + longitude);
			strUrl.append("&search_profile_id=" + search_profile_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get Get My Search Profile
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlGetSearchProfile() {
		return URL + KEY_POST_SETSEARCHPROFILE;
	}

	/**
	 * get Entity MySearchProfile of my point
	 * 
	 * @return
	 */
	public static StringEntity getEntityGetSearchProfile(Context context,
			int search_profile_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("search_profile_id", search_profile_id);

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("POST ADD_SEARCH_PROFILE", jObject.toString());
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Add My Search Profile
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddSearchProfile() {
		return URL + KEY_POST_ADDSEARCHPROFILE;
	}

	/**
	 * get Entity MySearchProfile of my point
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddMySearch(Context context,
			int restaurant_rating_min, int restaurant_rating_max,
			int item_price_min, int item_price_max, int point_offered_min,
			int point_offered_max, int item_rating_min, int item_rating_max,
			int radius, List<Integer> item_type, List<Integer> menu_type,
			String Keyword, int server_rating_min, int server_rating_max,
			int IsDefault, String Name) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("name", Name);
				jObject.put("restaurant_rating_min", restaurant_rating_min);
				jObject.put("restaurant_rating_max", restaurant_rating_max);
				jObject.put("item_price_min", item_price_min);
				jObject.put("item_price_max", item_price_max);
				jObject.put("point_offered_min", point_offered_min);
				jObject.put("point_offered_max", point_offered_max);
				jObject.put("item_rating_min", item_rating_min);
				jObject.put("item_rating_max", item_rating_max);
				jObject.put("radius", radius);
				JSONArray array1 = new JSONArray();
				for (Integer in : item_type) {
					array1.put(in);
				}
				jObject.put("item_type", array1);
				JSONArray array2 = new JSONArray();
				for (Integer in : menu_type) {
					array2.put(in);
				}
				jObject.put("menu_type", array2);
				jObject.put("keyword", Keyword);
				jObject.put("server_rating_min", server_rating_min);
				jObject.put("server_rating_max", server_rating_max);
				jObject.put("isdefault", IsDefault);

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("POST ADD_SEARCH_PROFILE", jObject.toString());
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Edit My Search Profile
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlEditSearchProfile() {
		return URL + KEY_POST_EDITSEARCHPROFILE;
	}

	/**
	 * get Entity MySearchProfile of MySearch
	 * 
	 * @return
	 */
	public static StringEntity getEntityEditMySearch(Context context,
			int search_profile_id, int restaurant_rating_min,
			int restaurant_rating_max, int item_price_min, int item_price_max,
			int point_offered_min, int point_offered_max, int item_rating_min,
			int item_rating_max, int radius, List<Integer> item_type,
			List<Integer> menu_type, String Keyword, int server_rating_min,
			int server_rating_max, String Name) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("access_token", UserObject.getInstance(context)
					.getAccess_token());

			jObject.put("search_profile_id", search_profile_id);
			jObject.put("restaurant_rating_min", restaurant_rating_min);
			jObject.put("restaurant_rating_max", restaurant_rating_max);
			jObject.put("item_price_min", item_price_min);
			jObject.put("item_price_max", item_price_max);
			jObject.put("point_offered_min", point_offered_min);
			jObject.put("point_offered_max", point_offered_max);
			jObject.put("item_rating_min", item_rating_min);
			jObject.put("item_rating_max", item_rating_max);
			jObject.put("radius", radius);
			/*
			 * Uncomment to post edit profile for item_type and menu_type =
			 * [1,2,3]
			 */
			JSONArray array1 = new JSONArray();
			for (Integer in : item_type) {
				array1.put(in);
			}
			jObject.put("item_type", array1);
			JSONArray array2 = new JSONArray();
			for (Integer in : menu_type) {
				array2.put(in);
			}
			jObject.put("menu_type", array2);

			/* Post edit profile for item_type and menu_type = "1,2,3" */
			// jObject.put("item_type", Utils.listIntToString(item_type));
			// jObject.put("menu_type", Utils.listIntToString(menu_type));
			jObject.put("keyword", Keyword);
			jObject.put("server_rating_min", server_rating_min);
			jObject.put("server_rating_max", server_rating_max);
			jObject.put("name", Name);

			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Delete My Search Profile
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlDeleteSearchProfile() {
		return URL + KEY_GET_DELETESEARCHPROFILE;
	}

	/**
	 * get Entity MySearchProfile of AdvancedSearch
	 * 
	 * @return
	 */
	public static StringEntity getEntityDeleteMySearch(Context context,
			int searchProfileId) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("search_profile_id", searchProfileId);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Edit My Search Profile
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlSetDefaultSearchProfile() {
		return URL + KEY_POST_SETSEARCHPROFILE;
	}

	/**
	 * get Entity MySearchProfile of my point
	 * 
	 * @return
	 */
	public static StringEntity getEntitySetDefaultMySearch(Context context,
			int searchProfileId) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("search_profile_id", searchProfileId);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Menu Type
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMenuType(Context context) {
		return URL + KEY_GET_ALL_MENUTYPE + "access_token="
				+ UserObject.getInstance(context).getAccess_token();
	}

	/**
	 * Get Item Type
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlItemType(Context context) {
		return URL + KEY_GET_ALL_ITEMTYPE + "access_token="
				+ UserObject.getInstance(context).getAccess_token();
	}

	/**
	 * Get URL to get list friends of My friend
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListMyFriends(String access_token) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_MYFRIENDS
					+ "friend_list.json?");
			strUrl.append("access_token=" + access_token);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get Share Point through SMS
	 * 
	 * @return a link URL from server to get database or put data to server
	 */
	public static String getUrlSharePointSMS() {
		return URL + KEY_POST_SHARE_POINT_SMS;
	}

	/**
	 * get Entity Share Point through SMS
	 * 
	 * @return
	 */
	public static StringEntity getEntitySharePointSMS(Context context,
			String phone, int location_id, int point, String first_name,
			String last_name) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("phone", phone);
				jObject.put("location_id", location_id);
				jObject.put("point", point);
				jObject.put("first_name", first_name);
				jObject.put("last_name", last_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Share Point through Email
	 * 
	 * @return a link URL from server to get database or put data to server
	 */
	public static String getUrlSharePointEmail() {
		return URL + KEY_POST_SHARE_POINT_EMAIL;
	}

	/**
	 * get Entity Share Point through Email
	 * 
	 * @return
	 */
	public static StringEntity getEntitySharePointEmail(Context context,
			String email, int location_id, int point, String first_name,
			String last_name) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("email", email);
				jObject.put("location_id", location_id);
				jObject.put("point", point);
				jObject.put("first_name", first_name);
				jObject.put("last_name", last_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get Share Point through FriendList
	 * 
	 * @return a link URL from server to get database or put data to server
	 */
	public static String getUrlSharePointFriendList() {
		return URL + KEY_POST_SHARE_POINT_FRIEND;
	}

	/**
	 * get Entity Share Point through FriendList
	 * 
	 * @return
	 */
	public static StringEntity getEntitySharePointFriendList(Context context,
			int to_user, int location_id, int point) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());

				jObject.put("to_user", to_user);
				jObject.put("location_id", location_id);
				jObject.put("point", point);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get invite friends SMS of My friend
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlInviteFriendSMS() {

		return URL + KEY_INVITE_FIREND_SMS;
	}

	/**
	 * get Entity Update user
	 * 
	 * @return
	 */
	public static StringEntity getEntityChangePassword(String access_token,
			String password) {

		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("password", password);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get Entity InviteFriendSMS.
	 * 
	 * @return
	 */
	public static StringEntity getEntityInviteFriendSMS(Context context,
			String phone, String first_name, String last_name) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());
				jObject.put("phone", phone);
				jObject.put("first_name", first_name);
				jObject.put("last_name", last_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get invite friends Email of My friend
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlInviteFriendEmail() {

		return URL + KEY_INVITE_FIREND_EMAIL;
	}

	/**
	 * get Entity InviteFriendSMS.
	 * 
	 * @return
	 */
	public static StringEntity getEntityInviteFrienEmail(Context context,
			String email, String first_name, String last_name) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", UserObject.getInstance(context)
						.getAccess_token());
				jObject.put("email", email);
				jObject.put("first_name", first_name);
				jObject.put("last_name", last_name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get list notification of global
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMyNotificationGlobal(String access_token) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_MYNOTIFICATION_GLOBAL);
			strUrl.append("access_token=" + access_token);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get list notification of chain
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMyNotificationChain(String access_token,
			String chain_name) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_MYNOTIFICATION_CHAIN);
			strUrl.append("access_token=" + access_token);
			strUrl.append(
					"&chain_name=" + URLEncoder.encode(chain_name, "UTF-8"))
					.toString().replace("+", "%20");
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get list notification of restaurant
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMyNotificationRestaurant(String access_token,
			int location_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_MYNOTIFICATION_RESTAURANT);
			strUrl.append("access_token=" + access_token);
			strUrl.append("&location_id=" + location_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get list notification of restaurant
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMyNotificationMessage(String access_token,
			int messageId) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_MYNOTIFICATION_MESSAGE);
			strUrl.append("access_token=" + access_token);
			strUrl.append("&message_id=" + messageId);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * This method use to delete friend from list friend
	 * 
	 * @return a link URL from server to get database or put data to server
	 */
	public static String getUrlDeleteFriend() {
		return URL + KEY_MYFRIENDS + "delete_friend.json";
	}

	/**
	 * get Entity for delete friend
	 * 
	 * @return
	 */
	public static StringEntity getEntityDeleteFriend(String access_token,
			int friend_id) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("access_token", access_token);
			jObject.put("friend_id", friend_id);
			return new StringEntity(jObject.toString(), "UTF-8");

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get url reply message
	 * 
	 * @return a link URL from server to get database or put data to server
	 */
	public static String getUrlReplyMessage() {
		return URL + KEY_MYNOTIFICATION_REPLY_MESSAGE;
	}

	/**
	 * get Entity reply message for restaurant
	 * 
	 * @return
	 */
	public static StringEntity getEntityReplyMessage(String access_token,
			int from_user, int location_id, int msg_id, int is_accept,
			double points, String message, String alert_type) {

		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("from_user", from_user);
				jObject.put("location_id", location_id);
				jObject.put("msg_id", msg_id);
				jObject.put("is_accept", is_accept);
				jObject.put("points", points);
				jObject.put("message", message);
				jObject.put("alert_type", alert_type);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to Update restaurant comments
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUpdateRestaurantComment(int location_id) {
		return URL + "/locations/" + location_id + "/update_comment.json";
	}

	/**
	 * get Entity Update restaurant comments
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateRestaurantComment(
			String access_token, int rating, String comment, int comment_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("rating", rating);
				jObject.put("comment", comment);
				jObject.put("comment_id", comment_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to Update menu item comments
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUpdateMenuItemComment(int item_id) {
		return URL + "/items/" + item_id + "/update_comment.json";
	}

	/**
	 * get Entity Update restaurant comments
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateMenuItemComment(
			String access_token, int rating, String comment, int comment_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("rating", rating);
				jObject.put("comment", comment);
				jObject.put("comment_id", comment_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Search Menu item
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlSearchMenuItem(int location_id,
			String access_token, String keyword) {
		try {
			return URL + KEY_LOCATION + location_id
					+ "/search_restaurant_item.json" + "?access_token="
					+ access_token + "&keyword="
					+ URLEncoder.encode(keyword, "UTF-8").replace("+", "%20");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL MyOrder Global
	 * 
	 * @return
	 */

	public static String getUrlGetMyOrderGlobal(String accessToken) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_GET_MYORDER_GLOBAL);
			strUrl.append("access_token=" + accessToken);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL MyOrder Chain
	 * 
	 * @return
	 */

	public static String getUrlGetMyOrderChain(String accessToken,
			String chain_name) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_GET_MYORDER_CHAIN);
			strUrl.append("access_token=" + accessToken);
			strUrl.append("&chain_name="
					+ URLEncoder.encode(chain_name, "UTF-8")
							.replace("+", "%20"));
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL MyOrder by location
	 * 
	 * @return
	 */

	public static String getUrlGetMyOrderByLocation(String accessToken,
			int location_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_GET_MYORDER_BY_LOCATION);
			strUrl.append("access_token=" + accessToken);
			strUrl.append("&location_id=" + location_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL MyOrder by Details
	 * 
	 * @return
	 */

	public static String getUrlGetMyOrderDetails(String accessToken,
			int location_id, int order_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + "/order/" + order_id
					+ "/details.json?");
			strUrl.append("access_token=" + accessToken);
			strUrl.append("&location_id=" + location_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL My Favorite global
	 * 
	 * @return
	 */

	public static String getUrlMyFavoriteGlobal(String accessToken) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_GET_MYFAVORITE);
			strUrl.append("access_token=" + accessToken);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL My Favorite chain
	 * 
	 * @return
	 */

	public static String getUrlMyFavoriteChain(String accessToken,
			String chain_name) {

		try {
			return URL
					+ KEY_GET_MYFAVORITE_CHAIN
					+ "access_token="
					+ accessToken
					+ "&chain_name="
					+ URLEncoder.encode(chain_name, "UTF-8")
							.replace("+", "%20");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL My Favorite restaurant
	 * 
	 * @return
	 */

	public static String getUrlMyFavoriteRestaurant(String accessToken,
			int location_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_GET_MYFAVORITE_RESTAURANT);
			strUrl.append("access_token=" + accessToken);
			strUrl.append("&location_id=" + location_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL Category
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMenuItem(int location_id, String access_token,
			int category_id) {
		return URL + KEY_CATEGORY + "category_items.json" + "?access_token="
				+ access_token + "&location_id=" + location_id
				+ "&category_id=" + category_id;
	}

	/**
	 * Get URL to add favorite to Server in My Order
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddFavoriteMyOrder() {
		return URL + "/order/server_favourite.json";
	}

	/**
	 * get Entity add favorite to Server in My Order
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddFavoriteMyOrder(String access_token,
			int server_id, int is_favourite) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("server_id", server_id);
				jObject.put("is_favourite", is_favourite);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Get URL My feedback
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlMyFeedback() {
		return URL + KEY_MY_FEEDBACK;
	}

	/**
	 * get Entity Category
	 * 
	 * @return
	 */
	public static StringEntity getEntityMyFeedBack(String access_token,
			int rating, String comment) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("rating", rating);
				jObject.put("comment", comment);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL Current Order
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlCurrentOrder(Context context, int location_id) {
		return URL + KEY_GET_CURRENT_ORDER + "?access_token="
				+ UserObject.getInstance(context).mAccess_token
				+ "&location_id=" + location_id;
	}

	/**
	 * Get URL to add new order
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddNewOrderItem() {
		return URL + KEY_MENUITEM_NEW_ORDER;
	}

	/**
	 * get Entity add new order
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddNewOrderItem(String access_token,
			int location_id, float tax, MenuItem itemOject, int quantity) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("location_id", location_id);
				jObject.put("tax", tax);

				// Calendar c = Calendar.getInstance();
				// SimpleDateFormat df = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// df.setTimeZone(TimeZone.getDefault());
				// String formattedDate = df.format(c.getTime());
				// jObject.put("order_date", formattedDate);

				JSONObject jItemOject = new JSONObject();
				jItemOject.put("id", itemOject.getId());
				jItemOject.put("menu_id", itemOject.getMenuId());
				jItemOject.put("category_id", itemOject.getCategory_id());
				// jItemOject.put("order_item_id", itemOject.getOrderItemId());
				// it's always pending,so = 0 (constant)
				// jItemOject.put("order_item_status", 0);
				jItemOject.put("quantity", quantity);
				jItemOject.put("price", itemOject.getPrice());
				jItemOject.put("redemption_value",
						itemOject.getRedemptionValue());
				jObject.put("items", jItemOject);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to update order
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUpdateOrderItem() {
		return URL + KEY_MENUITEM_UPDATE_ORDER;
	}

	public static String getUrlUpdateOrder() {
		return URL + KEY_UPDATE_ORDER;
	}

	/**
	 * get Entity Update Order
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateOrder(Context context,
			int order_id, int server_id, float tip, float tax, float subTotal,
			float total, List<MyOrderObject> listOrder, int status, int isPay) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token",
						UserObject.getInstance(context).mAccess_token);
				jObject.put("order_id", order_id);
				jObject.put("server_id", server_id);
				jObject.put("total_tip", tip);
				jObject.put("total_tax", tax);
				jObject.put("sub_price", subTotal);
				jObject.put("total_price", total);
				jObject.put("order_status", status);
				jObject.put("is_paid", isPay);
				JSONArray array = new JSONArray();

				for (MyOrderObject myOrderObject : listOrder) {
					JSONObject j = new JSONObject();
					j.put("id", myOrderObject.getIdMenuItem());
					j.put("menu_id", myOrderObject.getIdMenu());
					j.put("category_id", myOrderObject.getIdCategory());
					j.put("quantity", myOrderObject.getQuantity());
					j.put("note", myOrderObject.getComment());
					j.put("use_point", myOrderObject.getUsePoint());
					j.put("is_delete", myOrderObject.isDelete());
					j.put("status", myOrderObject.getStatus());
					j.put("order_item_id", myOrderObject.getOrderItemId());
					array.put(j);
					j = null;
				}
				jObject.put("items", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get Entity update order
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateOrderItem(String access_token,
			int order_id, MenuItem itemOject, int quantity) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("order_id", order_id);

				JSONObject jItemOject = new JSONObject();
				JSONArray jItemArray = new JSONArray();
				jItemOject.put("id", itemOject.getId());
				jItemOject.put("menu_id", itemOject.getMenuId());
				jItemOject.put("category_id", itemOject.getCategory_id());
				jItemOject.put("order_item_id", itemOject.getOrderItemId());
				jItemOject.put("order_item_status", itemOject.getStatus());
				jItemOject.put("quantity", quantity);
				jItemOject.put("price", itemOject.getPrice());
				jItemOject.put("redemption_value",
						itemOject.getRedemptionValue());
				jItemArray.put(jItemOject);
				jObject.put("items", jItemArray);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get instruction category
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListMyInStructions(String access_token) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_MYINSTRUCTION);
			strUrl.append("access_token=" + access_token);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to get instruction's item
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListMyInStructionItems(String access_token,
			int category_id) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ KEY_MYINSTRUCTION_ITEMS);
			strUrl.append("access_token=" + access_token);
			strUrl.append("&category_id=" + category_id);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to add Server comment and rating of customer
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlAddCommentServer() {
		return URL + KEY_POST_SERVER_RATING;
	}

	/**
	 * get Entity Comment And Rating menu item
	 * 
	 * @return
	 */
	public static StringEntity getEntityAddCommentServer(String access_token,
			int server_id, int rating, String comment) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("server_id", server_id);
				jObject.put("rating", rating);
				jObject.put("comment", comment);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get comment and rating of Server
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlListCommentsServer(String access_token,
			int server_id, int limit, int offset) {

		try {
			StringBuffer strUrl = new StringBuffer(URL
					+ "/order/get_server_comments.json?");
			strUrl.append("access_token=" + access_token);
			strUrl.append("&server_id=" + server_id);
			strUrl.append("&limit=" + limit);
			strUrl.append("&offset=" + offset);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get URL to Update Server comments
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUpdateServerComment() {
		return URL + "/order/edit_server_rating.json";
	}

	/**
	 * get Entity Update Server comments
	 * 
	 * @return
	 */
	public static StringEntity getEntityUpdateServerComment(
			String access_token, int server_id, int server_rating_id,
			int rating, String comment) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("server_rating_id", server_rating_id);
				jObject.put("rating", rating);
				jObject.put("comment", comment);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to get chain unread message of My notification
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlUnreadMsg(String access_token, String chain_name) {

		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_GET_ALL_UNREAD_MSG);
			strUrl.append("access_token=" + access_token);
			strUrl.append(
					"&chain_name=" + URLEncoder.encode(chain_name, "UTF-8"))
					.toString().replace("+", "%20");
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * <<<<<<< HEAD Get URL to pay order
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlPayOrder() {
		try {
			StringBuffer strUrl = new StringBuffer(URL + KEY_PAY_ORDER);
			return strUrl.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * get Entity Pay Order
	 * 
	 * @return
	 */
	public static StringEntity getEntityPayOrder(String access_token,
			int order_id, int is_paid) {
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("access_token", access_token);
			jObject.put("order_id", order_id);
			jObject.put("is_paid", is_paid);
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Get URL to delete notification by restaurant
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlDeleteNotificationByRestaurant() {
		return URL + KEY_GET_DELETE_NOTIFICATION_BY_RESTAURANT;
	}

	/**
	 * get Entity delete notification by restaurant
	 * 
	 * @return
	 */
	public static StringEntity getEntityDeleteNotificationByRestaurant(
			String access_token, int message_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("message_id", message_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get URL to delete notification by restaurant
	 * 
	 * @return a link url from server to get database or put data to server
	 */
	public static String getUrlDeleteNotificationByMessage() {
		return URL + KEY_GET_DELETE_NOTIFICATION_BY_MESSAGE;
	}

	/**
	 * get Entity delete notification by message
	 * 
	 * @return
	 */
	public static StringEntity getEntityDeleteNotificationByMessage(
			String access_token, int message_id) {
		try {
			JSONObject jObject = new JSONObject();
			try {
				jObject.put("access_token", access_token);
				jObject.put("message_id", message_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new StringEntity(jObject.toString(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
}