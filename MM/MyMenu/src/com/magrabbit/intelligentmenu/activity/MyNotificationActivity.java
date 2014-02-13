package com.magrabbit.intelligentmenu.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyNotificationAdapter;
import com.magrabbit.intelligentmenu.model.MyNotificationObject;
import com.magrabbit.intelligentmenu.utils.Constants;

/**
 * @author Vule
 * @Description It will store all notification of user, show new message when
 *              they have
 * 
 */
public class MyNotificationActivity extends BaseActivity implements
		OnItemClickListener {

	private List<MyNotificationObject> mLstMyNotification;
	private ListView mListview;
	// private ImageButton mImgBtn_sortByDateTime;
	// private ImageButton mImgBtn_sortDistance;
	private ImageView mIvSortStatus;
	private ImageView mIvSortStatusFc;
	private TextView mTvAmount;
	private MyNotificationAdapter mMyNotificationAdapter;
	private TextView mSortAnphabet;

	MyNotificationObject mynoti1;
	MyNotificationObject mynoti2;
	MyNotificationObject mynoti3;
	MyNotificationObject mynoti4;
	MyNotificationObject mynoti5;
	MyNotificationObject mynoti6;
	MyNotificationObject mynoti7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mynotification_global);
		mListview = (ListView) findViewById(R.id.mynotification_list_lv);
		// mImgBtn_sortByDateTime = (ImageButton)
		// findViewById(R.id.mynotification_list_ib_sort_datetime);
		// mImgBtn_sortDistance = (ImageButton)
		// findViewById(R.id.mynotification_list_ib_sort_distance);
		mIvSortStatus = (ImageView) findViewById(R.id.mynotification_iv_status_sortAphabel);
		mIvSortStatusFc = (ImageView) findViewById(R.id.mynotification_iv_status_sortAmount);
		mTvAmount = (TextView) findViewById(R.id.mynotification_sortAmount);
		mSortAnphabet = (TextView) findViewById(R.id.mynotification_sortAphabel);

		mSortAnphabet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_sortByAnphabel();
			}
		});

		mTvAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClick_sortByAmount();
			}
		});

		mLstMyNotification = new ArrayList<MyNotificationObject>();

		mynoti1 = new MyNotificationObject(R.drawable.twitter_fc, "Pizz hut",
				190, "10/04/2013", 1.2);

		mynoti2 = new MyNotificationObject(R.drawable.logo_pizahut,
				"Nice Part", 50, "09/04/2013", 7.2);

		mynoti3 = new MyNotificationObject(R.drawable.ic_restaurant_logo2,
				"Long time", 50, "12/02/2011", 4.2);

		mynoti4 = new MyNotificationObject(R.drawable.ic_restaurant_logo3,
				"WDC land", 50, "10/01/2011", 10.0);

		mynoti5 = new MyNotificationObject(R.drawable.ic_restaurant_logo4,
				"Fresh food", 10, "01/02/2011", 3.2);

		mynoti6 = new MyNotificationObject(R.drawable.ic_restaurant_logo5,
				"Fast Food", 60, "10/10/2013", 2.0);

		mynoti7 = new MyNotificationObject(R.drawable.ic_restaurant_logo6,
				"Family", 24, "01/02/2013", 2.7);

		mLstMyNotification.add(mynoti1);
		mLstMyNotification.add(mynoti2);
		mLstMyNotification.add(mynoti3);
		mLstMyNotification.add(mynoti4);
		mLstMyNotification.add(mynoti5);
		mLstMyNotification.add(mynoti6);
		mLstMyNotification.add(mynoti7);

		mMyNotificationAdapter = new MyNotificationAdapter(
				MyNotificationActivity.this, R.id.mynotification_list_lv,
				mLstMyNotification);
		mListview.setAdapter(mMyNotificationAdapter);
		mListview.setOnItemClickListener(this);
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
	protected void onDestroy() {
		if (mLstMyNotification != null) {
			mLstMyNotification.clear();
			mLstMyNotification = null;
		}

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int postion,
			long id) {
	}

	// public void onClick_sortByDate(View v) {
	// try {
	// mImgBtn_sortaz.setBackgroundResource(R.drawable.ic_sortaz);
	// // mImgBtn_sortDistance.setBackgroundResource(R.drawable.ic_group8);
	// // mImgBtn_sortByDateTime.setBackgroundResource(R.drawable.ic_success);
	// Collections.sort(mLstMyNotification,
	// new Comparator<MyNotificationObject>() {
	//
	// @Override
	// public int compare(MyNotificationObject lhs,
	// MyNotificationObject rhs) {
	//
	// @SuppressWarnings("deprecation")
	// Date d1 = new Date(lhs.getDatetimeRecent());
	//
	// @SuppressWarnings("deprecation")
	// Date d2 = new Date(rhs.getDatetimeRecent());
	// DateFormat form = new SimpleDateFormat("yyyy-MM-dd");
	// String obj1 = form.format(d1);
	// String obj2 = form.format(d2);
	// if (obj1.trim().compareTo(obj2.trim()) > 0)
	//
	// return -1;
	// else if (obj1.trim().compareTo(obj2.trim()) < 0)
	//
	// return 1;
	// else
	// return 0;
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// mMyNotificationAdapter.notifyDataSetChanged();
	// }

	// public void onClick_sortByDistance(View v) { try {
	// //mImgBtn_sortByDateTime //
	// .setBackgroundResource(R.drawable.ic_sort_date);
	// mImgBtn_sortaz.setBackgroundResource(R.drawable.ic_sortaz);
	// //mImgBtn_sortDistance.setBackgroundResource(R.drawable.ic_success);
	//
	// Collections.sort(mLstMyNotification, new
	// Comparator<MyNotificationObject>() {
	//
	// @Override public int compare(MyNotificationObject lhs,
	// MyNotificationObject rhs) { if (lhs.getDistance() > rhs.getDistance())
	// return 1; else if (lhs.getDistance() < rhs.getDistance()) return -1; else
	// return 0; } }); } catch (Exception e) { e.printStackTrace(); }
	// mMyNotificationAdapter.notifyDataSetChanged(); }

	public void onClick_sortByAnphabel() {
		try {
			mIvSortStatusFc.setBackgroundResource(R.drawable.ic_arrow_down);
			mIvSortStatus.setBackgroundResource(R.drawable.ic_arrow_up);
			Collections.sort(mLstMyNotification,
					new Comparator<MyNotificationObject>() {
						@Override
						public int compare(MyNotificationObject lhs,
								MyNotificationObject rhs) {
							if (lhs.getRestaurantName()
									.toString()
									.trim()
									.compareTo(
											rhs.getRestaurantName().toString()
													.trim()) > 0)
								return 1;
							else if (lhs
									.getRestaurantName()
									.toString()
									.trim()
									.compareTo(
											rhs.getRestaurantName().toString()
													.trim()) < 0)
								return -1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMyNotificationAdapter.notifyDataSetChanged();
	}

	public void onClick_sortByAmount() {
		try {
			mIvSortStatusFc.setBackgroundResource(R.drawable.ic_arrow_up);
			mIvSortStatus.setBackgroundResource(R.drawable.ic_arrow_down);

			Collections.sort(mLstMyNotification,
					new Comparator<MyNotificationObject>() {
						@Override
						public int compare(MyNotificationObject lhs,
								MyNotificationObject rhs) {
							if (lhs.getTotalMessage() > rhs.getTotalMessage())
								return -1;
							else if (lhs.getTotalMessage() < rhs
									.getTotalMessage())
								return 1;
							else
								return 0;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMyNotificationAdapter.notifyDataSetChanged();
	}

}
