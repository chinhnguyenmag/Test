package com.magrabbit.intelligentmenu.activity;

import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.BalloonsAdapter;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;

/**
 * 
 * @author baonguyen
 * @Description: Show the restaurant followed by address on the Map View
 */
public class RestaurantMapView extends FragmentActivity {

	private GoogleMap mMap;
	private String mRestaurantAddress = "";
	private String mRestaurantName = "";
	private double mLatitude;
	private double mLongitude;
	private LatLng mLocation;

	// Decide whether getting address from RestaurantDetails or MySettings
	private boolean mFromRestaurantDetails;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_restaurant_map);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mFromRestaurantDetails = bundle
					.getBoolean(StringExtraUtils.KEY_ADDRESS_FROM_RESTAURANT);
			mRestaurantAddress = bundle.getString(StringExtraUtils.KEY_ADDRESS);
			mRestaurantName = bundle
					.getString(StringExtraUtils.KEY_RESTAURANT_NAME);
			mLatitude = bundle.getDouble(StringExtraUtils.KEY_LATITUDE);
			mLongitude = bundle.getDouble(StringExtraUtils.KEY_LONGITUDE);
		}
		/* Load all created data into Map */
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.activity_restaurant_map_view))
					.getMap();

			/* Check whether there is ZoomControl +/- or not --> GONE */
			SupportMapFragment frm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.activity_restaurant_map_view);
			View zoomControls = frm.getView().findViewById(0x1);
			if (zoomControls != null)
				zoomControls.setVisibility(View.GONE);
		}

		// Get position followed by specific address and animate map to this
		// position
		if (mFromRestaurantDetails) {
			mLocation = new LatLng(mLatitude, mLongitude);
		} else {
			mLocation = getPositionByAddress(mRestaurantAddress);
		}
		drawMarkerByAddress(mLocation);

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

	private void drawMarkerByAddress(LatLng point) {
		try {

			mMap.clear();
			// Open an InfoWindow to show restaurant information
			mMap.setInfoWindowAdapter(new BalloonsAdapter(getLayoutInflater()));
			MarkerOptions markerOptions = new MarkerOptions();

			// Setting latitude and longitude for the marker
			markerOptions.position(point);
			markerOptions.title(mRestaurantName);
			markerOptions.snippet(mRestaurantAddress);
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_pin));
			// Adding marker on the Google Map
			mMap.addMarker(markerOptions);

			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LatLng getPositionByAddress(String addressName) {
		LatLng point = null;
		Geocoder geoCoder = new Geocoder(RestaurantMapView.this,
				Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocationName(addressName,
					2);
			if (addresses.size() > 0) {
				Address returnedAddress = addresses.get(0);
				point = new LatLng(returnedAddress.getLatitude(),
						returnedAddress.getLongitude());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return point;
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (resultCode == CodeRequest.CODE_RESULT_ADDRESS) {
	// if (data != null) {
	// mAddressRestaurant = data
	// .getStringExtra(StringExtraUtils.KEY_ADDRESS);
	// }
	// }
	// }

}
