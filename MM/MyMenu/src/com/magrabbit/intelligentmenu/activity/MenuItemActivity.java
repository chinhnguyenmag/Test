package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.HorizontalListView;
import com.magrabbit.intelligentmenu.adapter.ItemKeyAdapter;
import com.magrabbit.intelligentmenu.adapter.PagerCustomGridAdapter;
import com.magrabbit.intelligentmenu.adapter.PagerCustomHorizontalGridAdapter;
import com.magrabbit.intelligentmenu.adapter.ViewPageAdapter;
import com.magrabbit.intelligentmenu.control.QuickAction;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacks;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacks.ProcessMenuFeedBack;
import com.magrabbit.intelligentmenu.customview.DialogMenu;
import com.magrabbit.intelligentmenu.customview.DialogMenu.ProcessDialogMenu;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogRating;
import com.magrabbit.intelligentmenu.customview.DialogRating.ProcessDialogConfirm_Rating;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.MenuItemStatusListener;
import com.magrabbit.intelligentmenu.listener.MenuItemThumbnailListener;
import com.magrabbit.intelligentmenu.listener.MyOrderListener;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.ActionItemObject;
import com.magrabbit.intelligentmenu.model.AddFavoriteItemEntity;
import com.magrabbit.intelligentmenu.model.AddNextTimeEntity;
import com.magrabbit.intelligentmenu.model.CategoryMenuEntity;
import com.magrabbit.intelligentmenu.model.CategoryObject;
import com.magrabbit.intelligentmenu.model.GridItems;
import com.magrabbit.intelligentmenu.model.ItemKeyObject;
import com.magrabbit.intelligentmenu.model.LocationEntity;
import com.magrabbit.intelligentmenu.model.MenuItem;
import com.magrabbit.intelligentmenu.model.MenuItemCommentEntity;
import com.magrabbit.intelligentmenu.model.MenuItemEntity;
import com.magrabbit.intelligentmenu.model.MenuItemOrderEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.ParcelableIndexCategoryAndMenuItem;
import com.magrabbit.intelligentmenu.model.SearchMenuItemEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.AnimationControl;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;
import com.viewpagerindicator.LinePageIndicator;

/**
 * @author Vu Le
 * @Description This activity use to show all item of menu, user can order,
 *              favorite,rating and share information to friends
 * 
 */
public class MenuItemActivity extends FragmentActivity implements
		OnClickListener, OnPageChangeListener, OptionMenuClickListener,
		MenuItemThumbnailListener, MenuItemStatusListener, MyOrderListener,
		OnTouchListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private EditText mEtOrderNumber;
	private TextView mTvnRateScore;
	private TextView mTvNumberReview;
	private TextView mTvSpecialMessage;
	private TextView mTvNameFood;
	private TextView mTvPrice;
	private TextView mTvFoodInfo;
	private TextView mTvCalory;
	private TextView mTvPoint;
	private TextView mTvItemKey;
	private ImageView mIvLogoRestaurant;
	private TextView mBtMenu;
	public static ViewPager mVpPhotos;
	private Button mBtFavorite;
	private Button mBtAddOder;
	private Button mBtShare;
	private Button mBtRating;
	public static LinePageIndicator mCirIndicator;
	private RelativeLayout mRlSpeciallEvent;
	private LinearLayout mLnLayoutAddOrder;
	private LinearLayout mLnLayoutNumberOrder;
	private ImageButton mIbTryNextTime;
	private boolean mIsShowThumbnail = false;
	private List<ResolveInfo> mActivities;
	private ImageLoader mImageLoader;
	private int mIsSearchMenuItem = -1;// 1 true.
	private RelativeLayout mRlGroupFeature;
	private String mKeySearchMenuItem;
	private DialogMenu dialogMenu;
	private DialogCustomerFeedbacks mDLogCustomerFeedbacks;
	private QuickAction quickAction;
	private String mRestaurantName;
	private RelativeLayout mRlLandNameBottom;
	private boolean mShowBio = false;
	private LinearLayout mSlBio;
	private AnimationControl mAnimation = new AnimationControl();
	private Button mBtCloseInfor;
	private TextView mTvBioInformation;
	private TextView mTvBioTitle;

	/**
	 * This dialog rating,it's will show when user touch on rating button
	 */
	private DialogRating mDialogRating;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	/**
	 * This is adapter to set data for view pager
	 */
	public static ViewPageAdapter mViewAdapter;
	private List<GridMenuItemFragment> mGridFragments;
	/**
	 * A dialog showing a progress indicator and an optional text message or
	 * view.
	 */
	private ProgressDialog mProgressDialog;
	private RelativeLayout mRlSliderTop;
	private RelativeLayout mRlSliderBottom;
	// =============================================================
	private PagerCustomGridAdapter mPagerAdapter;

	// =============================================================
	private int mIdCategory;
	private ArrayList<String> mListMenuItemPhotos;
	private int mOrientation;
	private List<ItemKeyObject> mListItemKeys;
	private int mPosition = 0;
	private TimerTask mTimerTask;
	private Handler mHandler = new Handler();
	private int mCountTime = 0;
	private Timer mTimer = new Timer();
	private List<ParcelableIndexCategoryAndMenuItem> mIndexs;
	private ArrayList<String> mTextMatchLists;
	private ArrayList<GridItems> mItmLsts;
	private String mLogo;
	private List<GridFragmentMenuItemHorizontal> mGridFragmentHorizontal;
	private PagerCustomHorizontalGridAdapter mmPagerThumbnailHorizontalAdapter;
	private List<MenuItem> mListSearchMenuItem;
	private List<CategoryObject> mListCategoryObject;
	private List<MenuItem> mListMenuItem;
	private boolean isFavorite = false;
	private boolean isNexttime = false;
	private boolean mIsChangeOrder = false;
	private int mCurrentOrderId;
	private int mCurrentQuantity;
	private GestureDetector gestureDetector;

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	private String mWebsite = "";

	// For showing status and point number in MyAccount Setting
	private String mDinnerStatus;
	private float mPointNumber;
	private int mLocationId;
	private int mIdItem;
	private String mNameCategory;
	private boolean isActivityFavorite;
	// For updating menu item comment and rating
	private boolean mIsUpdateRestaurantRating = false;

	// For showing Item Key horizontal list view
	private HorizontalListView mHorizontalListViewItemKey;
	private ItemKeyAdapter mItemKeyListViewAdapter;
	private float mTax;
	private Button mBtOrderMinus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOrientation = getScreenOrientation();
		mImageLoader = new ImageLoader(this);
		gestureDetector = new GestureDetector(this, new GestureListener());
		if (null != getIntent().getExtras()) {
			mIsSearchMenuItem = getIntent().getExtras().getInt(
					StringExtraUtils.KEY_CATEGORY_FROM);
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				try {
					if (Utils.isNetworkConnected(MenuItemActivity.this)) {
						mLogo = getIntent().getExtras().getString(
								StringExtraUtils.KEY_LOGO_RESTAURANT);
						mWebsite = getIntent().getExtras().getString(
								StringExtraUtils.KEY_SHARELOCATION_WEBSITE);
						mDinnerStatus = getIntent()
								.getExtras()
								.getString(
										StringExtraUtils.KEY_SHARELOCATION_DINNERSTATUS);
						mPointNumber = getIntent().getExtras().getFloat(
								StringExtraUtils.KEY_SHARELOCATION_POINTNUMBER);
						mLocationId = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_LOCATION);
						mKeySearchMenuItem = getIntent().getExtras().getString(
								StringExtraUtils.KEY_SEARCH_MENU_ITEM);
						mTax = getIntent().getExtras().getFloat(
								StringExtraUtils.KEY_TAX);
						mRestaurantName = getIntent().getExtras().getString(
								StringExtraUtils.KEY_RESTAURANT_NAME);

						if (mListSearchMenuItem == null) {
							mListSearchMenuItem = new ArrayList<MenuItem>();
						}
						SearchMenuItemEntity searchMenuItemEntity = (SearchMenuItemEntity) getIntent()
								.getSerializableExtra("ItemSearch");
						new SearchMenuItemTask()
								.execute(new SearchMenuItemEntity[] { searchMenuItemEntity });
					} else {
						Toast.makeText(this, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT) {
				try {
					if (Utils.isNetworkConnected(MenuItemActivity.this)) {
						mIdCategory = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_CATEGORY);
						mLogo = getIntent().getExtras().getString(
								StringExtraUtils.KEY_LOGO_RESTAURANT);
						mWebsite = getIntent().getExtras().getString(
								StringExtraUtils.KEY_SHARELOCATION_WEBSITE);
						mDinnerStatus = getIntent()
								.getExtras()
								.getString(
										StringExtraUtils.KEY_SHARELOCATION_DINNERSTATUS);
						mPointNumber = getIntent().getExtras().getFloat(
								StringExtraUtils.KEY_SHARELOCATION_POINTNUMBER);
						mLocationId = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_LOCATION);
						mNameCategory = getIntent().getExtras().getString(
								StringExtraUtils.KEY_NAME_CATEGORY);

						mRestaurantName = getIntent().getExtras().getString(
								StringExtraUtils.KEY_RESTAURANT_NAME);
						isFavorite = getIntent().getExtras().getBoolean(
								StringExtraUtils.KEY_IS_FAVORITE, false);
						isNexttime = getIntent().getExtras().getBoolean(
								StringExtraUtils.KEY_IS_NEXTTIME, false);
						mTax = getIntent().getExtras().getFloat(
								StringExtraUtils.KEY_TAX);
						new MenuItemTask().execute();
					} else {
						Toast.makeText(this, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				try {
					if (Utils.isNetworkConnected(MenuItemActivity.this)) {
						mIdCategory = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_CATEGORY);
						mLogo = getIntent().getExtras().getString(
								StringExtraUtils.KEY_LOGO_RESTAURANT);
						mLocationId = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_LOCATION);
						mIdItem = getIntent().getExtras().getInt(
								StringExtraUtils.KEY_ID_ITEM);
						mNameCategory = getIntent().getExtras().getString(
								StringExtraUtils.KEY_NAME_CATEGORY);

						if (mListSearchMenuItem == null) {
							mListSearchMenuItem = new ArrayList<MenuItem>();
						}
						if (mListCategoryObject == null) {
							mListCategoryObject = new ArrayList<CategoryObject>();
						}
						new GetLocationTask().execute(mLocationId);
					} else {
						Toast.makeText(this, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			mIdCategory = -1;
			mLogo = "";
		}

	}

	/**
	 * Set event swipe for panel.
	 * 
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

	/**
	 * Setup ui for LandScape
	 */
	public void initUiThumbnailForLandScape() {
		setContentView(R.layout.activity_details_restaurant_thumbnail_land);
		mVpPhotos = (ViewPager) findViewById(R.id.details_restaurant_thumbnail_land_vp_photos);
		mCirIndicator = (LinePageIndicator) findViewById(R.id.details_restaurant_thumbnail_land_indicator);
		// ===========================================================================
	}

	/**
	 * @Description When the user change screen to landscape, it will show by
	 *              horizontal screen
	 */
	private void initUiForLandScape(int position) {
		setContentView(R.layout.menu_item_land);
		try {
			mListMenuItemPhotos.clear();
			if (mIsShowThumbnail) {
				position = 0;
			}

			mVpPhotos = (ViewPager) findViewById(R.id.menu_item_land_view_pager);
			mCirIndicator = (LinePageIndicator) findViewById(R.id.menu_item_land_indicator);
			mBtFavorite = (Button) findViewById(R.id.menu_item_land_favorite);
			mBtAddOder = (Button) findViewById(R.id.menu_item_land_add_order);
			mBtRating = (Button) findViewById(R.id.menu_item_land_rate);
			mBtShare = (Button) findViewById(R.id.menu_item_land_share);
			mTvNameFood = (TextView) findViewById(R.id.menu_item_land_land_name_food);
			mTvnRateScore = (TextView) findViewById(R.id.menu_item_land_tv_rate_score);
			mTvNumberReview = (TextView) findViewById(R.id.menu_item_land_land_tv_number_review);

			mLnLayoutAddOrder = (LinearLayout) findViewById(R.id.menu_item_land_layout_order);
			mLnLayoutAddOrder.setVisibility(View.GONE);
			mLnLayoutNumberOrder = (LinearLayout) findViewById(R.id.menu_item_land_layout_order_number);
			mLnLayoutNumberOrder.setVisibility(View.GONE);
			mEtOrderNumber = (EditText) findViewById(R.id.menu_item_land_edt_order);
			mBtOrderMinus = (Button) findViewById(R.id.menu_item_land_bt_order_minus);
			mRlSliderTop = (RelativeLayout) findViewById(R.id.details_restaurant_slider_top);
			mRlSliderBottom = (RelativeLayout) findViewById(R.id.details_restaurant_slider_bottom);
			mRlLandNameBottom = (RelativeLayout) findViewById(R.id.relativeLayout1);
			// showDialogInputNumberOrder();
			mRlSliderTop.setVisibility(View.VISIBLE);
			mRlSliderBottom.setVisibility(View.VISIBLE);
			mRlLandNameBottom.setVisibility(View.VISIBLE);
			mBtShare.setVisibility(View.VISIBLE);
			mBtAddOder.setVisibility(View.VISIBLE);
			mEtOrderNumber.setFocusable(false);
			if (quickAction != null) {
				quickAction.dismiss();
			}

			commonButtonFunction();
			Typeface type = Typeface.createFromAsset(getAssets(),
					"fonts/amrtypen.ttf");

			mTvnRateScore.setTypeface(type);
			mTvNumberReview.setTypeface(type);

			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				mListMenuItemPhotos.clear();
				for (MenuItem data : mListSearchMenuItem) {
					if (data.getImage() != null && data.getImage().size() != 0) {
						mListMenuItemPhotos.add(data.getImage().get(0).mImage);
					} else {
						mListMenuItemPhotos.add("");
					}
				}

				if (mListSearchMenuItem.get(position).getReview() != 0) {
					mTvnRateScore.setVisibility(View.VISIBLE);
					mTvnRateScore.setText(Utils.exchangeRateGetExtra(Float
							.parseFloat(mListSearchMenuItem.get(position)
									.getRating())));
					if (mListSearchMenuItem.get(position).getReview() > 1) {
						mTvNumberReview.setText(mListSearchMenuItem.get(
								position).getReview()
								+ " Reviews");
					} else {
						mTvNumberReview.setText(mListSearchMenuItem.get(
								position).getReview()
								+ " Review");
					}
				} else {
					mTvnRateScore.setVisibility(View.GONE);
					mTvnRateScore.setText("");
					mTvNumberReview.setText("");
				}

				mTvNameFood
						.setText(mListSearchMenuItem.get(position).getName());
				if (mListSearchMenuItem.get(position).isFavorite() == 1) {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
				} else {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
				}

				if (mListSearchMenuItem.get(position).getQuantity() == 0) {
					mBtAddOder
							.setBackgroundResource(R.drawable.menuitem_button_order_plus);
					mBtAddOder.setText("");
				} else {
					mBtAddOder.setBackgroundResource(R.drawable.circle_number);
					mBtAddOder.setText(mListSearchMenuItem.get(position)
							.getQuantity() + "");
				}

				if (mListSearchMenuItem.get(position).getStatus() != 1) {

					mBtOrderMinus.setVisibility(View.VISIBLE);
					mBtOrderMinus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onClick_Order_Minus();
						}
					});
				} else {
					mBtOrderMinus.setVisibility(View.GONE);
				}

			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				mListMenuItemPhotos.clear();
				for (MenuItem data : mListMenuItem) {
					if (data.getImage() != null && data.getImage().size() != 0) {
						mListMenuItemPhotos.add(data.getImage().get(0).mImage);
					} else {
						mListMenuItemPhotos.add("");
					}
				}

				if (mListMenuItem.get(position).getStatus() != 1) {

					mBtOrderMinus.setVisibility(View.VISIBLE);
					mBtOrderMinus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onClick_Order_Minus();
						}
					});
				} else {
					mBtOrderMinus.setVisibility(View.GONE);
				}

				if (mListMenuItem.get(position).getReview() != 0) {
					mTvnRateScore.setVisibility(View.VISIBLE);
					mTvnRateScore
							.setText(Utils.exchangeRateGetExtra(Float
									.parseFloat(mListMenuItem.get(position)
											.getRating())));
					if (mListMenuItem.get(position).getReview() > 1) {
						mTvNumberReview.setText(mListMenuItem.get(position)
								.getReview() + " Reviews");
					} else {
						mTvNumberReview.setText(mListMenuItem.get(position)
								.getReview() + " Review");
					}
				} else {
					mTvnRateScore.setVisibility(View.GONE);
					mTvnRateScore.setText("");
					mTvNumberReview.setText("");
				}

				mTvNameFood.setText(mListMenuItem.get(position).getName());
				if (mListMenuItem.get(position).isFavorite() == 1) {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
				} else {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
				}

				if (mListMenuItem.get(position).getQuantity() == 0) {
					mBtAddOder
							.setBackgroundResource(R.drawable.menuitem_button_order_plus);
					mBtAddOder.setText("");
				} else {
					mBtAddOder.setBackgroundResource(R.drawable.circle_number);
					mBtAddOder.setText(mListMenuItem.get(position)
							.getQuantity() + "");
				}
			}
			mViewAdapter = new ViewPageAdapter(MenuItemActivity.this,
					mListMenuItemPhotos, false);
			mVpPhotos.setAdapter(mViewAdapter);
			mVpPhotos.setOnTouchListener(this);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(position);
			mCirIndicator.setOnPageChangeListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description It will show by vertical screen
	 */
	private void initUiForPortrait() {

		setContentView(R.layout.activity_menu_item);
		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, true, true, true, true, true, true,
				this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		mVpPhotos = (ViewPager) findViewById(R.id.menu_item_view_pager);
		mIvLogoRestaurant = (ImageView) findViewById(R.id.menu_item_logo_restaurant);
		mTvnRateScore = (TextView) findViewById(R.id.menu_item_tv_rate_score);
		mTvNumberReview = (TextView) findViewById(R.id.menu_item_tv_number_review);
		mTvSpecialMessage = (TextView) findViewById(R.id.menu_item_tv_special_message);
		mTvNameFood = (TextView) findViewById(R.id.menu_item_namefood);
		mTvPrice = (TextView) findViewById(R.id.menu_item_price);
		mTvFoodInfo = (TextView) findViewById(R.id.menu_item_food_info);
		mTvCalory = (TextView) findViewById(R.id.menu_item_Calories);
		mTvPoint = (TextView) findViewById(R.id.menu_item_point);
		mTvCalory = (TextView) findViewById(R.id.menu_item_Calories);
		mCirIndicator = (LinePageIndicator) findViewById(R.id.menu_item_indicator);
		mBtFavorite = (Button) findViewById(R.id.menu_item_favorite);
		mBtAddOder = (Button) findViewById(R.id.menu_item_add_order);
		mBtRating = (Button) findViewById(R.id.menu_item_rate);
		mBtShare = (Button) findViewById(R.id.menu_item_share);
		mBtMenu = (Button) findViewById(R.id.menu_item_btn_menu);
		mHorizontalListViewItemKey = (HorizontalListView) findViewById(R.id.menu_item_gallary_item_key);
		mTvItemKey = (TextView) findViewById(R.id.menu_item_tv_itemkey);
		mRlSpeciallEvent = (RelativeLayout) findViewById(R.id.menu_item_special_message);
		mIbTryNextTime = (ImageButton) findViewById(R.id.menu_item_next_time);
		mRlSliderTop = (RelativeLayout) findViewById(R.id.menu_item_slider_top);
		mRlGroupFeature = (RelativeLayout) findViewById(R.id.menu_item_rl_group);
		mLnLayoutAddOrder = (LinearLayout) findViewById(R.id.menu_item_layout_order);
		mLnLayoutAddOrder.setVisibility(View.GONE);
		mLnLayoutNumberOrder = (LinearLayout) findViewById(R.id.menu_item_layout_order_number);
		mLnLayoutNumberOrder.setVisibility(View.GONE);
		mEtOrderNumber = (EditText) findViewById(R.id.menu_item_edt_order);
		mBtOrderMinus = (Button) findViewById(R.id.menu_item_bt_order_minus);
		mSlBio = (LinearLayout) findViewById(R.id.menu_item_ll_infomation);
		mBtCloseInfor = (Button) findViewById(R.id.menu_item_bt_close_information);
		mTvBioInformation = (TextView) findViewById(R.id.menu_item_tv_information);
		mTvBioTitle = (TextView) findViewById(R.id.menu_item_tv_title_information);
		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/amrtypen.ttf");
		mTvBioInformation.setTypeface(type);
		mTvBioTitle.setTypeface(type);
		mTvFoodInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTvFoodInfo.getText().toString().trim().length() > 60) {
					onClick_FoodInformation();
				}
			}
		});

		mBtCloseInfor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_FoodInformation();
			}
		});

		// showDialogInputNumberOrder();
		mEtOrderNumber.setFocusable(false);

		mBtMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_Menu();
			}
		});

		commonButtonFunction();

		mTvnRateScore.setTypeface(type);
		mTvNumberReview.setTypeface(type);
		mImageLoader.DisplayImage(ServerURL.URL + mLogo, mIvLogoRestaurant);

		mListMenuItemPhotos.clear();
		if (mIsSearchMenuItem != -1
				&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
			mListMenuItemPhotos.clear();
			for (MenuItem data : mListSearchMenuItem) {
				if (data.getImage() != null && data.getImage().size() != 0) {
					mListMenuItemPhotos.add(data.getImage().get(0).mImage);
				} else {
					mListMenuItemPhotos.add("");
				}
			}
			if (mListSearchMenuItem.get(mPosition).getReview() != 0) {
				mTvnRateScore.setVisibility(View.VISIBLE);
				mTvnRateScore.setText(Utils.exchangeRateGetExtra(Float
						.parseFloat(mListSearchMenuItem.get(mPosition)
								.getRating())));
				if (mListSearchMenuItem.get(mPosition).getReview() > 1) {
					mTvNumberReview.setText(mListSearchMenuItem.get(mPosition)
							.getReview() + " Reviews");
				} else {
					mTvNumberReview.setText(mListSearchMenuItem.get(mPosition)
							.getReview() + " Review");
				}
			} else {
				mTvnRateScore.setVisibility(View.GONE);
				mTvnRateScore.setText("");
				mTvNumberReview.setText("");
			}
			mTvSpecialMessage.setText(mListSearchMenuItem.get(mPosition)
					.getSpecialMesssage());
			mTvNameFood.setText(mListSearchMenuItem.get(mPosition).getName());
			mTvPrice.setText("$ "
					+ Utils.formatNumber(mListSearchMenuItem.get(mPosition)
							.getPrice(), "0.00"));

			mTvFoodInfo.setText(mListSearchMenuItem.get(mPosition)
					.getDescription());

			mTvBioInformation.setText(mListSearchMenuItem.get(mPosition)
					.getDescription());

			mTvCalory.setText(""
					+ (int) mListSearchMenuItem.get(mPosition).getCalories());
			mTvPoint.setText(" "
					+ mListSearchMenuItem.get(mPosition).getReward_point());
			mBtMenu.setText(mListSearchMenuItem.get(mPosition)
					.getCategoryName());
			try {
				if (mListSearchMenuItem.get(mPosition).isTryNextTime() == 1) {
					mIbTryNextTime.setVisibility(View.VISIBLE);
				} else {
					mIbTryNextTime.setVisibility(View.GONE);
				}
				if (mListSearchMenuItem.get(mPosition).isFavorite() == 1) {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
				} else {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
				}
				if (mListSearchMenuItem.get(mPosition).getQuantity() == 0) {
					mBtAddOder
							.setBackgroundResource(R.drawable.menuitem_button_order_plus);
					mBtAddOder.setText("");
				} else {
					mBtAddOder.setBackgroundResource(R.drawable.circle_number);
					mBtAddOder.setText(mListSearchMenuItem.get(mPosition)
							.getQuantity() + "");
				}

				if (mListSearchMenuItem.get(mPosition).getSpecialMesssage()
						.equals("")) {
					mRlSpeciallEvent.setVisibility(View.GONE);
				} else if (!mListSearchMenuItem.get(mPosition)
						.getSpecialMesssage().trim().equals("")) {
					mRlSpeciallEvent.setVisibility(View.VISIBLE);
				}
				if (mListSearchMenuItem.get(mPosition).getStatus() != 1) {
					mBtOrderMinus.setVisibility(View.VISIBLE);
					mBtOrderMinus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onClick_Order_Minus();
						}
					});
				} else {
					mBtOrderMinus.setVisibility(View.GONE);
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			// ==========================================================================
		} else if (mIsSearchMenuItem != -1
				&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
				|| mIsSearchMenuItem != -1
				&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
			mListMenuItemPhotos.clear();
			for (MenuItem data : mListMenuItem) {
				if (data.getImage() != null && data.getImage().size() != 0) {
					mListMenuItemPhotos.add(data.getImage().get(0).mImage);
				} else {
					mListMenuItemPhotos.add("");
				}
			}

			if (mListMenuItem.get(mPosition).getReview() != 0) {
				mTvnRateScore.setVisibility(View.VISIBLE);
				mTvnRateScore.setText(Utils.exchangeRateGetExtra(Float
						.parseFloat(mListMenuItem.get(mPosition).getRating())));
				if (mListMenuItem.get(mPosition).getReview() > 1) {
					mTvNumberReview.setText(mListMenuItem.get(mPosition)
							.getReview() + " Reviews");
				} else {
					mTvNumberReview.setText(mListMenuItem.get(mPosition)
							.getReview() + " Review");
				}
			} else {
				mTvnRateScore.setVisibility(View.GONE);
				mTvnRateScore.setText("");
				mTvNumberReview.setText("");
			}

			mTvSpecialMessage.setText(mListMenuItem.get(mPosition)
					.getSpecialMesssage());
			mTvNameFood.setText(mListMenuItem.get(mPosition).getName());
			mTvPrice.setText("$ "
					+ Utils.formatNumber(mListMenuItem.get(mPosition)
							.getPrice(), "0.00"));

			mTvFoodInfo.setText(mListMenuItem.get(mPosition).getDescription());

			mTvBioInformation.setText(mListMenuItem.get(mPosition)
					.getDescription());
			mTvCalory.setText(""
					+ (int) mListMenuItem.get(mPosition).getCalories());
			mTvPoint.setText(" "
					+ mListMenuItem.get(mPosition).getReward_point());
			mBtMenu.setText(mNameCategory);

			if (mListMenuItem.get(mPosition).getStatus() != 1) {
				mBtOrderMinus.setVisibility(View.VISIBLE);
				mBtOrderMinus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onClick_Order_Minus();
					}
				});
			} else {
				mBtOrderMinus.setVisibility(View.GONE);
			}
			try {
				if (mListMenuItem.get(mPosition).isTryNextTime() == 1) {
					mIbTryNextTime.setVisibility(View.VISIBLE);
				} else {
					mIbTryNextTime.setVisibility(View.GONE);
				}
				if (mListMenuItem.get(mPosition).isFavorite() == 1) {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
				} else {
					mBtFavorite
							.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
				}
				if (mListMenuItem.get(mPosition).getQuantity() == 0) {
					mBtAddOder
							.setBackgroundResource(R.drawable.menuitem_button_order_plus);
					mBtAddOder.setText("");
				} else {
					mBtAddOder.setBackgroundResource(R.drawable.circle_number);
					mBtAddOder.setText(mListMenuItem.get(mPosition)
							.getQuantity() + "");
				}

				if (mListMenuItem.get(mPosition).getSpecialMesssage()
						.equals("")) {
					mRlSpeciallEvent.setVisibility(View.GONE);
				} else if (!mListMenuItem.get(mPosition).getSpecialMesssage()
						.trim().equals("")) {
					mRlSpeciallEvent.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		mHorizontalListViewItemKey
				.setOnItemClickListener(new OnItemClickListener() {
					View previous = null;

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int pos, long id) {
						mTvItemKey.setText(mListItemKeys.get(pos)
								.getDescription());

						ActionItemObject nextItem = new ActionItemObject(
								mListItemKeys.get(pos).getDescription());

						quickAction = new QuickAction(MenuItemActivity.this,
								QuickAction.VERTICAL);
						quickAction.addActionItem(nextItem);

						quickAction.show(view);
						quickAction.setAnimStyle(QuickAction.ANIM_REFLECT);

						if (mTimerTask != null) {
							mTimerTask.cancel();
							mTimerTask = null;
							mCountTime = 0;
						}
						viewItemkey();

						if (previous == view) {
							view.setBackgroundResource(R.drawable.bg_gridview_selector);
						} else {
							view.setBackgroundResource(R.drawable.bg_gridview_selector);
							if (previous != null)
								previous.setBackgroundResource(0);
							previous = view;
						}

					}
				});
		mViewAdapter = new ViewPageAdapter(MenuItemActivity.this,
				mListMenuItemPhotos, true);
		mVpPhotos.setAdapter(mViewAdapter);
		mCirIndicator.setViewPager(mVpPhotos);
		mCirIndicator.setOnPageChangeListener(this);
		mCirIndicator.setCurrentItem(mPosition);

		// It will show thumbnail when the user click on viewpager
		mVpPhotos.setOnTouchListener(this);

		new LoadGalleryTask().execute(mPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen for landscape and portrait
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (mIsShowThumbnail) {
				initUiThumbnailForLandScape();
				new LoadViewThumbnailHorizontal().execute();
			} else {
				initUiForLandScape(mPosition);
			}
			mOrientation = Configuration.ORIENTATION_LANDSCAPE;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (mIsShowThumbnail) {
				initUiForPortrait();
				new LoadViewThumbnail().execute();
			} else {
				initUiForPortrait();
			}
			mOrientation = Configuration.ORIENTATION_PORTRAIT;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mGridFragments != null) {
			mGridFragments.clear();
			mGridFragments = null;
		}

		if (mActivities != null) {
			mActivities.clear();
			mActivities = null;
		}

		if (mListItemKeys != null) {
			mListItemKeys.clear();
			mListItemKeys = null;
		}

		if (mIndexs != null) {
			mIndexs.clear();
			mIndexs = null;
		}

		if (mTextMatchLists != null) {
			mTextMatchLists.clear();
			mTextMatchLists = null;
		}

		if (mItmLsts != null) {
			mItmLsts.clear();
			mItmLsts = null;
		}

		if (mListMenuItemPhotos != null) {
			mListMenuItemPhotos.clear();
			mListMenuItemPhotos = null;
		}
		if (mImageLoader != null) {
			mImageLoader.clearCache();
		}
		if (mListSearchMenuItem != null) {
			mListSearchMenuItem.clear();
			mListSearchMenuItem = null;
		}
		if (mListCategoryObject != null) {
			mListCategoryObject.clear();
			mListCategoryObject = null;
		}
	}

	@Override
	public void ChangeSubTotal(float total, boolean isChange) {
	}

	@Override
	public void ChangeStatusHorizontal(boolean isShow) {
		if (isShow) {
			mIsShowThumbnail = false;
			initUiForLandScape(mPosition);
		}
	}

	@Override
	public void ChangeStatus(boolean isShow) {
		if (isShow) {
			mBtFavorite.setVisibility(View.VISIBLE);
			mBtAddOder.setVisibility(View.VISIBLE);
			mBtShare.setVisibility(View.VISIBLE);
			mBtRating.setVisibility(View.VISIBLE);
			mRlSliderTop.setVisibility(View.VISIBLE);
			mRlGroupFeature.setVisibility(View.VISIBLE);

			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				if (mListSearchMenuItem.get(mPosition).getLisItemKey().size() <= 0) {
					mHorizontalListViewItemKey.setVisibility(View.VISIBLE);
				} else if (mListSearchMenuItem.get(mPosition).getLisItemKey()
						.size() > 0) {
					mHorizontalListViewItemKey.setVisibility(View.VISIBLE);
				}

				if (mListSearchMenuItem.get(mPosition).getSpecialMesssage() == null
						|| mListSearchMenuItem.get(mPosition)
								.getSpecialMesssage().equals("")) {
					mRlSpeciallEvent.setVisibility(View.GONE);
				} else if (!mListSearchMenuItem.get(mPosition)
						.getSpecialMesssage().trim().equals("")) {
					mRlSpeciallEvent.setVisibility(View.VISIBLE);
				}

				if (mListSearchMenuItem.get(mPosition).isTryNextTime() == 1) {
					mIbTryNextTime.setVisibility(View.VISIBLE);
				} else if (mListSearchMenuItem.get(mPosition).isTryNextTime() != 1) {
					mIbTryNextTime.setVisibility(View.GONE);
				}
				// =================================================================
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				if (mListMenuItem.get(mPosition).getLisItemKey().size() <= 0) {
					mHorizontalListViewItemKey.setVisibility(View.VISIBLE);
				} else if (mListMenuItem.get(mPosition).getLisItemKey().size() > 0) {
					mHorizontalListViewItemKey.setVisibility(View.VISIBLE);
				}

				if (mListMenuItem.get(mPosition).getSpecialMesssage()
						.equals("")) {
					mRlSpeciallEvent.setVisibility(View.GONE);
				} else if (!mListMenuItem.get(mPosition).getSpecialMesssage()
						.trim().equals("")) {
					mRlSpeciallEvent.setVisibility(View.VISIBLE);
				}

				if (mListMenuItem.get(mPosition).isTryNextTime() == 1) {
					mIbTryNextTime.setVisibility(View.VISIBLE);
				} else if (mListMenuItem.get(mPosition).isTryNextTime() != 1) {
					mIbTryNextTime.setVisibility(View.GONE);
				}
			}
			mIsShowThumbnail = false;
		}
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MENUITEM);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		Intent intent = new Intent(MenuItemActivity.this,
				MyAccountActivity.class);
		intent.putExtra(StringExtraUtils.KEY_LOGO_RESTAURANT_MYACCOUNT, mLogo);
		intent.putExtra(StringExtraUtils.KEY_STATUS_RESTAURANT_MYACCOUNT,
				mDinnerStatus);
		intent.putExtra(StringExtraUtils.KEY_POINT_RESTAURANT_MYACCOUNT,
				mPointNumber);
		intent.putExtra(StringExtraUtils.KEY_ID_RESTAURANT_MYACCOUNT,
				mLocationId);
		intent.putExtra(StringExtraUtils.KEY_RECOGNIZE_RESTAURANT_MYACCOUNT,
				true);
		startActivityForResult(intent, CodeRequest.CODE_REQUEST_MENUITEM);
	}

	@Override
	public void onNextTimeClickListener() {
		try {
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				if (Utils.isNetworkConnected(this)) {
					if (mListSearchMenuItem.get(mPosition).isTryNextTime() == 1) {
						mListSearchMenuItem.get(mPosition).setTryNextTime(0);
						new AddNextTimeTask().execute(0);
						// mIbTryNextTime.setVisibility(View.GONE);
					} else {
						new AddNextTimeTask().execute(1);
						mListSearchMenuItem.get(mPosition).setTryNextTime(1);
						// mIbTryNextTime.setVisibility(View.VISIBLE);
					}
				} else {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_network),
							Toast.LENGTH_SHORT).show();
				}
				// ==========================================================
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				if (Utils.isNetworkConnected(this)) {
					if (mListMenuItem.get(mPosition).isTryNextTime() == 1) {
						new AddNextTimeTask().execute(0);
						mListMenuItem.get(mPosition).setTryNextTime(0);
					} else {
						new AddNextTimeTask().execute(1);
						mListMenuItem.get(mPosition).setTryNextTime(1);
					}
				} else {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_network),
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MenuItemActivity.this,
				mLogo, mDinnerStatus, mPointNumber, false);
		dialog.show();
	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		} else {
			Toast.makeText(MenuItemActivity.this,
					getResources().getString(R.string.mess_error_network),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onThumbnailClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		mIsShowThumbnail = true;
		new LoadViewThumbnail().execute();
	}

	@Override
	public void onSearchClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		onClick_Menu();
	}

	@Override
	public void onFeedbackClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		myFeedback();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		// Set value for item detail when have page selected listener
		try {
			// TODO
			if (!mIsShowThumbnail) {
				mPosition = position;
				mOrientation = getScreenOrientation();
				Typeface type = Typeface.createFromAsset(getAssets(),
						"fonts/amrtypen.ttf");

				mTvnRateScore.setTypeface(type);
				mTvNumberReview.setTypeface(type);
				if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						if (mListSearchMenuItem.get(position).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore
									.setText(Utils.exchangeRateGetExtra(Float
											.parseFloat(mListSearchMenuItem
													.get(position).getRating())));
							if (mListSearchMenuItem.get(position).getReview() > 1) {
								mTvNumberReview
										.setText(mListSearchMenuItem.get(
												position).getReview()
												+ " Reviews");
							} else {
								mTvNumberReview.setText(mListSearchMenuItem
										.get(position).getReview() + " Review");
							}
						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}

						if (mLnLayoutAddOrder != null) {
							if (mLnLayoutAddOrder.isShown()) {
								mLnLayoutAddOrder.setVisibility(View.GONE);
							}
						}

						mBtMenu.setText(mListSearchMenuItem.get(position)
								.getCategoryName());
						mTvSpecialMessage.setText(mListSearchMenuItem.get(
								position).getSpecialMesssage());
						mTvNameFood.setText(mListSearchMenuItem.get(position)
								.getName());
						mTvPrice.setText("$ "
								+ Utils.formatNumber(
										mListSearchMenuItem.get(position)
												.getPrice(), "0.00"));
						mTvFoodInfo.setText(mListSearchMenuItem.get(position)
								.getDescription());
						mTvBioInformation.setText(mListSearchMenuItem.get(
								position).getDescription());
						mTvCalory.setText((int) mListSearchMenuItem.get(
								position).getCalories()
								+ "");
						mTvPoint.setText(" "
								+ mListSearchMenuItem.get(position)
										.getReward_point());
						if (mListSearchMenuItem.get(position)
								.getSpecialMesssage() == null
								|| mListSearchMenuItem.get(position)
										.getSpecialMesssage().equals("")) {
							mRlSpeciallEvent.setVisibility(View.GONE);
						} else if (mListSearchMenuItem.get(position)
								.getSpecialMesssage() != null
								&& !mListSearchMenuItem.get(position)
										.getSpecialMesssage().trim().equals("")) {
							mRlSpeciallEvent.setVisibility(View.VISIBLE);
						}
						if (mListSearchMenuItem.get(position).isTryNextTime() == 1) {
							mIbTryNextTime.setVisibility(View.VISIBLE);
						} else {
							mIbTryNextTime.setVisibility(View.GONE);
						}
						if (mListSearchMenuItem.get(position).isFavorite() == 1) {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}

						if (mListSearchMenuItem.get(position).getQuantity() == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mListSearchMenuItem
									.get(position).getQuantity() + "");
						}

						if (mListSearchMenuItem.get(position).getStatus() != 1) {
							mBtOrderMinus.setVisibility(View.VISIBLE);
							mBtOrderMinus
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											onClick_Order_Minus();
										}
									});
						} else {
							mBtOrderMinus.setVisibility(View.GONE);
						}

						// =================================================================
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						if (mListMenuItem.get(position).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore.setText(Utils
									.exchangeRateGetExtra(Float
											.parseFloat(mListMenuItem.get(
													position).getRating())));
							if (mListMenuItem.get(position).getReview() > 1) {
								mTvNumberReview.setText(mListMenuItem.get(
										position).getReview()
										+ " Reviews");
							} else {
								mTvNumberReview.setText(mListMenuItem.get(
										position).getReview()
										+ " Review");
							}
						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}

						if (mLnLayoutAddOrder != null) {
							if (mLnLayoutAddOrder.isShown()) {
								mLnLayoutAddOrder.setVisibility(View.GONE);
							}
						}

						if (mListMenuItem.get(position).getStatus() != 1) {
							mBtOrderMinus.setVisibility(View.VISIBLE);
							mBtOrderMinus
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											onClick_Order_Minus();
										}
									});
						} else {
							mBtOrderMinus.setVisibility(View.GONE);
						}

						mTvSpecialMessage.setText(mListMenuItem.get(position)
								.getSpecialMesssage());
						mTvNameFood.setText(mListMenuItem.get(position)
								.getName());
						mTvPrice.setText("$ "
								+ Utils.formatNumber(mListMenuItem
										.get(position).getPrice(), "0.00"));
						mTvFoodInfo.setText(mListMenuItem.get(position)
								.getDescription());
						mTvBioInformation.setText(mListMenuItem.get(position)
								.getDescription());
						mTvCalory.setText((int) mListMenuItem.get(position)
								.getCalories() + "");
						mTvPoint.setText(" "
								+ mListMenuItem.get(position).getReward_point());
						if (mListMenuItem.get(position).getSpecialMesssage()
								.equals("")) {
							mRlSpeciallEvent.setVisibility(View.GONE);
						} else if (!mListMenuItem.get(position)
								.getSpecialMesssage().trim().equals("")) {
							mRlSpeciallEvent.setVisibility(View.VISIBLE);
						}
						if (mListMenuItem.get(position).isTryNextTime() == 1) {
							mIbTryNextTime.setVisibility(View.VISIBLE);
						} else {
							mIbTryNextTime.setVisibility(View.GONE);
						}
						if (mListMenuItem.get(position).isFavorite() == 1) {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}

						if (mListMenuItem.get(position).getQuantity() == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mListMenuItem.get(position)
									.getQuantity() + "");
						}

					}
					new LoadGalleryTask().execute(position);
				} else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {

					mTvnRateScore.setTypeface(type);
					mTvNumberReview.setTypeface(type);

					if (mLnLayoutAddOrder != null) {
						if (mLnLayoutAddOrder.isShown()) {
							mLnLayoutAddOrder.setVisibility(View.GONE);
						}
					}

					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						if (mListSearchMenuItem.get(position).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore
									.setText(Utils.exchangeRateGetExtra(Float
											.parseFloat(mListSearchMenuItem
													.get(position).getRating())));
							if (mListSearchMenuItem.get(position).getReview() > 1) {
								mTvNumberReview
										.setText(mListSearchMenuItem.get(
												position).getReview()
												+ " Reviews");
							} else {
								mTvNumberReview.setText(mListSearchMenuItem
										.get(position).getReview() + " Review");
							}
						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}

						mTvNameFood.setText(mListSearchMenuItem.get(position)
								.getName());
						if (mListSearchMenuItem.get(position).isFavorite() == 1) {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}

						if (mListSearchMenuItem.get(position).getQuantity() == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mListSearchMenuItem
									.get(position).getQuantity() + "");
						}

					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						if (mListMenuItem.get(position).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore.setText(Utils
									.exchangeRateGetExtra(Float
											.parseFloat(mListMenuItem.get(
													position).getRating())));
							if (mListMenuItem.get(position).getReview() > 1) {
								mTvNumberReview.setText(mListMenuItem.get(
										position).getReview()
										+ " Reviews");
							} else {
								mTvNumberReview.setText(mListMenuItem.get(
										position).getReview()
										+ " Review");
							}
						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}

						if (mLnLayoutAddOrder != null) {
							if (mLnLayoutAddOrder.isShown()) {
								mLnLayoutAddOrder.setVisibility(View.GONE);
							}
						}

						mTvNameFood.setText(mListMenuItem.get(position)
								.getName());
						if (mListMenuItem.get(position).isFavorite() == 1) {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}

						if (mListMenuItem.get(position).getQuantity() == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mListMenuItem.get(position)
									.getQuantity() + "");
						}

					}
				}
			} else {
				mGridFragments.get(position).onResume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
	}

	public void onClick_Order_Plus(View v) {
		try {
			if (mEtOrderNumber != null) {
				if (mEtOrderNumber.getText().toString().length() == 0) {
					mEtOrderNumber.setText("1");
				} else {
					mEtOrderNumber.setText(String.valueOf(Integer
							.valueOf(mEtOrderNumber.getText().toString()) + 1));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Order_Minus() {
		try {
			if (mEtOrderNumber != null) {
				if (mEtOrderNumber.getText().toString().length() == 0) {
					mEtOrderNumber.setText("1");
				} else if (!mEtOrderNumber.getText().toString()
						.equalsIgnoreCase("0")) {
					mEtOrderNumber.setText(String.valueOf(Integer
							.valueOf(mEtOrderNumber.getText().toString()) - 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Share() {
		try {
			MenuItem item = null;
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				item = mListSearchMenuItem.get(mPosition);
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				item = mListMenuItem.get(mPosition);
			}
			Intent intent = new Intent(this, ShareLocationActivity.class);
			intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_SUBJECT,
					"Share Menu Item");
			intent.putExtra(
					StringExtraUtils.KEY_SHARELOCATION_TITLE,
					getResources().getString(
							R.string.sharelocation_menu_item_title));
			intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_NAME,
					item.getName());
			intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_WEBSITE,
					mWebsite);
			if (item.getImage() != null && item.getImage().size() > 0) {
				intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_LOGO, item
						.getImage().get(0).mImage);
			}
			intent.putExtra(StringExtraUtils.KEY_SHARE_TYPE, 2);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 * @Description The function use to add order when the user touch on button
	 *              order in menu item
	 */
	public void onClick_AddOrder() {
		try {
			if (mLnLayoutAddOrder != null) {
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					if (mLnLayoutAddOrder.isShown()) {
						mLnLayoutAddOrder.setVisibility(View.GONE);
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) != mCurrentQuantity) {

							mCurrentOrderId = mListSearchMenuItem
									.get(mPosition).getOrderId();
							if (mListSearchMenuItem.get(mPosition)
									.getQuantity() == 0) {
								new AddNewOrderSyncTask().execute(Integer
										.valueOf(mEtOrderNumber.getText()
												.toString()));
							} else {
								new UpdateOrderSyncTask()
										.execute(mCurrentOrderId);
							}

						} else if (Integer.valueOf(mEtOrderNumber.getText()
								.toString()) == mCurrentQuantity
								&& mCurrentQuantity != 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						} else if (mEtOrderNumber.getText().equals("")) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						}
					} else
						showOrderLayoutByListSearch();
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					if (mLnLayoutAddOrder.isShown()) {
						mLnLayoutAddOrder.setVisibility(View.GONE);
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) != mCurrentQuantity) {

							mCurrentOrderId = mListMenuItem.get(mPosition)
									.getOrderId();
							if (mListMenuItem.get(mPosition).getQuantity() == 0) {
								new AddNewOrderSyncTask().execute(Integer
										.valueOf(mEtOrderNumber.getText()
												.toString()));
							} else {
								new UpdateOrderSyncTask()
										.execute(mCurrentOrderId);
							}

						} else if (Integer.valueOf(mEtOrderNumber.getText()
								.toString()) == mCurrentQuantity
								&& mCurrentQuantity != 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						} else if (mEtOrderNumber.getText().equals("")) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						}
					} else
						showOrderLayoutByListMenuItem();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @param v
	 * @Description It will store rating and comment of user when they want to
	 *              rate for food item
	 */
	public void onClick_Rating(View v) {
		rating();
	}

	/**
	 * @param v
	 * @Description They also have many food in them favorite, and It will add
	 *              favorite when the user touch on button favorite in food item
	 */
	public void onClick_Favorite(View v) {
		try {
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				if (Utils.isNetworkConnected(this)) {
					if (mListSearchMenuItem.get(mPosition).isFavorite() == 1) {
						mListSearchMenuItem.get(mPosition).setIsFavorite(0);
						new AddFavoriteItemTask().execute(0);
					} else {
						new AddFavoriteItemTask().execute(1);
						mListSearchMenuItem.get(mPosition).setIsFavorite(1);
					}
				} else {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_network),
							Toast.LENGTH_SHORT).show();
				}

				// ==========================================================
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				if (Utils.isNetworkConnected(this)) {
					if (mListMenuItem.get(mPosition).isFavorite() == 1) {
						new AddFavoriteItemTask().execute(0);
						mListMenuItem.get(mPosition).setIsFavorite(0);
					} else {
						new AddFavoriteItemTask().execute(1);
						mListMenuItem.get(mPosition).setIsFavorite(1);
					}
				} else {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_network),
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	/**
	 * @param v
	 * @Description It will call a menu dialog, help for user can chose other
	 *              category in the restaurant They can search menu item which
	 *              they want.
	 */
	public void onClick_Menu() {
		try {
			new CategoryMenuTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 * @Description This method will show all message and rating of customer
	 *              feedback or comment for food item
	 */
	public void onClick_Feedback(View v) {
		try {
			if (Utils.isNetworkConnected(this)) {
				mIsUpdateRestaurantRating = false;
				feedback(0);
			} else {
				Toast.makeText(MenuItemActivity.this,
						getResources().getString(R.string.mess_error_network),
						Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Desciption: Process Google Voice Search
	 */
	public void checkVoiceRecognition() {
		try {
			// Check if voice recognition is present
			PackageManager pm = getPackageManager();
			mActivities = pm.queryIntentActivities(new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
			if (mActivities.size() == 0) {
				// mBtSpeak.setEnabled(false);
				Toast.makeText(MenuItemActivity.this,
						getResources().getString(R.string.mess_error_voice),
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return status screen is horizontal or vertical
	 */
	@SuppressWarnings("deprecation")
	public int getScreenOrientation() {
		Display getOrient = getWindowManager().getDefaultDisplay();
		int orientation = Configuration.ORIENTATION_UNDEFINED;
		if (getOrient.getWidth() == getOrient.getHeight()) {
			orientation = Configuration.ORIENTATION_SQUARE;
		} else {
			if (getOrient.getWidth() < getOrient.getHeight()) {
				orientation = Configuration.ORIENTATION_PORTRAIT;
			} else {
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	/**
	 * 
	 * @Description: Load Gallery AsyncTask
	 * 
	 */
	public class LoadGalleryTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			// showProgress();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				// Load Gallery Data
				mListItemKeys = new ArrayList<ItemKeyObject>();
				mListItemKeys.clear();
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					for (ItemKeyObject data : mListSearchMenuItem
							.get(params[0]).getLisItemKey()) {
						mListItemKeys.add(data);
					}
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					for (ItemKeyObject mi : mListMenuItem.get(params[0])
							.getLisItemKey()) {
						mListItemKeys.add(mi);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				// Set adapter into Gallery
				mItemKeyListViewAdapter = new ItemKeyAdapter(
						MenuItemActivity.this, mListItemKeys);
				mHorizontalListViewItemKey.setAdapter(mItemKeyListViewAdapter);
				mItemKeyListViewAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @Description This method use to show item key of food when the user touch
	 *              on it.
	 */
	public void viewItemkey() {
		try {
			doTimerTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Will show item key message, and count time to turn off.
	 */
	public void doTimerTask() {
		try {
			mTimerTask = new TimerTask() {
				public void run() {
					mHandler.post(new Runnable() {
						public void run() {
							mCountTime++;
							System.out.println("count time: " + mCountTime);
							if (mCountTime == 3) {
								quickAction.dismiss();
								mTimerTask.cancel();
								mCountTime = 0;
							}
						}
					});
				}
			};

			mTimer.schedule(mTimerTask, 1000, 1000); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == CodeRequest.CODE_REQUEST_MENUITEM) {
				if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					finish();
				} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
					setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
					finish();
				} else if (resultCode == CodeRequest.CODE_RESULT_ORDER) {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						try {
							if (Utils.isNetworkConnected(MenuItemActivity.this)) {
								if (mListSearchMenuItem == null) {
									mListSearchMenuItem = new ArrayList<MenuItem>();
								}
								new SearchMenuItemTask().execute();
							} else {
								Toast.makeText(this,
										R.string.mess_error_network,
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT) {
						try {
							if (Utils.isNetworkConnected(MenuItemActivity.this)) {
								new MenuItemTask().execute();
							} else {
								Toast.makeText(this,
										R.string.mess_error_network,
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						try {
							if (Utils.isNetworkConnected(MenuItemActivity.this)) {
								if (mListSearchMenuItem == null) {
									mListSearchMenuItem = new ArrayList<MenuItem>();
								}
								if (mListCategoryObject == null) {
									mListCategoryObject = new ArrayList<CategoryObject>();
								}
								new GetLocationTask().execute(mLocationId);
							} else {
								Toast.makeText(this,
										R.string.mess_error_network,
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (resultCode == CodeRequest.CODE_RESULT_START_OVER) {
					setResult(CodeRequest.CODE_RESULT_START_OVER);
					finish();
				} else if (resultCode == CodeRequest.CODE_RESULT_GO_HOME) {
					setResult(CodeRequest.CODE_RESULT_START_OVER);
					finish();
				} else if (resultCode == CodeRequest.CODE_RESULT_GO_BACK_MYFAVORITE) {
					finish();
				}

			}
			if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

				// If Voice recognition is successful then it returns RESULT_OK
				if (resultCode == RESULT_OK) {

					mTextMatchLists = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					if (!mTextMatchLists.isEmpty()) {
						// If first Match contains the 'open' word
						// Then start web search.
						if (mTextMatchLists.get(0).contains("open")) {

							String searchQuery = mTextMatchLists.get(0)
									.replace("open", " ");
							Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
							search.putExtra(SearchManager.QUERY, searchQuery);
							startActivity(search);
						} else {
							DialogMenu.mEdtSearch.setText(mTextMatchLists
									.get(0));
						}
					}
					// Result code for various error.
				} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(R.string.mess_error_audio),
							Toast.LENGTH_SHORT).show();
				} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_client),
							Toast.LENGTH_SHORT).show();
				} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
				} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_no_match),
							Toast.LENGTH_SHORT).show();
				} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================
	/**
	 * This method use to rate and comment for customer
	 */
	public void rating() {
		try {
			mDialogRating = new DialogRating(this, false,
					new ProcessDialogConfirm_Rating() {

						@Override
						public void click_Submit(int valueRating, String comment) {
							if (Utils.isNetworkConnected(MenuItemActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MenuItemActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MenuItemActivity.this)) {
														new AddCommentRatingTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														Toast.makeText(
																MenuItemActivity.this,
																getResources()
																		.getString(
																				R.string.mess_error_network),
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}

											}

											@Override
											public void click_Cancel() {
												mDialogConfirmRating.dismiss();
											}
										},
										Utils.exchangeRateGetExtra(mDialogRating
												.rateScore()), mDialogRating
												.rateComment());

								mDialogConfirmRating.show();

							} else {
								Toast.makeText(
										MenuItemActivity.this,
										getResources().getString(
												R.string.mess_error_network),
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void click_Cancel() {
							mDialogRating.dismiss();
						}
					}, 13, "", getResources().getString(
							R.string.custom_seebar_rate_tittle_menuitem));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDialogRating.show();

	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(MenuItemActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils.isNetworkConnected(MenuItemActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MenuItemActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MenuItemActivity.this)) {
														new PostMyFeedBackAsynTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														Toast.makeText(
																MenuItemActivity.this,
																R.string.mess_error_network,
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

											@Override
											public void click_Cancel() {
												mDialogConfirmRating.dismiss();
											}
										},
										Utils.exchangeRateGetExtra(mDialogMyFeedback
												.rateScore()),
										mDialogMyFeedback.rateComment());

								mDialogConfirmRating.show();

							} else {
								Toast.makeText(
										MenuItemActivity.this,
										getResources().getString(
												R.string.mess_error_network),
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void click_Cancel() {
							mDialogMyFeedback.dismiss();
						}
					}, 13, getResources().getString(
							R.string.custom_seebar_rate_tittle_menuitem));

		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialogMyFeedback.show();
	}

	/**
	 * This method will be show after user click on rate score and It's have a
	 * dialog custom to show all comment by customers
	 */
	public void feedback(float ratingNumber) {
		try {
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {

				mDLogCustomerFeedbacks = new DialogCustomerFeedbacks(
						MenuItemActivity.this,
						mListSearchMenuItem.get(mPosition),
						mIsUpdateRestaurantRating, ratingNumber,
						new ProcessMenuFeedBack() {

							@Override
							public void update_rating(float ratingNumber) {
								// Set the average rating returned from
								// Server after editing
								Typeface type = Typeface.createFromAsset(
										MenuItemActivity.this.getAssets(),
										"fonts/amrtypen.ttf");

								mTvnRateScore.setTypeface(type);
								mTvnRateScore.setVisibility(View.VISIBLE);
								if (ratingNumber >= 13) {
									mTvnRateScore.setText("F");
								} else {
									mTvnRateScore.setText(Utils
											.exchangeRateGetExtra(ratingNumber));
								}
								mListMenuItem.get(mPosition).setRating(
										ratingNumber + "");
								// Call service to load the list of
								// ratings again
								mDLogCustomerFeedbacks.dismiss();
								feedback(0);
							}
						});
				mDLogCustomerFeedbacks.show();
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				mDLogCustomerFeedbacks = new DialogCustomerFeedbacks(
						MenuItemActivity.this, mListMenuItem.get(mPosition),
						mIsUpdateRestaurantRating, ratingNumber,
						new ProcessMenuFeedBack() {

							@Override
							public void update_rating(float ratingNumber) {
								// Set the average rating returned from
								// Server after editing
								Typeface type = Typeface.createFromAsset(
										MenuItemActivity.this.getAssets(),
										"fonts/amrtypen.ttf");

								mTvnRateScore.setTypeface(type);
								mTvnRateScore.setVisibility(View.VISIBLE);
								if (ratingNumber >= 13) {
									mTvnRateScore.setText("F");
								} else {
									mTvnRateScore.setText(Utils
											.exchangeRateGetExtra(ratingNumber));
								}
								mListMenuItem.get(mPosition).setRating(
										ratingNumber + "");
								// Call service to load the list of
								// ratings again
								mDLogCustomerFeedbacks.dismiss();
								feedback(0);
							}
						});
				mDLogCustomerFeedbacks.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			if (mLayoutMenu != null) {
				if (mLayoutMenu.isShown()) {
					mLayoutMenu.setVisibility(View.GONE);
				} else {
					mLayoutMenu.setVisibility(View.VISIBLE);
					mLayoutMenu.startAnimation(mAnimationShow);

				}
			}

		}
		return super.onKeyDown(keycode, event);
	}

	/**
	 * 
	 * @Description: Load Thumbnail of Images Menu item List AsyncTask
	 * 
	 */
	public class LoadViewThumbnail extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Constants.mIdSelected = 0;
				Iterator<String> mIt;
				mGridFragments = new ArrayList<GridMenuItemFragment>();
				mIt = mListMenuItemPhotos.iterator();
				int i = 0;
				while (mIt.hasNext()) {
					mItmLsts = new ArrayList<GridItems>();

					GridItems itm = new GridItems(i, mIt.next(), false);
					mItmLsts.add(itm);
					i = i + 1;

					if (mIt.hasNext()) {
						GridItems itm1 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm1);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm2 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm2);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm3 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm3);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm4 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm4);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm5 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm5);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm6 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm6);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm7 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm7);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm8 = new GridItems(i, mIt.next(), false);
						mItmLsts.add(itm8);
						i = i + 1;
					}

					GridItems[] gp = {};
					GridItems[] gridPage = mItmLsts.toArray(gp);
					mGridFragments.add(new GridMenuItemFragment(gridPage,
							MenuItemActivity.this, mListMenuItemPhotos,
							MenuItemActivity.this, MenuItemActivity.this));
				}
				mPagerAdapter = new PagerCustomGridAdapter(
						getSupportFragmentManager(), mGridFragments);
				mIsShowThumbnail = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				mVpPhotos.setAdapter(mPagerAdapter);
				mCirIndicator.setViewPager(mVpPhotos);
				mCirIndicator.setCurrentItem(0);
				mCirIndicator.setOnPageChangeListener(MenuItemActivity.this);
				mBtAddOder.setVisibility(View.GONE);
				mBtFavorite.setVisibility(View.GONE);
				mBtRating.setVisibility(View.GONE);
				mBtShare.setVisibility(View.GONE);
				mRlSliderTop.setVisibility(View.GONE);
				mRlSpeciallEvent.setVisibility(View.GONE);
				mHorizontalListViewItemKey.setVisibility(View.GONE);
				mIbTryNextTime.setVisibility(View.GONE);
				mRlGroupFeature.setVisibility(View.GONE);
				if (mLnLayoutAddOrder != null) {
					if (mLnLayoutAddOrder.isShown()) {
						mLnLayoutAddOrder.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}

	}

	/**
	 * show progress dialog.
	 * 
	 */
	public void showProgress() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(MenuItemActivity.this);
			mProgressDialog.setMessage(getResources().getString(
					R.string.mess_waitting));
		}
		mProgressDialog.show();
	}

	/**
	 * cancel progress dialog.
	 */
	public void dismissProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void PostionItem(int position) {
		try {
			mPosition = position;
			Typeface type = Typeface.createFromAsset(getAssets(),
					"fonts/amrtypen.ttf");

			mTvnRateScore.setTypeface(type);
			mTvNumberReview.setTypeface(type);
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
				if (mListSearchMenuItem.get(position).getReview() != 0) {
					mTvnRateScore.setVisibility(View.VISIBLE);
					mTvnRateScore.setText(Utils.exchangeRateGetExtra(Float
							.parseFloat(mListSearchMenuItem.get(position)
									.getRating())));
					if (mListSearchMenuItem.get(position).getReview() > 1) {
						mTvNumberReview.setText(mListSearchMenuItem.get(
								position).getReview()
								+ " Reviews");
					} else {
						mTvNumberReview.setText(mListSearchMenuItem.get(
								position).getReview()
								+ " Review");
					}
				} else {
					mTvnRateScore.setVisibility(View.GONE);
					mTvnRateScore.setText("");
					mTvNumberReview.setText("");
				}

				mTvSpecialMessage.setText(mListSearchMenuItem.get(position)
						.getSpecialMesssage());
				mTvNameFood
						.setText(mListSearchMenuItem.get(position).getName());
				mTvPrice.setText("$ "
						+ Utils.formatNumber(mListSearchMenuItem.get(mPosition)
								.getPrice(), "0.00"));
				mTvFoodInfo.setText(mListSearchMenuItem.get(position)
						.getDescription());
				mTvBioInformation.setText(mListSearchMenuItem.get(position)
						.getDescription());

				mTvCalory
						.setText(""
								+ (int) mListSearchMenuItem.get(position)
										.getCalories());
				mTvPoint.setText(" "
						+ mListSearchMenuItem.get(position).getReward_point());
				mBtMenu.setText(mListSearchMenuItem.get(position)
						.getCategoryName());
			} else if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
					|| mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
				if (mListMenuItem.get(position).getReview() != 0) {
					mTvnRateScore.setVisibility(View.VISIBLE);
					mTvnRateScore
							.setText(Utils.exchangeRateGetExtra(Float
									.parseFloat(mListMenuItem.get(position)
											.getRating())));
					if (mListMenuItem.get(position).getReview() > 1) {
						mTvNumberReview.setText(mListMenuItem.get(position)
								.getReview() + " Reviews");
					} else {
						mTvNumberReview.setText(mListMenuItem.get(position)
								.getReview() + " Review");
					}
				} else {
					mTvnRateScore.setVisibility(View.GONE);
					mTvnRateScore.setText("");
					mTvNumberReview.setText("");
				}

				mTvSpecialMessage.setText(mListMenuItem.get(position)
						.getSpecialMesssage());
				mTvNameFood.setText(mListMenuItem.get(position).getName());
				mTvPrice.setText("$ "
						+ Utils.formatNumber(mListMenuItem.get(mPosition)
								.getPrice(), "0.00"));
				mTvFoodInfo.setText(mListMenuItem.get(position)
						.getDescription());
				mTvBioInformation.setText(mListMenuItem.get(position)
						.getDescription());
				mTvCalory.setText(""
						+ (int) mListMenuItem.get(position).getCalories());
				mTvPoint.setText(" "
						+ mListMenuItem.get(position).getReward_point());
				// ===========================================
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startActivityForResult() {
		Intent intent = new Intent(this, MyOrderActivity.class);
		intent.putExtra(StringExtraUtils.KEY_POINT, mPointNumber);
		intent.putExtra(StringExtraUtils.KEY_LOGO_RESTAURANT, mLogo);
		intent.putExtra(StringExtraUtils.KEY_ID_LOCATION, mLocationId);
		intent.putExtra(StringExtraUtils.KEY_IS_CHANGE_ORDER, mIsChangeOrder);
		intent.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME, mRestaurantName);
		startActivityForResult(intent, CodeRequest.CODE_REQUEST_MENUITEM);
	}

	@Override
	public void onBackPressed() {
		try {
			Intent intent = new Intent();
			intent.putExtra(StringExtraUtils.KEY_SEARCH_MENU_ITEM,
					mKeySearchMenuItem);
			setResult(RESULT_OK, intent);
			if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
				if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
					if (mLayoutMenu.isShown()) {
						mLayoutMenu.setVisibility(View.GONE);
					} else {
						finish();
					}
				} else {
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * When viewing the menu, the use should be able to return to the restaurant
	 * home by tapping on the restaurants logo in the top.
	 * 
	 * @param v
	 */
	public void onClick_backFromLogo(View v) {
		try {
			if (mIsSearchMenuItem != -1
					&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE
					|| isActivityFavorite == true) {
				Intent intent = new Intent(this,
						RestaurantDetailsActivity.class);
				intent.putExtra(StringExtraUtils.KEY_ID_LOCATION, mLocationId);
				intent.putExtra(StringExtraUtils.KEY_IS_ACTIVITY_FAVORITE,
						isActivityFavorite);
				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MENUITEM);
			} else {
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class AddCommentRatingTask extends
			AsyncTask<String, Void, MenuItemCommentEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MenuItemCommentEntity doInBackground(String... params) {
			try {
				InputStream source = null;
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					source = Server.requestPost(ServerURL
							.getUrlAddComment(mListSearchMenuItem
									.get(mPosition).getId()), ServerURL
							.getEntityAddComment(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), mDialogRating
											.rateScore(), mDialogRating
											.rateComment(), mListSearchMenuItem
											.get(mPosition).getCategory_id(),
									mListSearchMenuItem.get(mPosition)
											.getMenuId()));
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					source = Server.requestPost(ServerURL
							.getUrlAddComment(mListMenuItem.get(mPosition)
									.getId()), ServerURL.getEntityAddComment(
							UserObject.getInstance(MenuItemActivity.this)
									.getAccess_token(), mDialogRating
									.rateScore(), mDialogRating.rateComment(),
							mListMenuItem.get(mPosition).getCategory_id(),
							mListMenuItem.get(mPosition).getMenuId()));
				}
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MenuItemCommentEntity response = gson.fromJson(json,
						MenuItemCommentEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MenuItemCommentEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(MenuItemActivity.this,
							getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Typeface type = Typeface.createFromAsset(getAssets(),
							"fonts/amrtypen.ttf");

					mTvnRateScore.setTypeface(type);
					mTvNumberReview.setTypeface(type);

					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						mListSearchMenuItem.get(mPosition).setRating(
								result.mRating + "");
						mListSearchMenuItem.get(mPosition).setReview(
								result.mReview);
						if (mListSearchMenuItem.get(mPosition).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore
									.setText(Utils.exchangeRateGetExtra(Float
											.parseFloat(mListSearchMenuItem
													.get(mPosition).getRating())));
							if (mListSearchMenuItem.get(mPosition).getReview() > 1) {
								mTvNumberReview.setText(mListSearchMenuItem
										.get(mPosition).getReview()
										+ " Reviews");
							} else {
								mTvNumberReview
										.setText(mListSearchMenuItem.get(
												mPosition).getReview()
												+ " Review");
							}

						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						mListMenuItem.get(mPosition).setRating(
								result.mRating + "");
						mListMenuItem.get(mPosition).setReview(result.mReview);
						if (mListMenuItem.get(mPosition).getReview() != 0) {
							mTvnRateScore.setVisibility(View.VISIBLE);
							mTvnRateScore.setText(Utils
									.exchangeRateGetExtra(Float
											.parseFloat(mListMenuItem.get(
													mPosition).getRating())));
							mTvNumberReview.setText(mListMenuItem
									.get(mPosition).getReview() + " Reviews");

						} else {
							mTvnRateScore.setVisibility(View.GONE);
							mTvnRateScore.setText("");
							mTvNumberReview.setText("");
						}
						// ==============================================================
					}
					// Call service to load the list of diner ratings
					mIsUpdateRestaurantRating = false;
					feedback(0);
					// Call service to load Restaurant detail information task
					// to get Diner and Point
					new GetLocationTask().execute(mLocationId);
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class AddNextTimeTask extends
			AsyncTask<Integer, Void, AddNextTimeEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		int mCheck;

		@Override
		protected AddNextTimeEntity doInBackground(Integer... params) {
			try {
				InputStream source = null;
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					source = Server.requestPost(ServerURL
							.getUrlAddNextTime(mListSearchMenuItem.get(
									mPosition).getId()), ServerURL
							.getEntityAddNextTime(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), params[0],
									mListSearchMenuItem.get(mPosition)
											.getMenuId(), mListSearchMenuItem
											.get(mPosition).getCategory_id()));
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					source = Server.requestPost(ServerURL
							.getUrlAddNextTime(mListMenuItem.get(mPosition)
									.getId()), ServerURL.getEntityAddNextTime(
							UserObject.getInstance(MenuItemActivity.this)
									.getAccess_token(), params[0],
							mListMenuItem.get(mPosition).getMenuId(),
							mListMenuItem.get(mPosition).getCategory_id()));
				}
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				AddNextTimeEntity response = gson.fromJson(json,
						AddNextTimeEntity.class);
				mCheck = params[0];
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(AddNextTimeEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						if (mListSearchMenuItem.get(mPosition).isTryNextTime() == 1) {
							mListSearchMenuItem.get(mPosition)
									.setTryNextTime(1);
							mIbTryNextTime.setVisibility(View.VISIBLE);
						} else {
							mListSearchMenuItem.get(mPosition)
									.setTryNextTime(0);
							mIbTryNextTime.setVisibility(View.GONE);
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						if (mListMenuItem.get(mPosition).isTryNextTime() == 1) {
							mListMenuItem.get(mPosition).setTryNextTime(1);
							mIbTryNextTime.setVisibility(View.VISIBLE);
						} else {
							mListMenuItem.get(mPosition).setTryNextTime(0);
							mIbTryNextTime.setVisibility(View.GONE);
						}
						// ====================================================
					}
					if (Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
						return;
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mStatus
						.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources().getString(
									R.string.mess_error_network),
							Toast.LENGTH_LONG).show();
					dismissProgress();
				} else {
					if (mCheck == 0) {
						mIbTryNextTime.setVisibility(View.GONE);
					} else if (mCheck == 1) {
						mIbTryNextTime.setVisibility(View.VISIBLE);
					}
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class AddFavoriteItemTask extends
			AsyncTask<Integer, Void, AddFavoriteItemEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		int mCheckFavor;

		@Override
		protected AddFavoriteItemEntity doInBackground(Integer... params) {
			try {
				InputStream source = null;
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					source = Server.requestPost(ServerURL
							.getUrlAddFavorite(mListSearchMenuItem.get(
									mPosition).getId()), ServerURL
							.getEntityAddFavorite(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), params[0],
									mListSearchMenuItem.get(mPosition)
											.getCategory_id(),
									mListSearchMenuItem.get(mPosition)
											.getMenuId()));
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					source = Server.requestPost(ServerURL
							.getUrlAddFavorite(mListMenuItem.get(mPosition)
									.getId()), ServerURL.getEntityAddFavorite(
							UserObject.getInstance(MenuItemActivity.this)
									.getAccess_token(), params[0],
							mListMenuItem.get(mPosition).getCategory_id(),
							mListMenuItem.get(mPosition).getMenuId()));
					// =============================================================
				}
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				AddFavoriteItemEntity response = gson.fromJson(json,
						AddFavoriteItemEntity.class);
				mCheckFavor = params[0];
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(AddFavoriteItemEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						if (mListSearchMenuItem.get(mPosition).isFavorite() == 1) {
							mListSearchMenuItem.get(mPosition).setIsFavorite(1);

							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {
							mListSearchMenuItem.get(mPosition).setIsFavorite(0);
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						if (mListMenuItem.get(mPosition).isFavorite() == 1) {
							mListMenuItem.get(mPosition).setIsFavorite(1);
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
						} else {

							mListMenuItem.get(mPosition).setIsFavorite(0);
							mBtFavorite
									.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
						}
					}
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					if (mCheckFavor == 0) {
						mBtFavorite
								.setBackgroundResource(R.drawable.ic_restaurant_details_favorite);
					} else if (mCheckFavor == 1) {
						mBtFavorite
								.setBackgroundResource(R.drawable.ic_restaurant_details_favorite_focus);
					}
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Description: Load Thumbnail Horizontal of Images Restaurant List
	 *               AsyncTask
	 * 
	 */
	public class LoadViewThumbnailHorizontal extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Constants.mIdSelected = -1;
				// Exchange View between MapView show and ListView show
				Iterator<String> mIt;
				mGridFragmentHorizontal = new ArrayList<GridFragmentMenuItemHorizontal>();
				mIt = mListMenuItemPhotos.iterator();
				int i = 0;
				while (mIt.hasNext()) {
					ArrayList<GridItems> itmLst = new ArrayList<GridItems>();

					GridItems itm = new GridItems(i, mIt.next(), false);
					itmLst.add(itm);
					i = i + 1;

					if (mIt.hasNext()) {
						GridItems itm1 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm1);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm2 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm2);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm3 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm3);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm4 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm4);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm5 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm5);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm6 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm6);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm7 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm7);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm8 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm8);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm9 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm9);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm10 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm10);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm11 = new GridItems(i, mIt.next(), false);
						itmLst.add(itm11);
						i = i + 1;
					}

					GridItems[] gp = {};
					GridItems[] gridPage = itmLst.toArray(gp);

					mGridFragmentHorizontal
							.add(new GridFragmentMenuItemHorizontal(gridPage,
									MenuItemActivity.this, mListMenuItemPhotos,
									MenuItemActivity.this,
									MenuItemActivity.this));

					mmPagerThumbnailHorizontalAdapter = new PagerCustomHorizontalGridAdapter(
							getSupportFragmentManager(),
							mGridFragmentHorizontal);
					mIsShowThumbnail = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				mVpPhotos.setAdapter(mmPagerThumbnailHorizontalAdapter);
				mCirIndicator.setViewPager(mVpPhotos);
				mCirIndicator.setCurrentItem(0);
				mRlSliderTop.setVisibility(View.GONE);
				// mRlSliderBottom.setVisibility(View.GONE);
				mRlLandNameBottom.setVisibility(View.GONE);
				mBtShare.setVisibility(View.GONE);
				mBtAddOder.setVisibility(View.GONE);

				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismissProgress();
		}

	}

	/**
	 * 
	 * @author baonguyen
	 * @Description This task to load restaurant detailed information again to
	 *              get Diner and Point infos
	 */
	public class GetLocationTask extends
			AsyncTask<Integer, Void, LocationEntity> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected LocationEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlLocationByID(params[0],
								UserObject.getInstance(MenuItemActivity.this)
										.getAccess_token()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				LocationEntity response = gson.fromJson(json,
						LocationEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
			}

			return null;
		}

		@Override
		protected void onPostExecute(LocationEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					}
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						mWebsite = result.getUrl();
						mLogo = result.getLogo();
						mTax = result.getTax();
						isActivityFavorite = true;
						new MenuItemTask().execute();
					}
					mDinnerStatus = result.mDinnerInfos.getDinnerStatus();
					mPointNumber = result.mDinnerInfos.getTotal();

				}
			} catch (Exception e) {
				e.toString();
			}
		}
	}

	public class SearchMenuItemTask extends
			AsyncTask<SearchMenuItemEntity, Void, SearchMenuItemEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected SearchMenuItemEntity doInBackground(
				SearchMenuItemEntity... params) {
			try {
				SearchMenuItemEntity searchMenuItemEntity = params[0];
				return searchMenuItemEntity;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchMenuItemEntity result) {
			super.onPostExecute(result);

			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					finish();
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					finish();
					dismissProgress();
					return;
				} else {
					mListMenuItemPhotos = new ArrayList<String>();
					if (result.mListMenuItem != null) {
						mListSearchMenuItem.clear();
						mListSearchMenuItem.addAll(result.mListMenuItem);
						for (int i = 0; i < mListSearchMenuItem.size(); i++) {
							if (result.mListCurrentOrderObject != null
									&& result.mListCurrentOrderObject.size() != 0) {
								mListSearchMenuItem.get(i).setOrderId(
										result.mListCurrentOrderObject.get(0)
												.getmId());
								if (result.mListCurrentOrderObject.get(0)
										.getmListCurrentOrderItem().size() != 0) {
									for (int j = 0; j < result.mListCurrentOrderObject
											.get(0).getmListCurrentOrderItem()
											.size(); j++) {
										if (mListSearchMenuItem.get(i).getId() == result.mListCurrentOrderObject
												.get(0)
												.getmListCurrentOrderItem()
												.get(j).getItemId()
												&& result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getStatus() == 0
												&& mListSearchMenuItem.get(i)
														.getMenuId() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getmMenuId()
												&& mListSearchMenuItem.get(i)
														.getCategory_id() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j)
														.getmCategoryId()) {
											mListSearchMenuItem
													.get(i)
													.setQuantity(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getQuantity());
											mListSearchMenuItem
													.get(i)
													.setStatus(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getStatus());
											mListSearchMenuItem
													.get(i)
													.setOrderItemId(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getOrderItemId());

											break;
										}
									}
								}
							}
						}
					}
					if (mListSearchMenuItem != null
							&& mListSearchMenuItem.size() != 0) {
						mListMenuItemPhotos.clear();
						for (MenuItem data : mListSearchMenuItem) {
							if (data.getImage() != null
									&& data.getImage().size() != 0) {
								mListMenuItemPhotos
										.add(data.getImage().get(0).mImage);
							} else {
								mListMenuItemPhotos.add("");
							}
						}
						if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
							initUiForPortrait();
						} else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
							initUiForLandScape(mPosition);
						}
					} else {
						Toast.makeText(MenuItemActivity.this,
								getString(R.string.menuitem_search_item),
								Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					}
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();

			}

		}
	}

	public class SearchItemTask extends
			AsyncTask<SearchMenuItemEntity, Void, SearchMenuItemEntity> {

		@Override
		protected SearchMenuItemEntity doInBackground(
				SearchMenuItemEntity... params) {
			try {
				SearchMenuItemEntity searchMenuItemEntity = params[0];
				// InputStream source = Server.requestGet(ServerURL
				// .getUrlSearchMenuItem(mLocationId, UserObject
				// .getInstance(MenuItemActivity.this)
				// .getAccess_token(), mKeySearchMenuItem));
				// Gson gson = new Gson();
				//
				// String json = Server.inputStreamToString(source).toString();
				// SearchMenuItemEntity response = gson.fromJson(json,
				// SearchMenuItemEntity.class);
				return searchMenuItemEntity;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchMenuItemEntity result) {
			super.onPostExecute(result);
			try {
				List<MenuItem> mListTemp = new ArrayList<MenuItem>();
				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					return;
				} else {

					if (result.mListMenuItem != null) {
						mListTemp.clear();
						mListTemp.addAll(result.mListMenuItem);
					}
					if (mListTemp.size() != 0) {
						mListSearchMenuItem.clear();
						mListSearchMenuItem.addAll(result.mListMenuItem);
						if (mListMenuItemPhotos == null) {
							mListMenuItemPhotos = new ArrayList<String>();
						}
						for (int i = 0; i < mListSearchMenuItem.size(); i++) {
							if (result.mListCurrentOrderObject != null
									&& result.mListCurrentOrderObject.size() != 0) {
								mListSearchMenuItem.get(i).setOrderId(
										result.mListCurrentOrderObject.get(0)
												.getmId());
								if (result.mListCurrentOrderObject.get(0)
										.getmListCurrentOrderItem().size() != 0) {
									for (int j = 0; j < result.mListCurrentOrderObject
											.get(0).getmListCurrentOrderItem()
											.size(); j++) {
										if (mListSearchMenuItem.get(i).getId() == result.mListCurrentOrderObject
												.get(0)
												.getmListCurrentOrderItem()
												.get(j).getItemId()
												&& result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getStatus() == 0
												&& mListSearchMenuItem.get(i)
														.getMenuId() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getmMenuId()
												&& mListSearchMenuItem.get(i)
														.getCategory_id() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j)
														.getmCategoryId()) {
											mListSearchMenuItem
													.get(i)
													.setQuantity(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getQuantity());
											mListSearchMenuItem
													.get(i)
													.setStatus(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getStatus());
											mListSearchMenuItem
													.get(i)
													.setOrderItemId(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getOrderItemId());
											break;
										}
									}
								}
							}
						}
						mListMenuItemPhotos.clear();
						for (MenuItem data : mListSearchMenuItem) {
							if (data.getImage() != null
									&& data.getImage().size() != 0) {
								mListMenuItemPhotos
										.add(data.getImage().get(0).mImage);
							} else {
								mListMenuItemPhotos.add("");
							}
						}
						mPosition = 0;
						if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
							initUiForPortrait();
						} else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
							initUiForLandScape(mPosition);
						}
						dialogMenu.dismiss();
					} else {
						Toast.makeText(MenuItemActivity.this,
								getString(R.string.menuitem_search_item),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();

			}
		}
	}

	public class MenuItemTask extends AsyncTask<String, Void, MenuItemEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MenuItemEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlMenuItem(mLocationId,
								UserObject.getInstance(MenuItemActivity.this)
										.getAccess_token(), mIdCategory));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MenuItemEntity response = gson.fromJson(json,
						MenuItemEntity.class);
				return response;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MenuItemEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					finish();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					finish();
				} else {
					if (result.mListMenuItems != null) {
						if (mListMenuItem == null) {
							mListMenuItem = new ArrayList<MenuItem>();
						}

						mListMenuItem.clear();
						mListMenuItem.addAll(result.mListMenuItems);

						for (int i = 0; i < mListMenuItem.size(); i++) {
							if (result.mListCurrentOrderObject != null
									&& result.mListCurrentOrderObject.size() != 0) {

								mListMenuItem.get(i).setOrderId(
										result.mListCurrentOrderObject.get(0)
												.getmId());
								if (result.mListCurrentOrderObject.get(0)
										.getmListCurrentOrderItem().size() != 0) {
									for (int j = 0; j < result.mListCurrentOrderObject
											.get(0).getmListCurrentOrderItem()
											.size(); j++) {
										if (mListMenuItem.get(i).getId() == result.mListCurrentOrderObject
												.get(0)
												.getmListCurrentOrderItem()
												.get(j).getItemId()
												&& result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getStatus() == 0
												&& mListMenuItem.get(i)
														.getMenuId() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j).getmMenuId()
												&& mListMenuItem.get(i)
														.getCategory_id() == result.mListCurrentOrderObject
														.get(0)
														.getmListCurrentOrderItem()
														.get(j)
														.getmCategoryId()) {
											mListMenuItem
													.get(i)
													.setQuantity(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getQuantity());
											mListMenuItem
													.get(i)
													.setStatus(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getStatus());
											mListMenuItem
													.get(i)
													.setOrderItemId(
															result.mListCurrentOrderObject
																	.get(0)
																	.getmListCurrentOrderItem()
																	.get(j)
																	.getOrderItemId());
											break;
										}
									}
								}
							}
						}

						mListMenuItemPhotos = new ArrayList<String>();
						mListMenuItemPhotos.clear();
						for (MenuItem data : mListMenuItem) {
							if (data.getImage() != null
									&& data.getImage().size() != 0) {
								mListMenuItemPhotos
										.add(data.getImage().get(0).mImage);
							} else {
								mListMenuItemPhotos.add("");
							}
						}
						if (isFavorite) {
							for (int i = 0; i < mListMenuItem.size(); i++) {
								if (mListMenuItem.get(i).isFavorite() == 1) {
									mPosition = i;
									break;
								}
							}
						} else if (isNexttime) {
							for (int i = 0; i < mListMenuItem.size(); i++) {
								if (mListMenuItem.get(i).isTryNextTime() == 1) {
									mPosition = i;
									break;
								}
							}
						}
						if (mIsSearchMenuItem != -1
								&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
							for (int i = 0; i < mListMenuItem.size(); i++) {
								if (mIdItem == mListMenuItem.get(i).getId()) {
									mPosition = i;
									break;
								}
							}
						}
						if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
							initUiForPortrait();
						} else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
							initUiForLandScape(mPosition);
						}
					}

					if (dialogMenu != null) {
						dialogMenu.dismiss();
					}
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
		}
	}

	public class CategoryMenuTask extends
			AsyncTask<String, Void, CategoryMenuEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected CategoryMenuEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlCategory(mLocationId,
								UserObject.getInstance(MenuItemActivity.this)
										.getAccess_token()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				CategoryMenuEntity response = gson.fromJson(json,
						CategoryMenuEntity.class);
				return response;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(CategoryMenuEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(MenuItemActivity.this,
								R.string.mess_error_network, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(MenuItemActivity.this,
								R.string.mess_error_server, Toast.LENGTH_SHORT)
								.show();
					}
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					return;
				} else {
					if (result.mListCategory != null) {
						if (mListCategoryObject == null) {
							mListCategoryObject = new ArrayList<CategoryObject>();
						}
						mListCategoryObject.clear();
						mListCategoryObject.addAll(result.mListCategory);
					}
					dialogMenu = new DialogMenu(
							MenuItemActivity.this,
							mListCategoryObject,
							mLogo,
							mWebsite,
							new ProcessDialogMenu() {
								@Override
								public void click_Dialog(String logo,
										int idCategory, String nameCategory,
										String website, String dinnerStatus,
										float pointNumber, int idRestaurant,
										boolean isFavorite, boolean isNextTime,
										boolean isActivityFavorite) {
									try {
										MenuItemActivity.this.isActivityFavorite = isActivityFavorite;
										mPosition = 0;
										mIdCategory = idCategory;
										mNameCategory = nameCategory;
										MenuItemActivity.this.isFavorite = isFavorite;
										MenuItemActivity.this.isNexttime = isNextTime;
										mIsSearchMenuItem = Constants.CATEGORY_FROM_RESTAURANT;
										new MenuItemTask().execute();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								@Override
								public void click_Speak() {
									checkVoiceRecognition();
									Intent intent = new Intent(
											RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
									// Specify the calling package to identify
									// your
									// application
									intent.putExtra(
											RecognizerIntent.EXTRA_CALLING_PACKAGE,
											getClass().getPackage().getName());
									// Given an hint to the recognizer about
									// what the
									// user
									// is going to say
									intent.putExtra(
											RecognizerIntent.EXTRA_LANGUAGE_MODEL,
											RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
									startActivityForResult(intent,
											VOICE_RECOGNITION_REQUEST_CODE);
								}

								@Override
								public void click_StartOver() {
									Intent myItent = new Intent();
									setResult(
											CodeRequest.CODE_RESULT_START_OVER,
											myItent);
									finish();
								}

								@Override
								public void click_Home() {
									if (mIsSearchMenuItem != -1
											&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE
											|| isActivityFavorite) {
										onClick_backFromLogo(null);
									} else {
										finish();
									}
								}

								@Override
								public void click_MenuSearchItem(
										String logo,
										int isSearchMenuItem,
										String website,
										String dinnerStatus,
										float pointNumber,
										int mIdRestaurant,
										String keySearch,
										boolean isAcitvityFavorite,
										SearchMenuItemEntity searchMenuItemEntity) {
									mIsSearchMenuItem = isSearchMenuItem;
									MenuItemActivity.this.isActivityFavorite = isActivityFavorite;
									if (mListSearchMenuItem == null) {
										mListSearchMenuItem = new ArrayList<MenuItem>();
									}
									mKeySearchMenuItem = keySearch;

									new SearchItemTask()
											.execute(new SearchMenuItemEntity[] { searchMenuItemEntity });
								}

								@Override
								public void click_Order() {
									if (mIsSearchMenuItem != -1
											&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
										try {
											if (Utils
													.isNetworkConnected(MenuItemActivity.this)) {
												if (mListSearchMenuItem == null) {
													mListSearchMenuItem = new ArrayList<MenuItem>();
												}
												new SearchMenuItemTask()
														.execute();
											} else {
												Toast.makeText(
														MenuItemActivity.this,
														R.string.mess_error_network,
														Toast.LENGTH_SHORT)
														.show();
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else if (mIsSearchMenuItem != -1
											&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT) {
										try {
											if (Utils
													.isNetworkConnected(MenuItemActivity.this)) {
												new MenuItemTask().execute();
											} else {
												Toast.makeText(
														MenuItemActivity.this,
														R.string.mess_error_network,
														Toast.LENGTH_SHORT)
														.show();
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else if (mIsSearchMenuItem != -1
											&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
										try {
											if (Utils
													.isNetworkConnected(MenuItemActivity.this)) {
												if (mListSearchMenuItem == null) {
													mListSearchMenuItem = new ArrayList<MenuItem>();
												}
												if (mListCategoryObject == null) {
													mListCategoryObject = new ArrayList<CategoryObject>();
												}
												new GetLocationTask()
														.execute(mLocationId);
											} else {
												Toast.makeText(
														MenuItemActivity.this,
														R.string.mess_error_network,
														Toast.LENGTH_SHORT)
														.show();
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									dialogMenu.dismiss();
								}

							}, MenuItemActivity.this, mDinnerStatus,
							mPointNumber, mLocationId, isActivityFavorite,
							mKeySearchMenuItem,
							CodeRequest.CODE_REQUEST_MENUITEM);
					dialogMenu.show();
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();

			}
		}

	}

	public class PostMyFeedBackAsynTask extends
			AsyncTask<String, Void, MyFeedBackEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MyFeedBackEntity doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server.requestPost(ServerURL.getUrlMyFeedback(),
						ServerURL.getEntityMyFeedBack(
								UserObject.getInstance(MenuItemActivity.this)
										.getAccess_token(), mDialogMyFeedback
										.rateScore(), mDialogMyFeedback
										.rateComment()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyFeedBackEntity response = gson.fromJson(json,
						MyFeedBackEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyFeedBackEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					return;
				}

				if ((result.mError != null && result.mError.length() > 0 && result.mError
						.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
						|| (result.mMessage != null && result.mMessage.length() > 0)
						&& result.mMessage
								.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					finish();
					dismissProgress();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(MenuItemActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				}
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(
							MenuItemActivity.this,
							getResources()
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Toast.makeText(
							MenuItemActivity.this,
							getString(R.string.dialog_my_feedback_mess_success),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void totalPoint(float point) {

	}

	public class AddNewOrderSyncTask extends
			AsyncTask<Integer, Void, MenuItemOrderEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
			// if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			// }
			// else {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			// }
		}

		@Override
		protected MenuItemOrderEntity doInBackground(Integer... params) {
			try {
				InputStream source = null;
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					source = Server.requestPost(ServerURL
							.getUrlAddNewOrderItem(), ServerURL
							.getEntityAddNewOrderItem(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), mLocationId,
									mTax, mListSearchMenuItem.get(mPosition),
									params[0]));
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					source = Server.requestPost(ServerURL
							.getUrlAddNewOrderItem(), ServerURL
							.getEntityAddNewOrderItem(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), mLocationId,
									mTax, mListMenuItem.get(mPosition),
									params[0]));
				}

				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MenuItemOrderEntity response = gson.fromJson(json,
						MenuItemOrderEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MenuItemOrderEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(MenuItemActivity.this,
							getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					mIsChangeOrder = true;
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						mCurrentOrderId = result.mOrderId;
						mListSearchMenuItem.get(mPosition).setOrderId(
								result.mOrderId);
						mListSearchMenuItem.get(mPosition).setOrderItemId(
								result.mOrderItemId);
						mListSearchMenuItem.get(mPosition).setStatus(
								result.mOrderItemStatus);
						mListSearchMenuItem.get(mPosition).setQuantity(
								Integer.valueOf(mEtOrderNumber.getText()
										.toString()));
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						mCurrentOrderId = result.mOrderId;
						mListMenuItem.get(mPosition)
								.setOrderId(result.mOrderId);
						mListMenuItem.get(mPosition).setOrderItemId(
								result.mOrderItemId);
						mListMenuItem.get(mPosition).setStatus(
								result.mOrderItemStatus);
						mListMenuItem.get(mPosition).setQuantity(
								Integer.valueOf(mEtOrderNumber.getText()
										.toString()));
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						}
					}
					Constants.isChangeOrder = true;
					Toast.makeText(MenuItemActivity.this,
							"Item has been added successfully",
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class UpdateOrderSyncTask extends
			AsyncTask<Integer, Void, MenuItemOrderEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MenuItemOrderEntity doInBackground(Integer... params) {
			try {
				InputStream source = null;
				if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
					source = Server.requestPost(ServerURL
							.getUrlUpdateOrderItem(), ServerURL
							.getEntityUpdateOrderItem(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), params[0],
									mListSearchMenuItem.get(mPosition), Integer
											.valueOf(mEtOrderNumber.getText()
													.toString())));
				} else if (mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
						|| mIsSearchMenuItem != -1
						&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
					source = Server.requestPost(ServerURL
							.getUrlUpdateOrderItem(), ServerURL
							.getEntityUpdateOrderItem(
									UserObject.getInstance(
											MenuItemActivity.this)
											.getAccess_token(), params[0],
									mListMenuItem.get(mPosition), Integer
											.valueOf(mEtOrderNumber.getText()
													.toString())));
				}

				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MenuItemOrderEntity response = gson.fromJson(json,
						MenuItemOrderEntity.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MenuItemOrderEntity result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					if (!Utils.isNetworkConnected(MenuItemActivity.this)) {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MenuItemActivity.this,
								getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_LONG).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(MenuItemActivity.this,
							getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					mIsChangeOrder = true;
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						mListSearchMenuItem.get(mPosition).setQuantity(
								Integer.valueOf(mEtOrderNumber.getText()
										.toString()));
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						}
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						mListMenuItem.get(mPosition).setQuantity(
								Integer.valueOf(mEtOrderNumber.getText()
										.toString()));
						if (Integer
								.valueOf(mEtOrderNumber.getText().toString()) == 0) {
							mBtAddOder
									.setBackgroundResource(R.drawable.menuitem_button_order_plus);
							mBtAddOder.setText("");
						} else {
							mBtAddOder
									.setBackgroundResource(R.drawable.circle_number);
							mBtAddOder.setText(mEtOrderNumber.getText());
						}
					}
					Constants.isChangeOrder = true;
					Toast.makeText(MenuItemActivity.this,
							"Item has been updated successfully",
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void initOrderButtonOnScreen() {
		if (mLnLayoutAddOrder.isShown()) {
			try {
				if (mLnLayoutAddOrder != null) {
					if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_SEARCH_MENU_ITEM) {
						if (mLnLayoutAddOrder.isShown()) {
							mLnLayoutAddOrder.setVisibility(View.GONE);
							if (Integer.valueOf(mEtOrderNumber.getText()
									.toString()) != mCurrentQuantity) {

								mCurrentOrderId = mListSearchMenuItem.get(
										mPosition).getOrderId();
								if (mListSearchMenuItem.get(mPosition)
										.getQuantity() == 0) {
									new AddNewOrderSyncTask().execute(Integer
											.valueOf(mEtOrderNumber.getText()
													.toString()));
								} else {
									new UpdateOrderSyncTask()
											.execute(mCurrentOrderId);
								}

							} else if (Integer.valueOf(mEtOrderNumber.getText()
									.toString()) == mCurrentQuantity
									&& mCurrentQuantity != 0) {
								mBtAddOder
										.setBackgroundResource(R.drawable.circle_number);
								mBtAddOder.setText(mEtOrderNumber.getText());
							} else if (mEtOrderNumber.getText().equals("")) {
								mBtAddOder
										.setBackgroundResource(R.drawable.menuitem_button_order_plus);
								mBtAddOder.setText("");
							} else {
								mBtAddOder
										.setBackgroundResource(R.drawable.menuitem_button_order_plus);
								mBtAddOder.setText("");
							}
						} else
							showOrderLayoutByListSearch();
					} else if (mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_RESTAURANT
							|| mIsSearchMenuItem != -1
							&& mIsSearchMenuItem == Constants.CATEGORY_FROM_FAVORITE) {
						if (mLnLayoutAddOrder.isShown()) {
							mLnLayoutAddOrder.setVisibility(View.GONE);
							if (Integer.valueOf(mEtOrderNumber.getText()
									.toString()) != mCurrentQuantity) {

								mCurrentOrderId = mListMenuItem.get(mPosition)
										.getOrderId();
								if (mListMenuItem.get(mPosition).getQuantity() == 0) {
									new AddNewOrderSyncTask().execute(Integer
											.valueOf(mEtOrderNumber.getText()
													.toString()));
								} else {
									new UpdateOrderSyncTask()
											.execute(mCurrentOrderId);
								}

							} else if (Integer.valueOf(mEtOrderNumber.getText()
									.toString()) == mCurrentQuantity
									&& mCurrentQuantity != 0) {
								mBtAddOder
										.setBackgroundResource(R.drawable.circle_number);
								mBtAddOder.setText(mEtOrderNumber.getText());
							} else if (mEtOrderNumber.getText().equals("")) {
								mBtAddOder
										.setBackgroundResource(R.drawable.menuitem_button_order_plus);
								mBtAddOder.setText("");
							} else {
								mBtAddOder
										.setBackgroundResource(R.drawable.menuitem_button_order_plus);
								mBtAddOder.setText("");
							}
						} else
							showOrderLayoutByListMenuItem();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public void showDialogInputNumberOrder() {
	// mEtOrderNumber.setFocusable(false);
	// mEtOrderNumber.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// mDialogMenuItemOrderInput = new DialogMenuItemOrderInput(
	// MenuItemActivity.this, new ProcessDialogConfirmOrder() {
	//
	// @Override
	// public void click_Ok() {
	// mEtOrderNumber
	// .setText(mDialogMenuItemOrderInput
	// .getQuantity());
	// }
	//
	// @Override
	// public void click_Cancel() {
	//
	// }
	// }, mEtOrderNumber.getText().toString().trim());
	// mDialogMenuItemOrderInput.show();
	// }
	// });
	// }

	public void commonButtonFunction() {
		mBtAddOder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_AddOrder();
			}
		});

		mBtShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_Share();
			}
		});
	}

	public void showOrderLayoutByListSearch() {
		{
			mLnLayoutAddOrder.setVisibility(View.VISIBLE);
			if (mEtOrderNumber.getText().toString().length() == 0
					&& mListSearchMenuItem.get(mPosition).getQuantity() == 0) {
				mEtOrderNumber.setText("1");
			} else if (mBtAddOder.getText().equals("")) {
				mEtOrderNumber.setText("1");
			} else {
				if (!mEtOrderNumber.getText().equals("")) {
					mEtOrderNumber.setText(mBtAddOder.getText());
				} else {
					mEtOrderNumber.setText("1");
				}

			}
			if (!mBtAddOder.getText().equals("")) {
				mCurrentQuantity = Integer.parseInt(mBtAddOder.getText()
						.toString().trim());
			} else {
				mCurrentQuantity = 0;
			}
		}
	}

	public void showOrderLayoutByListMenuItem() {
		{
			mLnLayoutAddOrder.setVisibility(View.VISIBLE);
			if (mEtOrderNumber.getText().toString().length() == 0
					&& mListMenuItem.get(mPosition).getQuantity() == 0) {
				mEtOrderNumber.setText("1");
			} else if (mBtAddOder.getText().equals("")) {
				mEtOrderNumber.setText("1");
			} else {
				if (!mEtOrderNumber.getText().equals("")) {
					mEtOrderNumber.setText(mBtAddOder.getText());
				} else {
					mEtOrderNumber.setText("1");
				}

			}
			if (!mBtAddOder.getText().equals("")) {
				mCurrentQuantity = Integer.parseInt(mBtAddOder.getText()
						.toString().trim());
			} else {
				mCurrentQuantity = 0;
			}
		}
	}

	@Override
	public void deleteItem(int position) {
		// TODO Auto-generated method stub

	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			initOrderButtonOnScreen();
			return true;
		}

		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
				new LoadViewThumbnail().execute();
				mIsShowThumbnail = true;
			} else {
				new LoadViewThumbnailHorizontal().execute();
				mIsShowThumbnail = true;
			}
			return true;
		}
	}

	/**
	 * @param v
	 *            Show and hide bio info.
	 */
	public void onClick_FoodInformation() {
		try {
			if (mShowBio) {
				mSlBio.setVisibility(View.GONE);
				mAnimation.RunAnimations(MenuItemActivity.this, mSlBio,
						R.anim.popup_hide_up);
				mShowBio = false;
			} else {
				mSlBio.setVisibility(View.VISIBLE);
				mAnimation.RunAnimations(MenuItemActivity.this, mSlBio,
						R.anim.popup_show_down);
				mShowBio = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
