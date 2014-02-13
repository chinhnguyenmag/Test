package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.CustomerFeedRestaurantbackAdapter;
import com.magrabbit.intelligentmenu.adapter.PagerAdapterCustom;
import com.magrabbit.intelligentmenu.adapter.PagerThumbnailHorizontalAdapter;
import com.magrabbit.intelligentmenu.adapter.PhotosRestaurantAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacksRestaurant;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacksRestaurant.ProcessMenuRestaurantFeedBack;
import com.magrabbit.intelligentmenu.customview.DialogMenu;
import com.magrabbit.intelligentmenu.customview.DialogMenu.ProcessDialogMenu;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogRating;
import com.magrabbit.intelligentmenu.customview.DialogRating.ProcessDialogConfirm_Rating;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.MyOrderListener;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.listener.RestaurantDetailsListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.AddFavoriteItemEntity;
import com.magrabbit.intelligentmenu.model.CategoryMenuEntity;
import com.magrabbit.intelligentmenu.model.CategoryObject;
import com.magrabbit.intelligentmenu.model.CommentLocation;
import com.magrabbit.intelligentmenu.model.CommentsRestaurantEntity;
import com.magrabbit.intelligentmenu.model.GridItems;
import com.magrabbit.intelligentmenu.model.ImageEntity;
import com.magrabbit.intelligentmenu.model.LocationEntity;
import com.magrabbit.intelligentmenu.model.LocationFavouriteEntity;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.RestaurantObject;
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
 * @author Hung Hoang
 * @description Information of restaurant
 */
public class RestaurantDetailsActivity extends FragmentActivity implements
		OnTouchListener, OptionMenuClickListener, RestaurantDetailsListener,
		MyOrderListener, OnPageChangeListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	/**
	 * Object restaurant.
	 */
	private LocationEntity mLocation;
	private RestaurantObject mRestaurant;
	private List<CategoryObject> mListCategoryObject = new ArrayList<CategoryObject>();
	private List<String> mListPhotoRestaurant;
	private DialogMyFeedback mDialogMyFeedback;
	/**
	 * This dialog rating,it's will show when user touch on rating button
	 */
	private static DialogRating mDialogRating;
	/**
	 * This is a dialog custom when user click on rate score and it will show
	 * all comment about food
	 */
	private DialogCustomerFeedbacksRestaurant mDialogFeedback;
	private ImageView mIvLogo;
	private ImageView mIvImageDefault;
	private TextView mTvName;
	private TextView mTvPhoneNumber;
	private TextView mTvWebsite;
	private TextView mTvAddress;
	private TextView mTvRating;
	private TextView mTvBio;
	private TextView mTvOpenedHours;
	private TextView mTvTitleBio;
	private Button mBtFavorite;
	private Button mBtShare;
	private Button mBtRate;
	// private Button mBtBio;
	private RelativeLayout mRlHeader;
	private RelativeLayout mRlHeaderMenu;
	private RelativeLayout mRlSliderTop;
	private LinearLayout mSlBio;
	private ListView mLvfeedback;
	private LinePageIndicator mCirIndicator;
	private ViewPager mVpPhotos;
	private PagerAdapterCustom mPagerAdapter;
	private PhotosRestaurantAdapter mPhotoRestaurantAdapter;
	private PagerThumbnailHorizontalAdapter mPagerAdapterThumbnailHorizontalAdapter;
	private List<GridFragment> mGridFragments;
	private List<GridFragmentThumbnailHorizontal> mGridFragmentHorizontal;
	private AnimationControl mAnimation = new AnimationControl();
	private float mYDown, mXDown;
	private float mYUp, mXUp;
	private boolean mCheckUp = false;
	private boolean mShowBio = false;
	private int mOrientation;
	private DialogMenu mDialogMenu;
	private LocationFavouriteEntity mLocationFavouriteEntity;
	private Button mBtDone;
	private int mPosition = 0;
	private int mIdRestaurant;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean isActivityFavorite;
	private String mContextSearch;
	private GestureDetector gestureDetector;
	/**
	 * A dialog showing a progress indicator and an optional text message or
	 * view.
	 */
	protected ProgressDialog mProgressDialog;

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;
	private ImageLoader mImageLoader;
	private List<CommentsRestaurantEntity> mListCustomerFeedbacks;
	private CustomerFeedRestaurantbackAdapter mCusFeedbackAdapter;
	private boolean misShowThumbnail = false;
	private boolean mIsSearchMenuItem = false;
	private boolean mIsUpdateRestaurantRating = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_restaurant);
		gestureDetector = new GestureDetector(this, new GestureListener());
		mImageLoader = new ImageLoader(this);
		mOrientation = getScreenOrientation();
		mListPhotoRestaurant = new ArrayList<String>();

		if (null != getIntent().getExtras()) {
			if (getIntent().getExtras().getInt(
					StringExtraUtils.KEY_ID_LOCATION, -1) != -1) {
				mIdRestaurant = getIntent().getExtras().getInt(
						StringExtraUtils.KEY_ID_LOCATION);
				isActivityFavorite = getIntent().getExtras().getBoolean(
						StringExtraUtils.KEY_IS_ACTIVITY_FAVORITE);
				new GetLocationTask().execute(mIdRestaurant);
			} else {
				mRestaurant = (RestaurantObject) getIntent().getExtras()
						.getParcelable(StringExtraUtils.KEY_RESTAURANT);
				mIdRestaurant = mRestaurant.getRestaurantId();
				new GetLocationTask().execute(mIdRestaurant);
			}
		}
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

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (Utils.isNetworkConnected(RestaurantDetailsActivity.this)) {
				new LoadPointStatusTask().execute(mIdRestaurant);
			} else {
				Toast.makeText(
						this,
						RestaurantDetailsActivity.this
								.getString(R.string.mess_error_network),
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mDialogMenu != null) {
			mDialogMenu.dismiss();
		}
	}

	/**
	 * Init button menu of restaurant details.
	 * 
	 * @param v
	 */
	public void onClick_Menu(View v) {
		if (mListCategoryObject == null) {
			mListCategoryObject = new ArrayList<CategoryObject>();
		}
		try {
			if (Utils.isNetworkConnected(RestaurantDetailsActivity.this)) {
				new CategoryMenuTask().execute();
			} else {
				Toast.makeText(this, R.string.mess_error_network,
						Toast.LENGTH_SHORT).show();
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
			List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
			if (activities.size() == 0) {
				// mBtSpeak.setEnabled(false);
				Toast.makeText(RestaurantDetailsActivity.this,
						R.string.mess_error_voice, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Show and hide bio info.
	 */
	public void onClick_BioInfo(View v) {
		try {
			if (mShowBio) {
				mSlBio.setVisibility(View.GONE);
				mAnimation.RunAnimations(RestaurantDetailsActivity.this,
						mSlBio, R.anim.popup_hide_up);
				mShowBio = false;
			} else {
				mSlBio.setVisibility(View.VISIBLE);
				mAnimation.RunAnimations(RestaurantDetailsActivity.this,
						mSlBio, R.anim.popup_show_down);
				mShowBio = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Share(View v) {
		Intent intent = new Intent(this, ShareLocationActivity.class);
		intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_SUBJECT,
				"Share Restaurant");
		intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_TITLE,
				getString(R.string.sharelocation_restaurant_title));
		intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_NAME,
				mLocation.getNameRestaurant());
		intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_WEBSITE,
				removeHttp(mLocation.getUrl()));
		intent.putExtra(StringExtraUtils.KEY_SHARELOCATION_LOGO,
				mLocation.getLogo());
		intent.putExtra(StringExtraUtils.KEY_SHARE_TYPE, 1);
		startActivityForResult(intent,
				CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
	}

	/**
	 * @param v
	 */
	public void onClick_Rating(View v) {
		rating();
	}

	public void onClick_Call(View v) {

		call(removeDot(mLocation.getPhoneNumber()));
	}

	public void onClick_Feedback(View v) {
		mIsUpdateRestaurantRating = false;
		feedback(0);
	}

	/**
	 * @param v
	 *            Will open google maps to help find that restaurant.
	 */
	public void onClick_Address(View v) {
		try {
			Intent intentMessage = new Intent(RestaurantDetailsActivity.this,
					RestaurantMapView.class);
			// put the address of restaurant in Intent
			intentMessage.putExtra(
					StringExtraUtils.KEY_ADDRESS,
					getRestaurantAddress(mLocation.getAddress(),
							mLocation.getCity(), mLocation.getState()));
			intentMessage.putExtra(StringExtraUtils.KEY_LONGITUDE,
					mLocation.mLongitude);
			// put the name of restaurant in Intent
			intentMessage.putExtra(StringExtraUtils.KEY_LATITUDE,
					mLocation.mLatitude);
			intentMessage.putExtra(
					StringExtraUtils.KEY_ADDRESS_FROM_RESTAURANT, true);
			intentMessage.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
					mLocation.getNameRestaurant());
			// Show MapView and finish the activity
			startActivity(intentMessage);
			// finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param v
	 *            Init event click Favorite.
	 */
	public void onClick_Favorite(View v) {
		try {
			if (Utils.isNetworkConnected(RestaurantDetailsActivity.this)) {
				if (mLocation.getFavourites() != null) {
					if (mLocation.getFavourites().mFavourite == 1) {
						mBtFavorite
								.setBackgroundResource((R.drawable.ic_restaurant_details_favorite));
						mLocation.getFavourites().mFavourite = 0;
						new AddFavoriteItemTask().execute(0);
					} else {
						mBtFavorite
								.setBackgroundResource((R.drawable.ic_restaurant_details_favorite_focus));
						mLocation.getFavourites().mFavourite = 1;
						new AddFavoriteItemTask().execute(1);
					}
				} else {
				}
			} else {
				Toast.makeText(this, R.string.mess_error_network,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * Call form phone number restaurant details.
	 * 
	 * @param phoneNumber
	 */
	private void call(final String phoneNumber) {
		try {
			DialogConfirm d = new DialogConfirm(this, -1, "Call: "
					+ mTvPhoneNumber.getText().toString(),
					getString(R.string.details_restaurant_really_call), true,
					new ProcessDialogConfirm() {

						@Override
						public void click_Ok() {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:" + phoneNumber));
							startActivity(callIntent);
						}

						@Override
						public void click_Cancel() {
							// TODO Auto-generated method stub

						}
					});
			d.show();
		} catch (ActivityNotFoundException e) {
			Log.e("Call", "Call failed", e);
		}
	}

	/**
	 * @param s
	 * @return String not have dot.
	 * 
	 */
	public String removeDot(String s) {
		s = s.replace("(", "");
		s = s.replace(")", "");
		s = s.replace("-", "");
		return s;
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void rating() {
		try {
			mDialogRating = new DialogRating(
					this,
					false,
					new ProcessDialogConfirm_Rating() {

						@Override
						public void click_Submit(int valueRating, String comment) {
							try {
								if (Utils
										.isNetworkConnected(RestaurantDetailsActivity.this)) {
									mDialogConfirmRating = new DialogConFirmRating(
											RestaurantDetailsActivity.this,
											new ProcessDialogConfirmRating() {

												@Override
												public void click_PostIt() {
													try {
														if (Utils
																.isNetworkConnected(RestaurantDetailsActivity.this)) {
															new RateAndCommentSaveTask()
																	.execute();
															mDialogConfirmRating
																	.dismiss();
														} else {
															Toast.makeText(
																	RestaurantDetailsActivity.this,
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
													mDialogConfirmRating
															.dismiss();
												}
											},
											Utils.exchangeRateGetExtra(mDialogRating
													.rateScore()),
											mDialogRating.rateComment());

									mDialogConfirmRating.show();

								} else {
									Toast.makeText(
											RestaurantDetailsActivity.this,
											R.string.mess_error_network,
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						@Override
						public void click_Cancel() {
							mDialogRating.dismiss();
						}
					}, 13, "",
					getString(R.string.custom_seebar_rate_tittle_restaurant));
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialogRating.show();

	}

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
	 * Setup ui for LandScape
	 */
	public void InitUiForLandScape() {
		try {
			setContentView(R.layout.activity_details_restaurant_land);
			mVpPhotos = (ViewPager) findViewById(R.id.activity_my_order_viewpager_avatar);
			mCirIndicator = (LinePageIndicator) findViewById(R.id.details_restaurant_indicator);
			mBtFavorite = (Button) findViewById(R.id.details_restaurant_bt_favorite);
			mBtRate = (Button) findViewById(R.id.details_restaurant_bt_rating);
			mBtShare = (Button) findViewById(R.id.details_restaurant_bt_share);
			mIvImageDefault = (ImageView) findViewById(R.id.details_restaurant_iv_img_default);
			// ===========================================================================
			if (mListPhotoRestaurant.size() == 0) {
				mIvImageDefault.setVisibility(View.VISIBLE);
			} else {
				mIvImageDefault.setVisibility(View.GONE);
			}
			// set adapter for photos restaurant.
			PhotosRestaurantAdapter adapter = new PhotosRestaurantAdapter(this,
					mListPhotoRestaurant, false);
			mVpPhotos.setAdapter(adapter);

			mVpPhotos.setOnTouchListener(this);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(mPosition);
			mCirIndicator.setOnPageChangeListener(this);
			if (mLocation.getFavourites() != null) {
				if (mLocation.getFavourites().mFavourite == 1) {
					mBtFavorite
							.setBackgroundResource((R.drawable.ic_restaurant_details_favorite_focus));
				} else {
					mBtFavorite
							.setBackgroundResource((R.drawable.ic_restaurant_details_favorite));
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup ui for Portrait
	 */
	public void InitUiForPortrait() {
		try {
			setContentView(R.layout.activity_details_restaurant);
			OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
			footerBar.initFooterBar(true, true, false, true, true, true, true,
					true, this);
			mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
			mAnimationShow = AnimationUtils.loadAnimation(this,
					R.anim.popup_show_down);
			mVpPhotos = (ViewPager) findViewById(R.id.activity_my_order_viewpager_avatar);
			mIvLogo = (ImageView) findViewById(R.id.details_restaurant_iv_logo);
			mIvImageDefault = (ImageView) findViewById(R.id.details_restaurant_iv_img_default);
			mTvName = (TextView) findViewById(R.id.details_restaurant_tv_name_restaurant);
			mTvPhoneNumber = (TextView) findViewById(R.id.details_restaurant_tv_phone_number);
			mTvWebsite = (TextView) findViewById(R.id.details_restaurant_tv_website_restaurant);
			mTvAddress = (TextView) findViewById(R.id.details_restaurant_tv_address_restaurant);
			mTvRating = (TextView) findViewById(R.id.details_restaurant_tv_rating);
			mTvOpenedHours = (TextView) findViewById(R.id.details_restaurant_tv_open_hours);
			mTvBio = (TextView) findViewById(R.id.details_restaurant_tv_bio);
			mTvTitleBio = (TextView) findViewById(R.id.details_restaurant_tv_title_bio);
			mCirIndicator = (LinePageIndicator) findViewById(R.id.details_restaurant_indicator);
			mBtFavorite = (Button) findViewById(R.id.details_restaurant_bt_favorite);
			// mBtBio = (Button)
			// findViewById(R.id.details_restaurant_bt_bio_info);
			mBtRate = (Button) findViewById(R.id.details_restaurant_bt_rating);
			mBtShare = (Button) findViewById(R.id.details_restaurant_bt_share);
			mRlHeader = (RelativeLayout) findViewById(R.id.details_restaurant_header);
			mSlBio = (LinearLayout) findViewById(R.id.details_restaurant_sv_bio);
			mRlSliderTop = (RelativeLayout) findViewById(R.id.details_restaurant_slider_top);
			// ===========================================================================
			// set adapter for photos restaurant.
			if (mListPhotoRestaurant.size() == 0) {
				mIvImageDefault.setVisibility(View.VISIBLE);
			} else {
				mIvImageDefault.setVisibility(View.GONE);
			}
			mPhotoRestaurantAdapter = new PhotosRestaurantAdapter(this,
					mListPhotoRestaurant, true);
			mVpPhotos.setAdapter(mPhotoRestaurantAdapter);

			mImageLoader.DisplayImage(ServerURL.URL + mLocation.getLogo(),
					mIvLogo);
			mTvName.setText(mLocation.getNameRestaurant());
			if (mLocation.getPhoneNumber().length() == 10) {
				mTvPhoneNumber.setText(Utils.formatPhoneNumber(mLocation
						.getPhoneNumber()));
			} else {
				mTvPhoneNumber.setText(mLocation.getPhoneNumber());
			}
			mTvWebsite.setText(removeHttp(mLocation.getUrl()));
			mTvAddress.setText(mLocation.getAddress() + " "
					+ mLocation.getCity() + ", " + mLocation.getState() + " "
					+ mLocation.getZip());
			mTvOpenedHours.setText(mLocation.getHourOfOperation());
			Typeface type = Typeface.createFromAsset(getAssets(),
					"fonts/amrtypen.ttf");
			mTvBio.setTypeface(type);
			mTvTitleBio.setTypeface(type);
			mTvBio.setText(mLocation.getBio());
			mTvRating.setTypeface(type);
			if (mLocation.getRating() <= 13 && mLocation.getRating() > 0) {
				mTvRating.setVisibility(View.VISIBLE);
				mTvRating.setText(Utils.exchangeRateGetExtra(mLocation
						.getRating()));
			} else {
				mTvRating.setVisibility(View.GONE);
			}
			if (mLocation.getFavourites() != null) {
				if (mLocation.getFavourites().mFavourite == 1) {
					mBtFavorite
							.setBackgroundResource((R.drawable.ic_restaurant_details_favorite_focus));
				} else {
					mBtFavorite
							.setBackgroundResource((R.drawable.ic_restaurant_details_favorite));
				}

			}
			mVpPhotos.setOnTouchListener(this);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(mPosition);
			mCirIndicator.setOnPageChangeListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup ui for LandScape
	 */
	public void InitUiThumbnailForLandScape() {
		setContentView(R.layout.activity_details_restaurant_thumbnail_land);
		mVpPhotos = (ViewPager) findViewById(R.id.details_restaurant_thumbnail_land_vp_photos);
		mCirIndicator = (LinePageIndicator) findViewById(R.id.details_restaurant_thumbnail_land_indicator);
		// ===========================================================================
	}

	/**
	 * This method will be show after user click on rate score and It's have a
	 * dialog custom to show all comment by customers
	 */
	public void feedback(float ratingNumber) {
		try {
			mDialogFeedback = new DialogCustomerFeedbacksRestaurant(
					RestaurantDetailsActivity.this, mLocation,
					mIsUpdateRestaurantRating, ratingNumber,
					new ProcessMenuRestaurantFeedBack() {

						@Override
						public void update_rating(float ratingNumber) {
							// Set the average rating returned from
							// Server after editing
							Typeface type = Typeface.createFromAsset(
									RestaurantDetailsActivity.this.getAssets(),
									"fonts/amrtypen.ttf");
							mTvRating.setTypeface(type);
							mTvRating.setVisibility(View.VISIBLE);
							if (ratingNumber >= 13) {
								mTvRating.setText("F");
							} else {
								mTvRating.setText(Utils
										.exchangeRateGetExtra(ratingNumber));
							}
							mLocation.setRating(ratingNumber);
							feedback(0);
						}
					});
			mDialogFeedback.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == CodeRequest.CODE_REQUEST_RESTAURANTDETAIL) {
				if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
					setResult(CodeRequest.CODE_RESULT_LOGOUT);
					finish();
				} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
					setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
					finish();
				} else if (resultCode == RESULT_OK) {
					mIsSearchMenuItem = true;
					if (data != null) {
						mContextSearch = data.getExtras().getString(
								StringExtraUtils.KEY_SEARCH_MENU_ITEM);
					}
				} else if (resultCode == CodeRequest.CODE_RESULT_START_OVER) {
					setResult(CodeRequest.CODE_RESULT_START_OVER);
					finish();
				}
			}

			if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

				// If Voice recognition is successful then it returns RESULT_OK
				if (resultCode == RESULT_OK) {

					ArrayList<String> textMatchList = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					if (!textMatchList.isEmpty()) {
						// If first Match contains the 'open' word
						// Then start web search.
						if (textMatchList.get(0).contains("open")) {

							String searchQuery = textMatchList.get(0).replace(
									"open", " ");
							Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
							search.putExtra(SearchManager.QUERY, searchQuery);
							startActivity(search);
						} else {
							DialogMenu.mEdtSearch.setText(textMatchList.get(0));
						}
					}
					// Result code for various error.
				} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_audio, Toast.LENGTH_SHORT)
							.show();
				} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_client, Toast.LENGTH_SHORT)
							.show();
				} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
				} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_no_match, Toast.LENGTH_SHORT)
							.show();
				} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
			mLocation = null;
			misShowThumbnail = false;

			if (mGridFragments != null) {
				mGridFragments.clear();
				mGridFragments = null;
			}
			if (mListCategoryObject != null) {
				mListCategoryObject.clear();
				mListCategoryObject = null;
			}
			if (mListPhotoRestaurant != null) {
				mListPhotoRestaurant.clear();
				mListPhotoRestaurant = null;
			}
			if (mImageLoader != null) {
				mImageLoader.clearCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: Load Thumbnail of Images Restaurant List AsyncTask
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
				Iterator<String> mIt;
				mGridFragments = new ArrayList<GridFragment>();
				mIt = mListPhotoRestaurant.iterator();
				int i = 0;
				while (mIt.hasNext()) {
					ArrayList<GridItems> itmLst = new ArrayList<GridItems>();

					GridItems itm = new GridItems(i, mIt.next());
					itmLst.add(itm);
					i = i + 1;

					if (mIt.hasNext()) {
						GridItems itm1 = new GridItems(i, mIt.next());
						itmLst.add(itm1);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm2 = new GridItems(i, mIt.next());
						itmLst.add(itm2);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm3 = new GridItems(i, mIt.next());
						itmLst.add(itm3);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm4 = new GridItems(i, mIt.next());
						itmLst.add(itm4);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm5 = new GridItems(i, mIt.next());
						itmLst.add(itm5);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm6 = new GridItems(i, mIt.next());
						itmLst.add(itm6);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm7 = new GridItems(i, mIt.next());
						itmLst.add(itm7);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm8 = new GridItems(i, mIt.next());
						itmLst.add(itm8);
						i = i + 1;
					}

					GridItems[] gp = {};
					GridItems[] gridPage = itmLst.toArray(gp);

					mGridFragments.add(new GridFragment(gridPage,
							RestaurantDetailsActivity.this,
							RestaurantDetailsActivity.this));

					mPagerAdapter = new PagerAdapterCustom(
							getSupportFragmentManager(), mGridFragments);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mVpPhotos.setAdapter(mPagerAdapter);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(0);
			mBtFavorite.setVisibility(View.GONE);
			mBtRate.setVisibility(View.GONE);
			mBtShare.setVisibility(View.GONE);
			mRlSliderTop.setVisibility(View.GONE);
			dismissProgress();
		}

	}

	/**
	 * show progress dialog.
	 * 
	 */
	public void showProgress() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(RestaurantDetailsActivity.this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage(getString(R.string.mess_waitting));
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

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		Intent intent = new Intent(RestaurantDetailsActivity.this,
				MyAccountActivity.class);
		intent.putExtra(StringExtraUtils.KEY_LOGO_RESTAURANT_MYACCOUNT,
				mLocation.getLogo());
		intent.putExtra(StringExtraUtils.KEY_STATUS_RESTAURANT_MYACCOUNT,
				mLocation.getDinnerInfos().getDinnerStatus());
		intent.putExtra(StringExtraUtils.KEY_POINT_RESTAURANT_MYACCOUNT,
				mLocation.getDinnerInfos().getTotal());
		intent.putExtra(StringExtraUtils.KEY_ID_RESTAURANT_MYACCOUNT,
				mLocation.getRestaurantId());
		intent.putExtra(StringExtraUtils.KEY_RECOGNIZE_RESTAURANT_MYACCOUNT,
				true);

		startActivityForResult(intent,
				CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);

	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(
				RestaurantDetailsActivity.this, mLocation.getLogo(), mLocation
						.getDinnerInfos().getDinnerStatus(), mLocation
						.getDinnerInfos().getTotal(), false);
		dialog.show();
	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		} else {
			Toast.makeText(this, getString(R.string.mess_error_network),
					Toast.LENGTH_SHORT).show();
			return;
		}

	}

	@Override
	public void onThumbnailClickListener() {
		if (mListPhotoRestaurant != null && mListPhotoRestaurant.size() != 0) {
			mLayoutMenu.setVisibility(View.GONE);
			misShowThumbnail = true;
			new LoadViewThumbnail().execute();
		} else {
			Toast.makeText(this, "There is no photo", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onSearchClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		onClick_Menu(null);
	}

	@Override
	public void onFeedbackClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		myFeedback();
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void myFeedback() {
		try {
			mDialogMyFeedback = new DialogMyFeedback(
					RestaurantDetailsActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(RestaurantDetailsActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										RestaurantDetailsActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(RestaurantDetailsActivity.this)) {
														new PostMyFeedBackAsynTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														Toast.makeText(
																RestaurantDetailsActivity.this,
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
										RestaurantDetailsActivity.this,
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

	@Override
	public void isShowButton(boolean isShow, int position,
			boolean isShowThumbnail) {
		if (isShow && isShowThumbnail) {
			misShowThumbnail = false;
			InitUiForLandScape();
			mPhotoRestaurantAdapter = new PhotosRestaurantAdapter(this,
					mListPhotoRestaurant, false);
			mVpPhotos.setAdapter(mPhotoRestaurantAdapter);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(position);
		} else if (isShow && !isShowThumbnail) {
			misShowThumbnail = false;
			// mBtBio.setVisibility(View.VISIBLE);
			mBtFavorite.setVisibility(View.VISIBLE);
			mBtRate.setVisibility(View.VISIBLE);
			mBtShare.setVisibility(View.VISIBLE);
			mRlSliderTop.setVisibility(View.VISIBLE);
			mPhotoRestaurantAdapter = new PhotosRestaurantAdapter(this,
					mListPhotoRestaurant, true);
			mVpPhotos.setAdapter(mPhotoRestaurantAdapter);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(position);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			// Checks the orientation of the screen for landscape and portrait
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (misShowThumbnail) {
					InitUiThumbnailForLandScape();
					new LoadViewThumbnailHorizontal().execute();
				} else {
					InitUiForLandScape();
				}
				mOrientation = Configuration.ORIENTATION_LANDSCAPE;
			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (misShowThumbnail) {
					InitUiForPortrait();
					new LoadViewThumbnail().execute();
				} else {
					InitUiForPortrait();
				}
				mOrientation = Configuration.ORIENTATION_PORTRAIT;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ChangeSubTotal(float total, boolean isChange) {

	}

	@Override
	public void startActivityForResult() {
		Intent intent = new Intent(this, MyOrderActivity.class);
		intent.putExtra(StringExtraUtils.KEY_POINT, mLocation.getDinnerInfos()
				.getTotal());
		intent.putExtra(StringExtraUtils.KEY_LOGO_RESTAURANT,
				mLocation.getLogo());
		intent.putExtra(StringExtraUtils.KEY_ID_LOCATION,
				mLocation.getRestaurantId());
		intent.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
				mLocation.getNameRestaurant());
		startActivityForResult(intent,
				CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
	}

	@Override
	public void onBackPressed() {
		try {
			setResult(CodeRequest.CODE_RESULT_GO_BACK_MYFAVORITE);
			if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
				if (mLayoutMenu.isShown()) {
					mLayoutMenu.setVisibility(View.GONE);
				} else {
					finish();
				}
			} else {
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
						.getUrlCategory(mLocation.getRestaurantId(), UserObject
								.getInstance(RestaurantDetailsActivity.this)
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
					if (!Utils
							.isNetworkConnected(RestaurantDetailsActivity.this)) {
						Toast.makeText(RestaurantDetailsActivity.this,
								R.string.mess_error_network, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(RestaurantDetailsActivity.this,
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
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
					return;
				} else {
					if (result.mListCategory != null) {
						mListCategoryObject.clear();
						mListCategoryObject.addAll(result.mListCategory);
					}
					mDialogMenu = new DialogMenu(
							RestaurantDetailsActivity.this,
							mListCategoryObject,
							mLocation.getLogo(),
							removeHttp(mLocation.getUrl()),
							new ProcessDialogMenu() {

								@Override
								public void click_Dialog(String logo,
										int idCategory, String nameCategory,
										String website, String dinnerStatus,
										float pointNumber, int idRestaurant,
										boolean isFavorite, boolean isNextTime,
										boolean isActivityFavorite) {
									Intent mIntent = new Intent(
											RestaurantDetailsActivity.this,
											MenuItemActivity.class);
									mIntent.putExtra(
											StringExtraUtils.KEY_CATEGORY_FROM,
											Constants.CATEGORY_FROM_RESTAURANT);
									mIntent.putExtra(
											StringExtraUtils.KEY_ID_CATEGORY,
											idCategory);
									mIntent.putExtra(
											StringExtraUtils.KEY_NAME_CATEGORY,
											nameCategory);
									mIntent.putExtra(
											StringExtraUtils.KEY_LOGO_RESTAURANT,
											logo);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_WEBSITE,
											website);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_DINNERSTATUS,
											dinnerStatus);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_POINTNUMBER,
											pointNumber);
									mIntent.putExtra(
											StringExtraUtils.KEY_ID_LOCATION,
											idRestaurant);
									mIntent.putExtra(
											StringExtraUtils.KEY_IS_FAVORITE,
											isFavorite);
									mIntent.putExtra(
											StringExtraUtils.KEY_IS_NEXTTIME,
											isNextTime);
									mIntent.putExtra(
											StringExtraUtils.KEY_IS_ACTIVITY_FAVORITE,
											isActivityFavorite);
									mIntent.putExtra(StringExtraUtils.KEY_TAX,
											mLocation.getTax());
									mIntent.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
											mLocation.getNameRestaurant());
									
									
									startActivityForResult(
											mIntent,
											CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
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
									// what
									// the user
									// is going to say
									intent.putExtra(
											RecognizerIntent.EXTRA_LANGUAGE_MODEL,
											RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

									startActivityForResult(intent,
											VOICE_RECOGNITION_REQUEST_CODE);
								}

								@Override
								public void click_StartOver() {
									setResult(CodeRequest.CODE_RESULT_START_OVER);
									finish();
								}

								@Override
								public void click_Home() {
									mDialogMenu.dismiss();
									dismissProgress();
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
										boolean isActivityFavorite,
										SearchMenuItemEntity searchMenuItemEntity) {
									Intent mIntent = new Intent(
											RestaurantDetailsActivity.this,
											MenuItemActivity.class);
									mIntent.putExtra(
											StringExtraUtils.KEY_CATEGORY_FROM,
											isSearchMenuItem);
									mIntent.putExtra(
											StringExtraUtils.KEY_LOGO_RESTAURANT,
											logo);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_WEBSITE,
											website);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_DINNERSTATUS,
											dinnerStatus);
									mIntent.putExtra(
											StringExtraUtils.KEY_SHARELOCATION_POINTNUMBER,
											pointNumber);
									mIntent.putExtra(
											StringExtraUtils.KEY_SEARCH_MENU_ITEM,
											keySearch);
									mIntent.putExtra(
											StringExtraUtils.KEY_ID_LOCATION,
											mLocation.getRestaurantId());
									mIntent.putExtra(
											StringExtraUtils.KEY_IS_ACTIVITY_FAVORITE,
											isActivityFavorite);
									mIntent.putExtra(StringExtraUtils.KEY_TAX,
											mLocation.getTax());
									mIntent.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
											mLocation.getNameRestaurant());
									mIntent.putExtra("ItemSearch",
											searchMenuItemEntity);
									startActivityForResult(
											mIntent,
											CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
								}

								@Override
								public void click_Order() {
									// TODO Auto-generated method stub

								}
							}, RestaurantDetailsActivity.this, mLocation
									.getDinnerInfos().getDinnerStatus(),
							mLocation.getDinnerInfos().getTotal(),
							mLocation.mRestaurantId, isActivityFavorite,
							mContextSearch,
							CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
					mDialogMenu.show();
					dismissProgress();
				}

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();

			}
		}

	}

	public class RateAndCommentSaveTask extends
			AsyncTask<String, Void, CommentLocation> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected CommentLocation doInBackground(String... params) {
			try {
				InputStream source = Server.requestPost(ServerURL
						.getUrlLocationComent(mLocation.getRestaurantId()),
						ServerURL.getEntityCommentAndRateLocationSave(
								UserObject.getInstance(
										RestaurantDetailsActivity.this)
										.getAccess_token(), mDialogRating
										.rateScore(), mDialogRating
										.rateComment()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				CommentLocation response = gson.fromJson(json,
						CommentLocation.class);
				return response;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(CommentLocation result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
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
					Toast.makeText(RestaurantDetailsActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mStatus.equals(Constants.CONNECT_ERROR)) {
					Toast.makeText(RestaurantDetailsActivity.this,
							result.mStatus.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					mLocation.setRating(result.mRating);
					mIsUpdateRestaurantRating = false;
					feedback(0);
					if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
						Typeface type = Typeface.createFromAsset(getAssets(),
								"fonts/amrtypen.ttf");
						mTvRating.setTypeface(type);

						if (mLocation.getRating() <= 13
								&& mLocation.getRating() > 0) {
							mTvRating.setVisibility(View.VISIBLE);
							mTvRating
									.setText(Utils
											.exchangeRateGetExtra(mLocation
													.getRating()));
						} else {
							mTvRating.setVisibility(View.GONE);
						}
					}
					dismissProgress();
				}
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}
		}
	}

	public class AddFavoriteItemTask extends
			AsyncTask<Integer, Void, AddFavoriteItemEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected AddFavoriteItemEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestPost(
						ServerURL.getUrlAddFavoriteLocation(mLocation
								.getRestaurantId()), ServerURL
								.getEntityAddFavoriteLocation(
										UserObject.getInstance(
												RestaurantDetailsActivity.this)
												.getAccess_token(),
										(int) params[0]));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				AddFavoriteItemEntity response = gson.fromJson(json,
						AddFavoriteItemEntity.class);
				return response;
			} catch (Exception e) {
				e.toString();
			}

			return null;
		}

		@Override
		protected void onPostExecute(AddFavoriteItemEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					undoFavorite();
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
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
					undoFavorite();
					Toast.makeText(RestaurantDetailsActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mStatus
						.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					undoFavorite();
					dismissProgress();
				} else {
					dismissProgress();
				}
			} catch (Exception e) {
				undoFavorite();
				e.toString();
				dismissProgress();
			}
		}

	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public class GetLocationTask extends
			AsyncTask<Integer, Void, LocationEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected LocationEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlLocationByID(
								params[0],
								UserObject.getInstance(
										RestaurantDetailsActivity.this)
										.getAccess_token()));
				Gson gson = new Gson();
				LocationEntity response = gson.fromJson(Server
						.inputStreamToString(source).toString(),
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
					if (!Utils
							.isNetworkConnected(RestaurantDetailsActivity.this)) {
						Toast.makeText(RestaurantDetailsActivity.this,
								R.string.mess_error_network, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(RestaurantDetailsActivity.this,
								R.string.mess_error_server, Toast.LENGTH_SHORT)
								.show();
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
					Toast.makeText(RestaurantDetailsActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					mLocation = result;
					mListPhotoRestaurant.clear();
					List<ImageEntity> mListImage = new ArrayList<ImageEntity>();
					mListImage.addAll(mLocation.getListImages());
					for (ImageEntity data : mListImage) {
						mListPhotoRestaurant.add(data.mImageUrl);
					}
					if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
						InitUiForPortrait();
					} else if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						InitUiForLandScape();
					}
					mListImage.clear();
					mListImage = null;
					dismissProgress();
				}
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}
		}
	}

	/**
	 * @param s
	 * @return String not have http://.
	 * 
	 */
	public String removeHttp(String s) {
		URL myURL;
		try {
			myURL = new URL(s);
			s = myURL.getHost();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
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
				// Exchange View between MapView show and ListView show
				Iterator<String> mIt;
				mGridFragmentHorizontal = new ArrayList<GridFragmentThumbnailHorizontal>();
				mIt = mListPhotoRestaurant.iterator();
				int i = 0;
				while (mIt.hasNext()) {
					ArrayList<GridItems> itmLst = new ArrayList<GridItems>();

					GridItems itm = new GridItems(i, mIt.next());
					itmLst.add(itm);
					i = i + 1;

					if (mIt.hasNext()) {
						GridItems itm1 = new GridItems(i, mIt.next());
						itmLst.add(itm1);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm2 = new GridItems(i, mIt.next());
						itmLst.add(itm2);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm3 = new GridItems(i, mIt.next());
						itmLst.add(itm3);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm4 = new GridItems(i, mIt.next());
						itmLst.add(itm4);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm5 = new GridItems(i, mIt.next());
						itmLst.add(itm5);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm6 = new GridItems(i, mIt.next());
						itmLst.add(itm6);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm7 = new GridItems(i, mIt.next());
						itmLst.add(itm7);
						i = i + 1;
					}

					if (mIt.hasNext()) {
						GridItems itm8 = new GridItems(i, mIt.next());
						itmLst.add(itm8);
						i = i + 1;
					}

					GridItems[] gp = {};
					GridItems[] gridPage = itmLst.toArray(gp);

					mGridFragmentHorizontal
							.add(new GridFragmentThumbnailHorizontal(gridPage,
									RestaurantDetailsActivity.this,
									RestaurantDetailsActivity.this));

					mPagerAdapterThumbnailHorizontalAdapter = new PagerThumbnailHorizontalAdapter(
							getSupportFragmentManager(),
							mGridFragmentHorizontal);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mVpPhotos.setAdapter(mPagerAdapterThumbnailHorizontalAdapter);
			mCirIndicator.setViewPager(mVpPhotos);
			mCirIndicator.setCurrentItem(0);
			dismissProgress();
		}

	}

	/**
	 * 
	 * @author baonguyen
	 * @Description This task to load restaurant detailed information again to
	 *              get Diner and Point infos
	 */
	public class LoadPointStatusTask extends
			AsyncTask<Integer, Void, LocationEntity> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected LocationEntity doInBackground(Integer... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlLocationByID(
								params[0],
								UserObject.getInstance(
										RestaurantDetailsActivity.this)
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
					Toast.makeText(RestaurantDetailsActivity.this,
							R.string.mess_error_server, Toast.LENGTH_SHORT)
							.show();
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
					Toast.makeText(RestaurantDetailsActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					mLocation = result;
				}
			} catch (Exception e) {
				e.toString();
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
								UserObject.getInstance(
										RestaurantDetailsActivity.this)
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
					if (!Utils
							.isNetworkConnected(RestaurantDetailsActivity.this)) {
						Toast.makeText(
								RestaurantDetailsActivity.this,
								RestaurantDetailsActivity.this
										.getString(R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								RestaurantDetailsActivity.this,
								RestaurantDetailsActivity.this
										.getString(R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(
							RestaurantDetailsActivity.this,
							RestaurantDetailsActivity.this
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Toast.makeText(
							RestaurantDetailsActivity.this,
							getString(R.string.dialog_my_feedback_mess_success),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void undoFavorite() {
		if (mLocation.getFavourites() != null) {
			if (mLocation.getFavourites().mFavourite == 0) {
				mLocation.getFavourites().mFavourite = 1;
				mBtFavorite
						.setBackgroundResource((R.drawable.ic_restaurant_details_favorite_focus));
			} else {
				mLocation.getFavourites().mFavourite = 0;
				mBtFavorite
						.setBackgroundResource((R.drawable.ic_restaurant_details_favorite));
			}
		} else {
			mLocation.getFavourites().mFavourite = 0;
			mBtFavorite
					.setBackgroundResource((R.drawable.ic_restaurant_details_favorite));
		}
	}

	/**
	 * 
	 * @param address
	 * @param city
	 * @param state
	 * @return accurate address of restaurant and prevent printing "," when
	 *         there is no address/city/state Ex: 28 Ly Thuong Kiet, ,Thanh Pho
	 *         Hue --> 28 Ly Thuong Kiet,Thanh Pho Hue
	 */
	public String getRestaurantAddress(String address, String city, String state) {
		StringBuilder builder = new StringBuilder(address);
		if (!city.equals("")) {
			builder.append(" " + city);
		}
		if (!state.equals("")) {
			builder.append("," + state);
		}
		return builder.toString();
	}

	@Override
	public void totalPoint(float point) {
		// TODO Auto-generated method stub

	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
				new LoadViewThumbnail().execute();
				misShowThumbnail=true;
			} else {
				new LoadViewThumbnailHorizontal().execute();
				misShowThumbnail=true;
			}
			return true;
		}
	}

	@Override
	public void deleteItem(int position) {
		// TODO Auto-generated method stub

	}
}