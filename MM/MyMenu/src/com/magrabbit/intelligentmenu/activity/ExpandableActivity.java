package com.magrabbit.intelligentmenu.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.ExpandableListAdapter;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;

/**
 * 
 * @author baonguyen
 * @description: Activity for showing searching criteria.
 */
public class ExpandableActivity extends BaseActivity {

	/**
	 * Elements can be inflated from layout
	 */
	private ImageView mIvResLogo;
	private ImageView mIvUserLogo;
	private TextView mTvStatus;
	private TextView mTvPoint;

	private ExpandableListAdapter mListAdapter;
	private ExpandableListView mExpListView;
	private List<String> mListDataHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable);

		// Inflate from layout
		mIvResLogo = (ImageView) findViewById(R.id.activity_expandable_restaurantLogo);
		// mIvResLogo.setImageResource(RestaurantObject.getInstance().getLogoRestaurant());
		mIvUserLogo = (ImageView) findViewById(R.id.activity_expandable_userLogo);
		// Get Avatar from UserObject
		// UserObject.getInstance().getProfileAvatar();
		mTvStatus = (TextView) findViewById(R.id.activity_expandable_status);
		// mTvStatus.setText(UserObject.getInstance().getUserStatus());
		mTvPoint = (TextView) findViewById(R.id.activity_expandable_point);
		// mTvPoint.setText(Integer.toString(UserObject.getInstance().getPoints()));

		// get the listview
		mExpListView = (ExpandableListView) findViewById(R.id.lvExp);
		// preparing list data
		prepareListData();

		// mListAdapter = new ExpandableListAdapter(this, mListDataHeader);
		// setting list adapter
		mExpListView.setAdapter(mListAdapter);

		// Set GroupListView Indicator --> Null
		mExpListView.setGroupIndicator(null);

		// Listview Group click listener
		mExpListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				if (groupPosition == 2) {
					startActivityForResult(new Intent(ExpandableActivity.this,
							AccountSettingActivity.class),
							CodeRequest.CODE_REQUEST_EXPANDABLE);
				} else if (groupPosition == 4) {
					startActivityForResult(new Intent(ExpandableActivity.this,
							MyFriendsActivity.class),
							CodeRequest.CODE_REQUEST_EXPANDABLE);
				} else if (groupPosition == 1) {
					startActivityForResult(new Intent(ExpandableActivity.this,
							MyNotificationGlobalActivity.class),
							CodeRequest.CODE_REQUEST_EXPANDABLE);
				}
				return false;
			}
		});

		// Listview Group expanded listener
		mExpListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {

				return;
			}
		});

		// Listview Group collasped listener
		mExpListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {

				return;
			}
		});
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
	 * Destroy all list of items right here
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mListDataHeader != null) {
			mListDataHeader.clear();
			mListDataHeader = null;
		}
	}

	/**
	 * @Description: Put data info into list of Group Header and Child Header
	 */
	private void prepareListData() {
		mListDataHeader = new ArrayList<String>();
		// mListDataChild = new HashMap<String, List<String>>();

		// Adding child data
		mListDataHeader.add("MyPoints");
		mListDataHeader.add("MyNotifications");
		mListDataHeader.add("MySettings");
		mListDataHeader.add("MySearch");
		mListDataHeader.add("MyFriends");
		mListDataHeader.add("MyWallet");
		mListDataHeader.add("MyFavorites");
		mListDataHeader.add("MyCalories");

		// List<String> menuItem = new ArrayList<String>();
		// menuItem.add("Price");
		// menuItem.add("Category");
		// menuItem.add("Tag");
		//
		// mListDataChild.put(mListDataHeader.get(2), menuItem);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_EXPANDABLE) {
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
		}

	}
}
