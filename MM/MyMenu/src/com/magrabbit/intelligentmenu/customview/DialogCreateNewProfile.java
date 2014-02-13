package com.magrabbit.intelligentmenu.customview;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.control.RangeSeekBar;
import com.magrabbit.intelligentmenu.control.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.magrabbit.intelligentmenu.customview.DialogMenuType.ProcessDialogConfirm_Menu_Type;
import com.magrabbit.intelligentmenu.customview.DialogSaveProfile.ProcessDialogConfirm_SaveProfile;
import com.magrabbit.intelligentmenu.model.MenuTypeObject;
import com.magrabbit.intelligentmenu.model.MessageErrorEntity;
import com.magrabbit.intelligentmenu.model.RestaurantObject;
import com.magrabbit.intelligentmenu.model.SearchProfile;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * 
 * @author baonguyen
 * @Description : Dialog for users to create a new profile of searching or even
 *              modifying the old settings
 */
public class DialogCreateNewProfile extends BaseDialog implements
		OnClickListener {

	private ProcessDialogConfirm_CreateNewProfile mProcessDialogMove;
	// List of restaurants
	private List<RestaurantObject> mListRestaurants;
	private int mIsDefault;
	/**
	 * Customize Range SeekBar
	 */
	private RangeSeekBar<Integer> mSeekBarMenuItemPrice;
	private int mMaxMenuItemMoney = 100;
	private int mMinMenuItemMoney = 0;
	private RangeSeekBar<Integer> mSeekBarPoint;
	private int mMaxRewardPoint = 500;
	private int mMinRewardPoint = 0;
	private RangeSeekBar<Integer> mSeekBarSerRating;
	private int mMaxServerRating = 12;
	private int mMinServerRating = 0;
	private RangeSeekBar<Integer> mSeekBarResRating;
	private int mMaxRestaurantRating = 12;
	private int mMinRestaurantRating = 0;
	private RangeSeekBar<Integer> mSeekBarSearchDistance;
	private int mMaxSearchDistance = 100;
	private RangeSeekBar<Integer> mSeekBarMenuItemRating;
	private int mMaxMenuItemRating = 12;
	private int mMinMenuItemRating = 0;
	private List<String> mListValues;

	/**
	 * Layout elements to inflate from layout activity_advanced_search.xml
	 */
	private EditText mEdtMenuItems;
	private Button mBtnMultiSelect;
	private Button mBtnResStyle;
	private Button mBtnSaveProfile;
	private Button mBtnSearch;
	private Button mBtnCancel;
	private TextView mTvTitle;

	/**
	 * List of checkBox of MenuType and Restaurant Style, Menu Items to pass
	 * from saved profile
	 */
	private List<MenuTypeObject> mListMenuType;
	private List<MenuTypeObject> mListItemType;
	private List<String> mListMenuItems;
	private Context mContext;

	// Mode for editing profile
	private boolean mIsEditMode = false;
	private int mEditedProfilePosition;
	private boolean mIsFirstProfile;
	private List<SearchProfile> mListSearchProfiles;

	private String alphaAndDigits;

	/**
	 * 
	 * @param context
	 * @param mIsEditMode
	 *            : 1 for Editing mode and 0 for Creating mode
	 * @param mEditedProfilePosition
	 *            : position of edited profile
	 * @param mIsFirstProfile
	 *            : to check whether it is the first search profile or not
	 * @param listSearchProfiles
	 * @param mProcessDialogMove
	 *            : processing events
	 * @Description:Show dialog for user to create a new profile or just edit
	 *                   old profile
	 */
	public DialogCreateNewProfile(Context context, boolean mIsEditMode,
			int mEditedProfilePosition, boolean mIsFirstProfile,
			List<SearchProfile> listSearchProfiles,
			ProcessDialogConfirm_CreateNewProfile mProcessDialogMove) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.mContext = context;

		// Set variables for editing mode
		this.mIsEditMode = mIsEditMode;
		this.mEditedProfilePosition = mEditedProfilePosition;
		if (this.mListSearchProfiles == null) {
			this.mListSearchProfiles = new ArrayList<SearchProfile>();
		}
		this.mListSearchProfiles.clear();
		if (listSearchProfiles != null) {
			for (SearchProfile s : listSearchProfiles) {
				this.mListSearchProfiles.add(s.clone());
			}
		}

		// Check whether it is the first search profile or not
		this.mIsFirstProfile = mIsFirstProfile;
		this.mProcessDialogMove = mProcessDialogMove;

		// /** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_advanced_search);

		// Inflate layout elements from total design
		mBtnMultiSelect = (Button) findViewById(R.id.dialog_advanced_search_btn_menu_type);
		mBtnResStyle = (Button) findViewById(R.id.dialog_advanced_search_btn_food_type);
		mBtnSaveProfile = (Button) findViewById(R.id.dialog_advanced_search_bt_saveProfile);
		mBtnSearch = (Button) findViewById(R.id.dialog_advanced_search_bt_search);
		mBtnCancel = (Button) findViewById(R.id.dialog_advanced_search_bt_cancel);
		mTvTitle = (TextView) findViewById(R.id.dialog_advanced_search_header_title);
		if (this.mIsEditMode) {
			mTvTitle.setText("Edit MySearch Profile");
		}
		mBtnMultiSelect.setOnClickListener(this);
		mBtnResStyle.setOnClickListener(this);
		mBtnSaveProfile.setOnClickListener(this);
		mBtnSearch.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		/* Implement Seekbar for layout */

		// Avg. MenuItem Price Range Bar as Integer range between 0
		// and 100
		mSeekBarMenuItemPrice = new RangeSeekBar<Integer>(0, 100, mContext, 5);
		mListValues = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			mListValues.add("$" + i);
		}
		mSeekBarMenuItemPrice.setValue(mListValues);
		mSeekBarMenuItemPrice
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxMenuItemMoney = maxValue;
						mMinMenuItemMoney = minValue;
					}
				});
		LinearLayout layout1 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_menuItemPrice);
		layout1.addView(mSeekBarMenuItemPrice);

		// MyReward Points Range Bar as Integer range between 0 and 500
		mSeekBarPoint = new RangeSeekBar<Integer>(0, 500, mContext, 20);
		mListValues = new ArrayList<String>();
		for (int i = 0; i <= 500; i++) {
			mListValues.add(i + "pts");
		}
		mSeekBarPoint.setValue(mListValues);
		mSeekBarPoint
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxRewardPoint = maxValue;
						mMinRewardPoint = minValue;
					}
				});
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_MyRewardPoint);
		layout2.addView(mSeekBarPoint);

		// Restaurant Rating Range Bar as Integer range between 1(=F) and
		// 13(=A+)
		mSeekBarResRating = new RangeSeekBar<Integer>(0, 12, mContext, 0);
		mListValues = new ArrayList<String>();
		for (int i = 13; i >= 1; i--) {
			mListValues.add(Utils.exchangeRateGetExtra(i) + "");
		}
		mSeekBarResRating.setValue(mListValues);
		mSeekBarResRating
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxRestaurantRating = maxValue;
						mMinRestaurantRating = minValue;
					}
				});
		LinearLayout layout3 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_restaurantRating);
		layout3.addView(mSeekBarResRating);

		// Server Rating Range Bar as Integer range between 1(=F) and 13(=A+)
		mSeekBarSerRating = new RangeSeekBar<Integer>(0, 12, mContext, 0);
		mListValues = new ArrayList<String>();
		for (int i = 13; i >= 1; i--) {
			mListValues.add(Utils.exchangeRateGetExtra(i) + "");
		}
		mSeekBarSerRating.setValue(mListValues);
		mSeekBarSerRating
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxServerRating = maxValue;
						mMinServerRating = minValue;
					}
				});
		LinearLayout layout4 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_serverRating);
		layout4.addView(mSeekBarSerRating);

		// Search Distance Range Bar as Integer range between 0 and 100
		mSeekBarSearchDistance = new RangeSeekBar<Integer>(0, 100, mContext, 20);
		mSeekBarSearchDistance.setDrawMax(false);
		mListValues = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			mListValues.add(i + " mi");
		}
		mSeekBarSearchDistance.setValue(mListValues);
		mSeekBarSearchDistance
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxSearchDistance = minValue;
					}
				});
		LinearLayout layout5 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_searchDistance);
		layout5.addView(mSeekBarSearchDistance);
		mSeekBarSearchDistance.setSelectedMinValue(mMaxSearchDistance);
		// MenuItem Rating Range Bar as Integer range between 1(=F) and 13(=A+)
		mSeekBarMenuItemRating = new RangeSeekBar<Integer>(0, 12, mContext, 0);
		mListValues = new ArrayList<String>();
		for (int i = 13; i >= 1; i--) {
			mListValues.add(Utils.exchangeRateGetExtra(i) + "");
		}
		mSeekBarMenuItemRating.setValue(mListValues);
		mSeekBarMenuItemRating
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						mMaxMenuItemRating = maxValue;
						mMinMenuItemRating = minValue;
					}
				});
		LinearLayout layout6 = (LinearLayout) findViewById(R.id.dialog_seekbar_layout_menuItemRating);
		layout6.addView(mSeekBarMenuItemRating);

		// Initialize search profiles and add into the list

		// Inflate from layout
		mEdtMenuItems = (EditText) findViewById(R.id.dialog_advanced_search_edt_items);

		mListMenuItems = new ArrayList<String>();

		// Initialize list of MenuType
		if (mListMenuType == null) {
			mListMenuType = new ArrayList<MenuTypeObject>();
		}
		// Initialize list of ItemType
		if (mListItemType == null) {
			mListItemType = new ArrayList<MenuTypeObject>();
		}

		// Fill data of selected profile into RangeBar and Keyword textBox
		if (this.mEditedProfilePosition != -1) {
			setData_asSavedProfile(this.mEditedProfilePosition);
		}
		// Load data for MenuType and FoodType dialog check box
		new getMenuTypeTask().execute();
	}

	/**
	 * @Description: Destroy all list of objects to clear the cache memory
	 */
	@Override
	public void dismiss() {
		super.dismiss();
		if (mListMenuItems != null) {
			mListMenuItems.clear();
			mListMenuItems = null;
		}
		if (mListValues != null) {
			mListValues.clear();
			mListValues = null;
		}

		// Change button background when coming back to MySearchActivity
		mProcessDialogMove.click_ChangeButtonBackground();
	}

	public void resetData() {
		/* Reset RangleBar element */
		setSeekBar_MenuItem("0", "100");
		setSeekBar_MyRewardPoint("0", "500");
		setSeekBar_RestaurantRating("0", "12");
		setSeekBar_ServerRating("0", "12");
		setSeekBar_SearchDistance(100);
		setSeekBar_MenuItemRating("0", "12");

		mEdtMenuItems.setText("");
		mEdtMenuItems.requestFocus();
		// Reset lists of checkBox for MenuType Dialog
	}

	public void setData_asSavedProfile(int pos) {
		try {
			SearchProfile profile = mListSearchProfiles.get(pos);
			if (profile == null) {
				return;
			}
			/* Set RangleBar element */
			setSeekBar_MenuItem(
					Utils.parseStringIntoList(profile.getItemPrices()).get(0),
					Utils.parseStringIntoList(profile.getItemPrices()).get(1));
			setSeekBar_MenuItemRating(
					Utils.parseStringIntoList(profile.getItemRatings()).get(0),
					Utils.parseStringIntoList(profile.getItemRatings()).get(1));
			setSeekBar_MyRewardPoint(
					Utils.parseStringIntoList(profile.getPointOffered()).get(0),
					Utils.parseStringIntoList(profile.getPointOffered()).get(1));
			setSeekBar_RestaurantRating(
					Utils.parseStringIntoList(profile.getRestaurantRatings())
							.get(0),
					Utils.parseStringIntoList(profile.getRestaurantRatings())
							.get(1));
			setSeekBar_ServerRating(
					Utils.parseStringIntoList(profile.getServerRatings())
							.get(0),
					Utils.parseStringIntoList(profile.getServerRatings())
							.get(1));
			setSeekBar_SearchDistance(Integer.parseInt(profile.getRadius()));

			// Remove %20 from keyword due to transferring character space(" ")
			// into website, it become %20
			// String keywords = profile.getKeywords();
			// keywords = keywords.replaceAll("%20", " ");
			mEdtMenuItems.setText(profile.getKeywords());
			mEdtMenuItems.requestFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveNewProfile(String nameProfile) {
		try {
			if (Utils.isNetworkConnected(mContext)) {
				// Save a new profile
				// alphaAndDigits = mEdtMenuItems.getText().toString()
				// .replaceAll("[^a-zA-Z0-9, ]+", " ");
				// mEdtMenuItems.setText(alphaAndDigits);
				new saveProfileTask().execute(new String[] { nameProfile });
			} else {
				showToastMessage(mContext.getResources().getString(
						R.string.mess_error_network));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Set and Get functions for RangeBars */
	// SET
	public void setSeekBar_MenuItem(String min, String max) {
		mSeekBarMenuItemPrice.setSelectedMaxValue(Integer.parseInt(max));
		mSeekBarMenuItemPrice.setSelectedMinValue(Integer.parseInt(min));
		mMaxMenuItemMoney = Integer.parseInt(max);
		mMinMenuItemMoney = Integer.parseInt(min);
	}

	public void setSeekBar_MyRewardPoint(String min, String max) {
		mSeekBarPoint.setSelectedMaxValue(Integer.parseInt(max));
		mSeekBarPoint.setSelectedMinValue(Integer.parseInt(min));
		mMaxRewardPoint = Integer.parseInt(max);
		mMinRewardPoint = Integer.parseInt(min);
	}

	public void setSeekBar_RestaurantRating(String min, String max) {
		mSeekBarResRating.setSelectedMaxValue(13 - Integer.parseInt(min));
		mSeekBarResRating.setSelectedMinValue(13 - Integer.parseInt(max));
		mMaxRestaurantRating = 13 - Integer.parseInt(min);
		mMinRestaurantRating = 13 - Integer.parseInt(max);
	}

	public void setSeekBar_ServerRating(String min, String max) {
		mSeekBarSerRating.setSelectedMaxValue(13 - Integer.parseInt(min));
		mSeekBarSerRating.setSelectedMinValue(13 - Integer.parseInt(max));
		mMaxServerRating = 13 - Integer.parseInt(min);
		mMinServerRating = 13 - Integer.parseInt(max);
	}

	public void setSeekBar_SearchDistance(int max) {
		mSeekBarSearchDistance.setSelectedMinValue(max);
		mMaxSearchDistance = max;
	}

	public void setSeekBar_MenuItemRating(String min, String max) {
		mSeekBarMenuItemRating.setSelectedMaxValue(13 - Integer.parseInt(min));
		mSeekBarMenuItemRating.setSelectedMinValue(13 - Integer.parseInt(max));
		mMaxMenuItemRating = 13 - Integer.parseInt(min);
		mMinMenuItemRating = 13 - Integer.parseInt(max);
	}

	/**
	 * Show Toast message template
	 * 
	 * @param message
	 */
	void showToastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_advanced_search_bt_search:
			try {
				if (mMaxSearchDistance == 0) {
					showToastMessage(mContext
							.getString(R.string.dialog_save_profile_invalidate_distance));
				} else {
					if (Utils.isNetworkConnected(mContext)) {
						SearchProfile profile = new SearchProfile();

						/* Uncomment to get menu_type and item_type as "[1,2,3]" */
						String menuType = "";
						for (int i = 0; i < mListMenuType.size(); i++) {
							if (mListMenuType.get(i).mIsChecked) {
								menuType += mListMenuType.get(i).mID + ",";
							}
						}

						String itemType = "";
						for (int i = 0; i < mListItemType.size(); i++) {
							if (mListItemType.get(i).mIsChecked) {
								itemType += mListItemType.get(i).mID + ",";
							}
						}

						profile.setItemPrices(mMinMenuItemMoney + ","
								+ mMaxMenuItemMoney);
						profile.setItemRatings((13 - mMaxMenuItemRating) + ","
								+ (13 - mMinMenuItemRating));
						profile.setItemTypes(itemType);
						profile.setKeywords(mEdtMenuItems.getText().toString()
								.trim());
						profile.setMenuTypes(menuType);
						profile.setPointOffered(mMinRewardPoint + ","
								+ mMaxRewardPoint);
						profile.setRadius("" + mMaxSearchDistance);
						profile.setRestaurantRatings((13 - mMaxRestaurantRating)
								+ "," + (13 - mMinRestaurantRating));
						profile.setServerRatings((13 - mMaxServerRating) + ","
								+ (13 - mMinServerRating));

						// new
						// getRestaurantAdvancedSearchTask().execute(profile);
						mProcessDialogMove.click_Search(profile);
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case R.id.dialog_advanced_search_btn_menu_type:
			DialogMenuType dialog = new DialogMenuType(mContext, mListMenuType,
					mContext.getString(R.string.dialog_menu_type_title),
					new ProcessDialogConfirm_Menu_Type() {

						@Override
						public void click_Close(
								List<MenuTypeObject> listMenuType) {
							// Save choices
							mListMenuType.clear();
							mListMenuType.addAll(listMenuType);
						}
					});

			dialog.show();

			break;
		case R.id.dialog_advanced_search_bt_cancel:
			dismiss();
			break;
		case R.id.dialog_advanced_search_btn_food_type:
			DialogMenuType dialogRes = new DialogMenuType(mContext,
					mListItemType,
					mContext.getString(R.string.dialog_restaurant_style_title),
					new ProcessDialogConfirm_Menu_Type() {

						@Override
						public void click_Close(
								List<MenuTypeObject> listMenuType) {
							// Save choices
							mListItemType.clear();
							mListItemType.addAll(listMenuType);

						}
					});

			dialogRes.show();
			break;
		case R.id.dialog_advanced_search_bt_saveProfile:
			if (mMaxSearchDistance == 0) {
				showToastMessage(mContext
						.getString(R.string.dialog_save_profile_invalidate_distance));
			} else {
				DialogSaveProfile dialog_save_profile = new DialogSaveProfile(
						mContext, mIsEditMode, mEditedProfilePosition,
						mListSearchProfiles, mMaxSearchDistance,
						new ProcessDialogConfirm_SaveProfile() {

							@Override
							public void click_Cancel() {

							}

							@Override
							public void click_Save(String name) {
								if (Utils.isNetworkConnected(mContext)) {
									// Save into list of Search Profiles with
									// the
									// created name
									saveNewProfile(name);
									dismiss();
								} else {
									showToastMessage(mContext
											.getResources()
											.getString(
													R.string.mess_error_network));
								}
							}
						});
				dialog_save_profile.show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @Description: Turn off virtual keyboard when touch on outside of textBox
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	public class saveProfileTask extends
			AsyncTask<String, Void, MessageErrorEntity> {

		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected MessageErrorEntity doInBackground(String... params) {
			try {
				String name = params[0];

				List<Integer> menuType = new ArrayList<Integer>();
				for (int i = 0; i < mListMenuType.size(); i++) {
					if (mListMenuType.get(i).mIsChecked) {
						menuType.add(mListMenuType.get(i).mID);
					}
				}

				List<Integer> itemType = new ArrayList<Integer>();
				for (int i = 0; i < mListItemType.size(); i++) {
					if (mListItemType.get(i).mIsChecked) {
						itemType.add(mListItemType.get(i).mID);
					}
				}
				// Process special characters for input keyword
				// alphaAndDigits = alphaAndDigits.replaceAll(" ", "%20");
				// String keywords = alphaAndDigits;

				InputStream source = null;
				// Add a new profile
				if (!mIsEditMode) {
					// Set default for the first profile
					if (mIsFirstProfile) {
						mIsDefault = 1;
					} else {
						mIsDefault = 0;
					}
					source = Server.requestPost(ServerURL
							.getUrlAddSearchProfile(), ServerURL
							.getEntityAddMySearch(mContext,
									13 - mMaxRestaurantRating,
									13 - mMinRestaurantRating,
									mMinMenuItemMoney, mMaxMenuItemMoney,
									mMinRewardPoint, mMaxRewardPoint,
									13 - mMaxMenuItemRating,
									13 - mMinMenuItemRating,
									mMaxSearchDistance, itemType, menuType,
									mEdtMenuItems.getText().toString().trim(),
									13 - mMaxServerRating,
									13 - mMinServerRating, mIsDefault, name));

					// Add a new profile into local list
					// addNewProfile(name, itemType, menuType);
				} else {
					// Edit an old profile
					source = Server.requestPost(ServerURL
							.getUrlEditSearchProfile(), ServerURL
							.getEntityEditMySearch(
									mContext,
									mListSearchProfiles.get(
											mEditedProfilePosition).getId(),
									13 - mMaxRestaurantRating,
									13 - mMinRestaurantRating,
									mMinMenuItemMoney, mMaxMenuItemMoney,
									mMinRewardPoint, mMaxRewardPoint,
									13 - mMaxMenuItemRating,
									13 - mMinMenuItemRating,
									mMaxSearchDistance, itemType, menuType,
									mEdtMenuItems.getText().toString().trim(),
									13 - mMaxServerRating,
									13 - mMinServerRating, name));
					// edit old profile followed by mEditedProfilePosition
					editOldProfile(name, itemType, menuType);
				}

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MessageErrorEntity response = gson.fromJson(json,
						MessageErrorEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(MessageErrorEntity result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(mContext
							.getString(R.string.common_mess_save_error));
				} else {
					showToastMessage(mContext
							.getString(R.string.common_mess_save_success));
					int indexOfAddedProfile = mListSearchProfiles.size();
					mProcessDialogMove.click_Save(mListSearchProfiles,
							indexOfAddedProfile);
					dismiss();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

		}
	}

	public class getMenuTypeTask extends
			AsyncTask<Void, Void, MenuTypeObject[]> {

		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected MenuTypeObject[] doInBackground(Void... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlMenuType(mContext));

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MenuTypeObject[] response = gson.fromJson(json,
						MenuTypeObject[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MenuTypeObject[] result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
					return;
				}
				if (result != null) {
					if (mListMenuType == null) {
						mListMenuType = new ArrayList<MenuTypeObject>();
					}
					mListMenuType.clear();
					for (MenuTypeObject menuTypeObject : result) {
						mListMenuType.add(menuTypeObject);
					}
				}
				// Edit mode for Menu Type Dialog
				if (mIsEditMode) {
					List<Integer> mListTypeInteger = new ArrayList<Integer>();

					String menuType = mListSearchProfiles.get(
							mEditedProfilePosition).getMenuTypes();
					if (menuType != null && menuType.length() > 0) {
						mListTypeInteger = processItemMenuTypeNewest(menuType);
						if (mListTypeInteger != null) {
							for (int i = 0; i < mListMenuType.size(); i++) {
								for (int j = 0; j < mListTypeInteger.size(); j++) {
									if (mListMenuType.get(i).mID == mListTypeInteger
											.get(j)) {
										mListMenuType.get(i).mIsChecked = true;
									}
								}
							}
						}
					}
				}
				// Call service to get item types
				new getItemTypeTask().execute();
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
		}
	}

	public class getItemTypeTask extends
			AsyncTask<Void, Void, MenuTypeObject[]> {

		@Override
		protected MenuTypeObject[] doInBackground(Void... params) {
			try {

				InputStream source = Server.requestGet(ServerURL
						.getUrlItemType(mContext));

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MenuTypeObject[] response = gson.fromJson(json,
						MenuTypeObject[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MenuTypeObject[] result) {
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
					return;
				}
				if (result != null) {
					if (mListItemType == null) {
						mListItemType = new ArrayList<MenuTypeObject>();
					}
					mListItemType.clear();
					for (MenuTypeObject menuTypeObject : result) {
						mListItemType.add(menuTypeObject);
					}
				}
				// Edit mode
				if (mIsEditMode) {
					List<Integer> mListTypeInteger = new ArrayList<Integer>();
					String itemType = mListSearchProfiles.get(
							mEditedProfilePosition).getItemTypes();
					if (itemType != null && itemType.length() > 0) {
						// get list of integer from string ItemType "1,2" -->
						// [1,2]
						mListTypeInteger = processItemMenuTypeNewest(itemType);
						// check whether id of item type equals to value of item
						// type to set check
						if (mListTypeInteger != null) {
							for (int i = 0; i < mListItemType.size(); i++) {
								for (int j = 0; j < mListTypeInteger.size(); j++) {
									if (mListItemType.get(i).mID == mListTypeInteger
											.get(j)) {
										mListItemType.get(i).mIsChecked = true;
									}
								}
							}
						}
					}

				}
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

		}
	}

	/**
	 * @Description: parse string like "[1,3]" into list of Integer
	 * @param result
	 * @return
	 */
	public List<Integer> processItemMenuType(String result) {
		try {
			if (result == null || result.length() == 0) {
				return null;
			}
			// Remove 2 char "[" and "]" from the string
			result = result.substring(1, result.length() - 1);
			List<String> results = Utils.parseStringIntoList(result);
			List<Integer> mListTypes = new ArrayList<Integer>();
			for (String s1 : results) {
				mListTypes.add(Integer.parseInt(s1));
			}
			return mListTypes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @Description: parse string like "1,3" into list of Integer
	 * @param result
	 * @return
	 */
	public List<Integer> processItemMenuTypeNewest(String result) {
		try {
			if (result == null || result.length() == 0) {
				return null;
			}
			List<String> results = Utils.parseStringIntoList(result);
			List<Integer> mListTypes = new ArrayList<Integer>();
			for (String s1 : results) {
				mListTypes.add(Integer.parseInt(s1));
			}
			return mListTypes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public void editOldProfile(String name, List<Integer> itemType,
			List<Integer> menuType) {
		try {
			mListSearchProfiles.get(mEditedProfilePosition)
					.setNameProfile(name);
			mListSearchProfiles.get(mEditedProfilePosition)
					.setRestaurantRatings(
							(13 - mMaxRestaurantRating) + ","
									+ (13 - mMinRestaurantRating));
			mListSearchProfiles.get(mEditedProfilePosition).setItemPrices(
					mMinMenuItemMoney + "," + mMaxMenuItemMoney);
			mListSearchProfiles.get(mEditedProfilePosition).setPointOffered(
					mMinRewardPoint + "," + mMaxRewardPoint);
			mListSearchProfiles.get(mEditedProfilePosition)
					.setItemRatings(
							(13 - mMaxMenuItemRating) + ","
									+ (13 - mMinMenuItemRating));
			mListSearchProfiles.get(mEditedProfilePosition).setRadius(
					"" + mMaxSearchDistance);
			mListSearchProfiles.get(mEditedProfilePosition).setItemTypes(
					Utils.listIntToString(itemType));
			mListSearchProfiles.get(mEditedProfilePosition).setMenuTypes(
					Utils.listIntToString(menuType));
			mListSearchProfiles.get(mEditedProfilePosition).setKeywords(
					mEdtMenuItems.getText().toString().trim());
			mListSearchProfiles.get(mEditedProfilePosition).setServerRatings(
					(13 - mMaxServerRating) + "," + (13 - mMinServerRating));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addNewProfile(String name, List<Integer> itemType,
			List<Integer> menuType) {
		SearchProfile search = new SearchProfile();
		search.setNameProfile(name);
		search.setRestaurantRatings(mMinRestaurantRating + ","
				+ mMaxRestaurantRating);
		search.setItemPrices(mMinMenuItemMoney + "," + mMaxMenuItemMoney);
		search.setPointOffered(mMinRewardPoint + "," + mMaxRewardPoint);
		search.setItemRatings(mMinMenuItemRating + "," + mMaxMenuItemRating);
		search.setRadius("" + mMaxSearchDistance);
		search.setItemTypes(Utils.listIntToString(itemType));
		search.setMenuTypes(Utils.listIntToString(menuType));
		search.setKeywords(mEdtMenuItems.getText().toString().trim());
		search.setServerRatings(mMinServerRating + "," + mMaxServerRating);
		mListSearchProfiles.add(search);
	}

	public static abstract class ProcessDialogConfirm_CreateNewProfile {

		public abstract void click_Search(SearchProfile searchprofile);

		public abstract void click_Save(List<SearchProfile> listSearchProfiles,
				int indexOfAddedProfile);

		public abstract void click_ChangeButtonBackground();
	}
}
