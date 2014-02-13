package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.BalloonsAdapter;
import com.magrabbit.intelligentmenu.adapter.HorizontalListView;
import com.magrabbit.intelligentmenu.adapter.HorizontalListViewAdapter;
import com.magrabbit.intelligentmenu.adapter.RestaurantListViewAdapter;
import com.magrabbit.intelligentmenu.adapter.RestaurantListViewAdapter.AdapterControlListener;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogNormalSearch;
import com.magrabbit.intelligentmenu.customview.DialogNormalSearch.ProcessDialogConfirm_NormalSearch;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.LocationResultListener;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyLocation;
import com.magrabbit.intelligentmenu.model.RestaurantObject;
import com.magrabbit.intelligentmenu.model.SearchProfile;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.task.LoadContactPhoneTask;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @Description Display MapView for locating current position and displaying all
 *              restaurant information
 */
public class MapViewActivity extends FragmentActivity implements
		OptionMenuClickListener, LocationResultListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	/**
	 * A dialog showing a progress indicator and an optional text message or
	 * view.
	 */
	protected ProgressDialog mProgressDialog;
	// Decide whether it is MapView or ListView
	private boolean mIsFromMapView = true;
	// AutoCompleteTextView, Button, ImageButton to connect to layouts
	private TextView mEdtSearch;
	private Button mBtMapView;
	private Button mBtListView;
	private ImageButton mBtSpeak;
	// Sorting followed by Distance and A-Z
	private ImageButton mIbtLeftArrow;
	private ImageButton mIbtRightArrow;
	private ImageButton mIbtSortAZ;
	private ImageButton mIbtSortDistance;
	private boolean mIsSortAZ = false;
	private boolean mIsSortNearestDistance = true;
	// List of restaurant images
	private GoogleMap mMap;
	private Location mCurrentLocation;
	private MyLocation myLocation;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	// Normal Search
	private String mKeywords = "";

	// private Button mBtFilter;
	private HorizontalListView mHorizontalListView;
	// Layout of Gallery to show on or off
	private RelativeLayout mHorizontalListViewLayout;
	private HorizontalListViewAdapter mHorizontalListViewAdapter;
	private int selectedImagePosition = 1;
	/**
	 * List of restaurant objects
	 */
	private List<RestaurantObject> mListRestaurants;

	/**
	 * Variables to change the way of showing restaurant
	 */
	private ListView mLvRestaurant;
	private ArrayAdapter<RestaurantObject> mAdapterRestaurant = null;
	private LinearLayout mLayoutListRestaurant;
	private RelativeLayout mLayoutMapView;
	private DialogNormalSearch dialog_search_normal;

	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;
	private SupportMapFragment mSupportMapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Check GOOGLEPLAY SERVICE whether it is available or not */
		int googlePlayStatus = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// Showing status
		if (googlePlayStatus != ConnectionResult.SUCCESS) { // Google Play
															// Services are not
															// available

			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
					googlePlayStatus, this,
					CodeRequest.CODE_REQUEST_GOOGLEPLAY_SERVICE);
			dialog.show();

		} else { // Google Play Services are available

			setContentView(R.layout.activity_map);
			/* Load all created data into Map */
			if (mMap == null) {
				mMap = ((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.activity_map_map)).getMap();
			}
			/*
			 * Check whether there is ZoomControl +/- or not --> GONE
			 */
			mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.activity_map_map);
			View zoomControls = mSupportMapFragment.getView().findViewById(0x1);
			if (zoomControls != null)
				zoomControls.setVisibility(View.GONE);
			/* Setup GPS button */
			mMap.setMyLocationEnabled(true);

			// Click on Map Marker
			mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(Marker marker) {
					return false;
				}
			});
			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				public void onInfoWindowClick(Marker marker) {
					if (Utils.isNetworkConnected(MapViewActivity.this)) {
						for (RestaurantObject restaurant : mListRestaurants) {
							if (marker.getTitle().equals(
									restaurant.getNameRestaurant())) {
								Intent intent = new Intent(
										MapViewActivity.this,
										RestaurantDetailsActivity.class);
								intent.putExtra(
										StringExtraUtils.KEY_RESTAURANT,
										restaurant);
								startActivityForResult(intent,
										CodeRequest.CODE_REQUEST_MAPVIEW);
							}
						}
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					}
				}
			});
			showProgress();

			/* Check whether GPS is turned on/off */
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				DialogConfirm dialog = new DialogConfirm(
						MapViewActivity.this,
						android.R.drawable.ic_dialog_alert,
						"GPS Confirmation",
						MapViewActivity.this
								.getString(R.string.activity_map_mess_confirm_gps),
						true, new ProcessDialogConfirm() {

							@Override
							public void click_Ok() {
								startActivityForResult(
										new Intent(
												android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
										CodeRequest.CODE_REQUEST_MAPVIEW);
							}

							@Override
							public void click_Cancel() {
								dismissProgress();
							}
						});
				dialog.show();
			}
			// Add Optional Menu to layout
			OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
			footerBar.initFooterBar(true, true, false, true, true, false,
					false, true, this);
			mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
			mAnimationShow = AnimationUtils.loadAnimation(this,
					R.anim.popup_show_down);

			mLvRestaurant = (ListView) findViewById(R.id.activity_map_listview);
			mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_map_gallery);
			mEdtSearch = (TextView) findViewById(R.id.activity_map_edt_search);
			dialog_search_normal = new DialogNormalSearch(MapViewActivity.this,
					mEdtSearch.getText().toString(),
					new ProcessDialogConfirm_NormalSearch() {

						@Override
						public void click_Submit_NormalSearch(
								String keywordsFilled, String keywordsNormal) {
							// Get keyword from editText of
							// normal
							// search
							mKeywords = keywordsFilled;
							mEdtSearch.setText(keywordsNormal);
							// Load Map once more time
							new LoadMapTask().execute();

						}

						@Override
						public void click_Cancel() {
							// TODO Auto-generated method stub
						}

						@Override
						public void click_Submit_DefaultSearch() {
							// Searching Default Profile
							mEdtSearch.setText("");
							if (Utils.isNetworkConnected(MapViewActivity.this)) {
								new RunDefaultProfileTask().execute();
							} else {
								showToastMessage(MapViewActivity.this
										.getString(R.string.mess_error_network));
							}
						}

						@Override
						public void click_Search(SearchProfile searchprofile,
								boolean isRunDefault) {
							// Searching advanced followed by a
							// brand
							// new search profile
							mEdtSearch.setText("");
							if (Utils.isNetworkConnected(MapViewActivity.this)) {
								if (isRunDefault) {
									new RunDefaultProfileTask().execute();
								} else {
									new LoadMapAdvancedSearchTask()
											.execute(searchprofile);
								}
							} else {
								showToastMessage(MapViewActivity.this
										.getString(R.string.mess_error_network));
							}
							dialog_search_normal.dismiss();

						}
					});
			mEdtSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!dialog_search_normal.isShowing()) {
						dialog_search_normal.show();
					}
				}
			});
			mBtSpeak = (ImageButton) findViewById(R.id.activity_map_imbt_speak);
			mBtMapView = (Button) findViewById(R.id.activity_map_imbt_mapView);
			mBtListView = (Button) findViewById(R.id.activity_map_imbt_listView);
			mIbtLeftArrow = (ImageButton) findViewById(R.id.activity_map_iv_leftArrow);
			mIbtRightArrow = (ImageButton) findViewById(R.id.activity_map_iv_rightArrow);
			mIbtSortAZ = (ImageButton) findViewById(R.id.activity_map_imbt_sortAZ);
			mIbtSortDistance = (ImageButton) findViewById(R.id.activity_map_imbt_sortDistance);
			mLayoutListRestaurant = (LinearLayout) findViewById(R.id.activity_map_layout_listView);
			mHorizontalListViewLayout = (RelativeLayout) findViewById(R.id.activity_map_layout_gallery);
			mLayoutMapView = (RelativeLayout) findViewById(R.id.activity_map_mapview);

			// Temporarily remove Gallery layout
			mHorizontalListViewLayout.setVisibility(View.GONE);
			// Click on item restaurant on Gallery
			// ===================================================================
			mHorizontalListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int pos, long id) {
							if (Utils.isNetworkConnected(MapViewActivity.this)) {
								Intent intent = new Intent(
										MapViewActivity.this,
										RestaurantDetailsActivity.class);
								intent.putExtra(
										StringExtraUtils.KEY_RESTAURANT,
										mListRestaurants.get(pos));
								startActivityForResult(intent,
										CodeRequest.CODE_REQUEST_MAPVIEW);
							} else {
								showToastMessage(getResources().getString(
										R.string.mess_error_network));
							}
						}
					});

			mListRestaurants = new ArrayList<RestaurantObject>();

			mHorizontalListViewAdapter = new HorizontalListViewAdapter(
					MapViewActivity.this, mListRestaurants);
			mHorizontalListView.setAdapter(mHorizontalListViewAdapter);
			mAdapterRestaurant = new RestaurantListViewAdapter(
					MapViewActivity.this, mListRestaurants, listener);
			mLvRestaurant.setAdapter(mAdapterRestaurant);

			showProgress();
			// Use MyLocation object to get the current location from GPS
			// update
			myLocation = new MyLocation();
			myLocation.getLocation(MapViewActivity.this, this);

			if (ContactObject.mLoadStatus == ContactObject.LOAD_ERROR) {
				LoadContactPhoneTask.execute(getApplicationContext());
			}

		}
	}

	// ======================================================================================

	// Destroy all objects
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProgressDialog = null;
		myLocation = null;
		if (mListRestaurants != null) {
			mListRestaurants.clear();
			mListRestaurants = null;
		}

		if (mAdapterRestaurant != null) {
			mAdapterRestaurant.clear();
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

	/**
	 * @Description : Process Back event, especially when exchanging between
	 *              MapView and ListView show
	 */
	@Override
	public void onBackPressed() {
		try {
			if (mLayoutMenu.isShown()) {
				mLayoutMenu.setVisibility(View.GONE);
			} else if (mLayoutListRestaurant.isShown()) {
				mLayoutListRestaurant.setVisibility(View.GONE);

				mBtMapView.setBackgroundResource(R.drawable.ic_tab_selected);
				mBtListView.setBackgroundResource(R.drawable.ic_tab_unselected);
			} else {
				finish();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description: Process event click on items on ListView
	 */

	private AdapterControlListener listener = new AdapterControlListener() {

		@Override
		public void clickItem(RestaurantObject obj) {
			Intent intent = new Intent(MapViewActivity.this,
					RestaurantDetailsActivity.class);
			intent.putExtra(StringExtraUtils.KEY_RESTAURANT, obj);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MAPVIEW);
		}
	};

	/**
	 * 
	 * @param idIcon
	 *            : Id of Icon
	 * @param title
	 *            : Name of restaurant
	 * @param snipse
	 *            : Address of restaurant
	 * @param point
	 *            : Location of restaurant
	 * @Description: Put marker with image icon and info window on the Map
	 */
	private void drawMarker(int idIcon, String title, String snipse,
			LatLng point) {
		try {
			// Open an InfoWindow to show restaurant information
			mMap.setInfoWindowAdapter(new BalloonsAdapter(getLayoutInflater()));
			// Creating an instance of MarkerOptions
			MarkerOptions markerOptions = new MarkerOptions();

			// Setting latitude and longitude for the marker
			markerOptions.position(point);
			markerOptions.title(title);
			markerOptions.snippet(snipse);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(idIcon));

			// Adding marker on the Google Map
			mMap.addMarker(markerOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param v
	 * @Description: Change the way of showing list of restaurant from MapView
	 *               to ListView
	 */
	public void onClick_ChangeToListView(View v) {
		try {
			mIsFromMapView = false;
			mLayoutListRestaurant.setVisibility(View.VISIBLE);
			mBtMapView.setBackgroundResource(R.drawable.ic_tab_unselected);
			mBtListView.setBackgroundResource(R.drawable.ic_tab_selected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change the way of showing list of restaurant from ListView to MapView
	 * 
	 * @param v
	 */
	public void onClick_ChangeToMapView(View v) {
		try {
			mIsFromMapView = true;
			mLayoutListRestaurant.setVisibility(View.GONE);
			mBtMapView.setBackgroundResource(R.drawable.ic_tab_selected);
			mBtListView.setBackgroundResource(R.drawable.ic_tab_unselected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param v
	 * @Description: Arrange the list of restaurant followed by name order
	 *               criteria
	 */
	public void onClick_sortByAZ(View v) {
		try {
			mIbtSortAZ.setVisibility(View.VISIBLE);
			mIbtSortDistance.setVisibility(View.INVISIBLE);
			if (mIsSortAZ) {
				mIbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_up);
				// Sorting from z to a
				mIsSortAZ = false;
				Collections.sort(mListRestaurants,
						new Comparator<RestaurantObject>() {
							@Override
							public int compare(RestaurantObject lhs,
									RestaurantObject rhs) {
								if (lhs.getNameRestaurant()
										.toLowerCase()
										.trim()
										.compareTo(
												rhs.getNameRestaurant()
														.toLowerCase().trim()) > 0)
									return -1;
								else if (lhs
										.getNameRestaurant()
										.toLowerCase()
										.trim()
										.compareTo(
												rhs.getNameRestaurant()
														.toLowerCase().trim()) < 0)
									return 1;
								else
									return 0;
							}
						});
			} else {
				mIbtSortAZ.setBackgroundResource(R.drawable.ic_arrow_down);
				// Sorting from a to z
				mIsSortAZ = true;
				Collections.sort(mListRestaurants,
						new Comparator<RestaurantObject>() {
							@Override
							public int compare(RestaurantObject lhs,
									RestaurantObject rhs) {
								if (lhs.getNameRestaurant()
										.toLowerCase()
										.trim()
										.compareTo(
												rhs.getNameRestaurant()
														.toLowerCase().trim()) > 0)
									return 1;
								else if (lhs
										.getNameRestaurant()
										.toLowerCase()
										.trim()
										.compareTo(
												rhs.getNameRestaurant()
														.toLowerCase().trim()) < 0)
									return -1;
								else
									return 0;
							}
						});
			}

			// Update ListView
			mAdapterRestaurant.notifyDataSetChanged();
			// Update Gallery one more time
			mHorizontalListViewAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param v
	 * @Description: Arrange the list of restaurant followed by nearby distance
	 *               criteria
	 */
	public void onClick_sortByDistance(View v) {
		try {
			mIbtSortDistance.setVisibility(View.VISIBLE);
			mIbtSortAZ.setVisibility(View.INVISIBLE);
			if (mIsSortNearestDistance) {
				mIbtSortDistance.setBackgroundResource(R.drawable.ic_arrow_up);
				// Sorting from the longest distance
				mIsSortNearestDistance = false;
				Collections.sort(mListRestaurants,
						new Comparator<RestaurantObject>() {
							@Override
							public int compare(RestaurantObject lhs,
									RestaurantObject rhs) {
								if (lhs.getDistance() > rhs.getDistance())
									return -1;
								else if (lhs.getDistance() < rhs.getDistance())
									return 1;
								else
									return 0;
							}
						});
			} else {
				mIbtSortDistance
						.setBackgroundResource(R.drawable.ic_arrow_down);
				// Sorting from the nearest distance
				mIsSortNearestDistance = true;
				Collections.sort(mListRestaurants,
						new Comparator<RestaurantObject>() {
							@Override
							public int compare(RestaurantObject lhs,
									RestaurantObject rhs) {
								if (lhs.getDistance() > rhs.getDistance())
									return 1;
								else if (lhs.getDistance() < rhs.getDistance())
									return -1;
								else
									return 0;
							}
						});
			}

			// Update ListView
			mAdapterRestaurant.notifyDataSetChanged();
			// Update Gallery one more time
			mHorizontalListViewAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param v
	 * @Description: Open feature Advanced Search
	 */
	public void onClick_AdvancedSearch(View v) {
		// Utils.showComingSoonDialog(MapViewActivity.this);
		startActivityForResult(new Intent(MapViewActivity.this,
				MySearchActivity.class), CodeRequest.CODE_REQUEST_MAPVIEW);
	}

	/**
	 * 
	 * @param v
	 * @Description: Click to see restaurant on the left side
	 */
	public void onClick_imbtLeftArrow(View v) {
		if (selectedImagePosition > 0) {
			selectedImagePosition--;
		}
		mHorizontalListView.setSelection(selectedImagePosition);
	}

	/**
	 * 
	 * @param v
	 * @Description: Click to see restaurant on the right side
	 */
	public void onClick_imbtRightArrow(View v) {
		if (selectedImagePosition < mListRestaurants.size() - 1) {
			selectedImagePosition++;
		}
		mHorizontalListView.setSelection(selectedImagePosition);

	}

	/**
	 * 
	 * @param v
	 * @Description: Show speak interface for users to speak and recognize
	 */
	public void onClick_SpeakRegconition(View v) {
		if (Utils.isNetworkConnected(MapViewActivity.this)) {
			checkVoiceRecognition();
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// Specify the calling package to identify your application
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
					.getPackage().getName());
			// Given an hint to the recognizer about what the user is going to
			// say
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		} else {
			Toast.makeText(MapViewActivity.this, R.string.mess_error_network,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			mBtSpeak.setEnabled(false);
			showToastMessage("Voice recognizer not present");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					mEdtSearch.setText(textMatchList.get(0));
					mKeywords = mEdtSearch.getText().toString().trim();
					new LoadMapTask().execute();
				}
				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage(getResources().getString(R.string.audioError));
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage(getResources().getString(R.string.clientError));
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage(getResources()
						.getString(R.string.networkError));
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage(getResources().getString(R.string.noMatch));
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage(getResources().getString(R.string.serverError));
			}
		} else if (requestCode == CodeRequest.CODE_REQUEST_MAPVIEW) {
			// using for logout and search advanced
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				logout();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				/*
				 * Get the list of restaurants searched by advance from
				 * DialogCreateNewProfile
				 */
				SearchProfile p = null;
				if (data != null) {
					if (data.getExtras() != null) {
						p = data.getExtras().getParcelable(
								StringExtraUtils.KEY_SEARCHPROFILE_MYSEARCH);
					}
				}
				if (p != null) {
					// Load Map again
					new LoadMapAdvancedSearchTask().execute(p);
				}

			} else if (resultCode == 0) {
				// GPS turn on -> load application again
				startActivity(new Intent(MapViewActivity.this,
						MapViewActivity.class));
				finish();
			}
		} else if (requestCode == CodeRequest.CODE_REQUEST_GOOGLEPLAY_SERVICE) {
			// Google Play Service
			if (resultCode == RESULT_CANCELED) {
				startActivity(new Intent(MapViewActivity.this,
						MapViewActivity.class));
				finish();
			}
		}
	}

	public void processData(List<RestaurantObject> mListRestaurant) {
		try {
			for (RestaurantObject r : mListRestaurants) {
				drawMarker(
						R.drawable.ic_pin,
						r.getNameRestaurant(),
						getRestaurantAddress(r.getAddress(), r.getCity(),
								r.getState()),
						new LatLng(r.getLatitude(), r.getLongitude()));

			}
			mHorizontalListViewLayout.setVisibility(View.VISIBLE);
			mHorizontalListViewAdapter.notifyDataSetChanged();
			if (mIsFromMapView) {
				mLayoutListRestaurant.setVisibility(View.GONE);
			} else {
				mLayoutListRestaurant.setVisibility(View.VISIBLE);
			}
			// Sort restaurant from the nearest to the longest
			sortNearestDistance();
			mAdapterRestaurant.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Description: Load Map AsyncTask
	 * 
	 */
	public class LoadMapTask extends AsyncTask<Void, Void, RestaurantObject[]> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected RestaurantObject[] doInBackground(Void... params) {
			try {
				if (mCurrentLocation == null) {
					return null;
				}
				InputStream source = Server
						.requestPost(
								ServerURL.getUrlNormalSearch(),
								ServerURL
										.getEntityNormalSearch(
												UserObject.getInstance(
														MapViewActivity.this)
														.getAccess_token(),
												Double.toString(
														mCurrentLocation
																.getLatitude())
														.trim(),
												Double.toString(
														mCurrentLocation
																.getLongitude())
														.trim(), mKeywords
														.trim()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();

				RestaurantObject[] responses = gson.fromJson(json,
						RestaurantObject[].class);
				return responses;
			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RestaurantObject[] result) {
			super.onPostExecute(result);
			/* Start to get data from Server */
			try {
				mHorizontalListViewLayout.setVisibility(View.GONE);
				mListRestaurants.clear();
				// Drawing markers on the map
				mMap.clear();
				if (result == null) {
					dismissProgress();
					if (!Utils.isNetworkConnected(MapViewActivity.this)) {
						showToastMessage(MapViewActivity.this.getResources()
								.getString(R.string.mess_error_network));
					} else {
						if (Server.mStatus != 200) {
							dismissProgress();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.mess_invalid_accesstoken),
									Toast.LENGTH_LONG).show();
							logout();
						} else {
							showToastMessage(MapViewActivity.this
									.getResources().getString(
											R.string.mess_error_server));
						}
					}
					return;
				} else if (result.length == 0) {
					showToastMessage(MapViewActivity.this.getResources()
							.getString(R.string.search_no_result));
					mHorizontalListViewLayout.setVisibility(View.GONE);
					mHorizontalListViewAdapter.notifyDataSetChanged();
					mAdapterRestaurant.notifyDataSetChanged();
					dismissProgress();
					return;
				}

				// Very Important!!!--> pass list of restaurants from Server

				if (mListRestaurants == null) {
					mListRestaurants = new ArrayList<RestaurantObject>();
				}
				for (int i = 0; i < result.length; i++) {
					mListRestaurants.add(result[i]);
				}

				// Process the list of restaurants and apply them into the map
				// and horizontal list view
				processData(mListRestaurants);
				// Animate map view to the nearest restaurant
				new drawMapTask()
						.execute(new LatLng(mListRestaurants.get(0)
								.getLatitude(), mListRestaurants.get(0)
								.getLongitude()));
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
			// Turn off progress dialog
			dismissProgress();
		}
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Get DEFAULT search profile and call advancedSearch service
	 *               to process Searching Advanced Mode
	 */
	public class RunDefaultProfileTask extends
			AsyncTask<Void, Void, RestaurantObject[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected RestaurantObject[] doInBackground(Void... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlRunDefaultSearch(
								UserObject.getInstance(MapViewActivity.this).mAccess_token,
								mCurrentLocation.getLatitude(),
								mCurrentLocation.getLongitude(),
								UserObject.getInstance(MapViewActivity.this).mDefaultProfileId));

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				RestaurantObject[] response = gson.fromJson(json,
						RestaurantObject[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RestaurantObject[] result) {
			/* Start to get data from Server */
			try {
				mHorizontalListViewLayout.setVisibility(View.GONE);
				mListRestaurants.clear();
				// Drawing markers on the map
				mMap.clear();
				if (result == null) {
					dismissProgress();
					if (!Utils.isNetworkConnected(MapViewActivity.this)) {
						showToastMessage(MapViewActivity.this.getResources()
								.getString(R.string.mess_error_network));
					} else {
						if (Server.mStatus != 200) {
							dismissProgress();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.mess_invalid_accesstoken),
									Toast.LENGTH_LONG).show();
							logout();
						} else {
							showToastMessage(MapViewActivity.this
									.getResources().getString(
											R.string.mess_error_server));
						}
					}

					return;
				} else if (result.length == 0) {
					showToastMessage(MapViewActivity.this.getResources()
							.getString(R.string.search_no_result));
					mHorizontalListViewAdapter.notifyDataSetChanged();
					mAdapterRestaurant.notifyDataSetChanged();
					dismissProgress();
					return;
				}
				// Very Important!!!--> pass list of restaurants from Server
				if (mListRestaurants == null)
					mListRestaurants = new ArrayList<RestaurantObject>();
				for (int i = 0; i < result.length; i++) {
					mListRestaurants.add(result[i]);
				}

				processData(mListRestaurants);
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
		}
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Process searching advanced mode
	 */
	public class LoadMapAdvancedSearchTask extends
			AsyncTask<SearchProfile, Void, RestaurantObject[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected RestaurantObject[] doInBackground(SearchProfile... params) {
			try {
				SearchProfile profile = params[0];
				// get list integer of menu_type and item_type to put into URL
				List<Integer> menu_type = new ArrayList<Integer>();
				String menu = profile.getMenuTypes();
				menu_type = Utils.parseListStringIntoListInteger(Utils
						.parseStringIntoList(menu));
				List<Integer> item_type = new ArrayList<Integer>();
				String item = profile.getItemTypes();
				item_type = Utils.parseListStringIntoListInteger(Utils
						.parseStringIntoList(item));

				InputStream source = Server.requestPost(ServerURL
						.getUrlAdvancedSearch(), ServerURL
						.getEntityAdvancedSearch(
								MapViewActivity.this,
								mCurrentLocation.getLatitude(),
								mCurrentLocation.getLongitude(),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getRestaurantRatings()))
										.get(0),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getRestaurantRatings()))
										.get(1),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getItemPrices())).get(0),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getItemPrices())).get(1),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getPointOffered())).get(0),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getPointOffered())).get(1),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getItemRatings())).get(0),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getItemRatings())).get(1),
								profile.getRadius(),
								item_type,
								menu_type,
								profile.getKeywords(),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getServerRatings())).get(0),
								Utils.parseListStringIntoListInteger(
										Utils.parseStringIntoList(profile
												.getServerRatings())).get(1)));

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				RestaurantObject[] response = gson.fromJson(json,
						RestaurantObject[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RestaurantObject[] result) {
			try {
				mHorizontalListViewLayout.setVisibility(View.GONE);
				mListRestaurants.clear();
				// Drawing markers on the map
				mMap.clear();
				if (result == null) {
					dismissProgress();
					if (!Utils.isNetworkConnected(MapViewActivity.this)) {
						showToastMessage(MapViewActivity.this.getResources()
								.getString(R.string.mess_error_network));
					} else {
						if (Server.mStatus != 200) {
							dismissProgress();
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.mess_invalid_accesstoken),
									Toast.LENGTH_LONG).show();
							logout();
						} else {
							showToastMessage(MapViewActivity.this
									.getResources().getString(
											R.string.mess_error_server));
						}
					}

					return;
				} else if (result.length == 0) {
					showToastMessage(MapViewActivity.this.getResources()
							.getString(R.string.search_no_result));
					mHorizontalListViewAdapter.notifyDataSetChanged();
					mAdapterRestaurant.notifyDataSetChanged();
					dismissProgress();
					return;
				}
				// Very Important!!!--> pass list of restaurants from Server
				if (mListRestaurants == null)
					mListRestaurants = new ArrayList<RestaurantObject>();
				for (int i = 0; i < result.length; i++) {
					mListRestaurants.add(result[i]);
				}

				processData(mListRestaurants);
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
		}
	}

	/**
	 * show progress dialog.
	 * 
	 */
	public void showProgress() {
		try {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(MapViewActivity.this);
				mProgressDialog.setCancelable(false);
				mProgressDialog.setMessage(getString(R.string.mess_waitting));
			}
			mProgressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * mIsDestroy = true;
	 * 
	 * @param msgResId
	 * @param keyListener
	 */

	/**
	 * cancel progress dialog.
	 */
	public void dismissProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	/**
	 * Show Toast message template
	 * 
	 * @param message
	 */
	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			if (mLayoutMenu.isShown()) {
				mLayoutMenu.setVisibility(View.GONE);
			} else {
				mLayoutMenu.setVisibility(View.VISIBLE);
				mLayoutMenu.startAnimation(mAnimationShow);

			}
		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MAPVIEW);

	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		Intent intent = new Intent(MapViewActivity.this,
				MyAccountActivity.class);
		intent.putExtra("LogoIndex", 1);
		startActivityForResult(intent, CodeRequest.CODE_REQUEST_MAPVIEW);
	}

	@Override
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MapViewActivity.this, null,
				"", 0, true);
		dialog.show();
	}

	@Override
	public void onIntructionsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		if (Utils.isNetworkConnected(this)) {
			Intent intent = new Intent(this, MyIntructionsActivity.class);
			startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYACCOUNT);
		} else {
			showToastMessage(getString(R.string.mess_error_network));
			return;
		}
	}

	@Override
	public void onThumbnailClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onSearchClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
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
			mDialogMyFeedback = new DialogMyFeedback(MapViewActivity.this,
					true, true, new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils.isNetworkConnected(MapViewActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MapViewActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MapViewActivity.this)) {
														new PostMyFeedBackAsynTask()
																.execute();
														mDialogConfirmRating
																.dismiss();
													} else {
														showToastMessage(getResources()
																.getString(
																		R.string.mess_error_network));
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
								showToastMessage(getResources().getString(
										R.string.mess_error_network));
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
	 * @Description: Get current location
	 */
	@Override
	public void gotLocation(Location location) {
		try {
			// Got the location!
			mCurrentLocation = location;
			if (location == null) {
				showToastMessage(getResources().getString(
						R.string.activity_map_locate_error));
				dismissProgress();
				return;
			}

			LatLng p = new LatLng(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude());
			new drawMapTask().execute(p);
			new LoadMapTask().execute();

		} catch (Exception e) {
			dismissProgress();
		}
	}

	public class drawMapTask extends AsyncTask<LatLng, Void, LatLng> {

		@Override
		protected LatLng doInBackground(LatLng... params) {
			LatLng p = params[0];
			return p;
		}

		@Override
		protected void onPostExecute(LatLng result) {
			try {
				mMap.animateCamera(CameraUpdateFactory
						.newLatLngZoom(result, 15));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

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
								UserObject.getInstance(MapViewActivity.this)
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
					if (!Utils.isNetworkConnected(MapViewActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
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

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(getResources().getString(
							R.string.mess_error_server));
					dismissProgress();
				} else {
					showToastMessage(getString(R.string.dialog_my_feedback_mess_success));
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void logout() {
		startActivity(new Intent(MapViewActivity.this, LoginActivity.class));
		finish();
	}

	public void sortNearestDistance() {
		mIbtSortDistance.setBackgroundResource(R.drawable.ic_arrow_down);
		// Sorting from the nearest distance
		mIsSortNearestDistance = true;
		Collections.sort(mListRestaurants, new Comparator<RestaurantObject>() {
			@Override
			public int compare(RestaurantObject lhs, RestaurantObject rhs) {
				if (lhs.getDistance() > rhs.getDistance())
					return 1;
				else if (lhs.getDistance() < rhs.getDistance())
					return -1;
				else
					return 0;
			}
		});
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
}