package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.MyOrderDetailsAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback;
import com.magrabbit.intelligentmenu.customview.DialogMyFeedback.ProcessDialogFeedback;
import com.magrabbit.intelligentmenu.customview.DialogStatInfo;
import com.magrabbit.intelligentmenu.customview.OptionMenu;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFeedBackEntity;
import com.magrabbit.intelligentmenu.model.MyOrderChainObject;
import com.magrabbit.intelligentmenu.model.MyOrderDetailsEntity;
import com.magrabbit.intelligentmenu.model.OrderByLocationEntity;
import com.magrabbit.intelligentmenu.model.OrderItemEntity;
import com.magrabbit.intelligentmenu.model.ServerDetailsEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.CodeResult;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @Description Load all info of an order including receipt id, store id, all
 *              bought items,...
 */

public class MyOrderDetailsActivity extends BaseActivity implements
		OptionMenuClickListener {
	// Elements for Server
	private List<ServerDetailsEntity> mServerInfos;
	private float mSubtotal = 0.00f;
	private TextView mTvSubtotal;
	private TextView mTvTax;
	private TextView mTvTip;
	private int mTipPercent = 0;
	private TextView mTvTipPercent;
	private float mTotal = 0f;
	private TextView mTvTotal;
	private TextView mTvServerName;
	private ImageView mIvServerAvatar;

	// Elements for Order Info
	private MyOrderDetailsEntity mMyOrderDetails;
	private OrderByLocationEntity mOrderByLocationObject;
	private TextView mTvStoreId;
	private TextView mTvReceipt;
	private TextView mTvTime;
	private TextView mTvDate;
	// Elements for Restaurant Info
	private MyOrderChainObject mChainObject;
	private ImageView mIvRestaurantLogo;
	private TextView mTvRestaurantName;
	private TextView mTvRestaurantAddress;
	private TextView mTvRestaurantState;
	private ImageButton mBtnShare;
	// Process list view of Order Items
	private ListView mLvOrder;
	private MyOrderDetailsAdapter mAdapter;
	// List of order items
	private List<OrderItemEntity> mListOrderItems;
	// Loader for Images
	private ImageLoader mImageLoader;
	// For loading data first time
	private boolean mIsFirstTimeToLoad = false;
	// For share order details
	private LinearLayout mLayoutShare;
	private LinearLayout mLayoutLogoBackPress;
	private DialogMyFeedback mDialogMyFeedback;
	private DialogConFirmRating mDialogConfirmRating;
	/**
	 * Layout Menu
	 */
	private LinearLayout mLayoutMenu;
	/**
	 * Animation for Menu
	 */
	private Animation mAnimationShow;

	// Message to send order details
	private StringBuilder mOrderDetailSMS;
	private StringBuilder mOrderDetailEmail;

	SimpleDateFormat parseFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	SimpleDateFormat printFormatTime = new SimpleDateFormat("HH:mm aa");
	SimpleDateFormat printFormatDate = new SimpleDateFormat("MM.dd.yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_details);

		OptionMenu footerBar = (OptionMenu) findViewById(R.id.optionMenu);
		footerBar.initFooterBar(true, true, false, true, true, false, false,
				true, this);
		mLayoutMenu = (LinearLayout) findViewById(R.id.optionmenu);
		mAnimationShow = AnimationUtils.loadAnimation(this,
				R.anim.popup_show_down);

		this.mIsFirstTimeToLoad = true;
		mImageLoader = new ImageLoader(this);
		// Initialize list of order info
		mListOrderItems = new ArrayList<OrderItemEntity>();
		mLvOrder = (ListView) findViewById(R.id.activity_myorder_details_lv_item);
		// For Server info
		mTvServerName = (TextView) findViewById(R.id.activity_myorder_details_tv_server_name);
		mIvServerAvatar = (ImageView) findViewById(R.id.activity_myorder_details_iv_server_avatar);

		// For Order receipt info
		mTvStoreId = (TextView) findViewById(R.id.activity_myorder_details_tv_store_id);
		mTvReceipt = (TextView) findViewById(R.id.activity_myorder_details_tv_receipt_id);
		mTvTime = (TextView) findViewById(R.id.activity_myorder_details_tv_time);
		mTvDate = (TextView) findViewById(R.id.activity_myorder_details_tv_date);
		mTvSubtotal = (TextView) findViewById(R.id.activity_myorder_details_tv_subtotal);
		mTvTax = (TextView) findViewById(R.id.activity_myorder_details_tv_tax);
		mTvTip = (TextView) findViewById(R.id.activity_myorder_details_tv_tip);
		mTvTipPercent = (TextView) findViewById(R.id.activity_myorder_details_tv_tip_percent);
		mTvTotal = (TextView) findViewById(R.id.activity_myorder_details_tv_total);

		// For Restaurant info
		mIvRestaurantLogo = (ImageView) findViewById(R.id.activity_myorder_details_iv_restaurant_logo);
		mTvRestaurantName = (TextView) findViewById(R.id.activity_myorder_details_tv_restaurant_name);
		mTvRestaurantAddress = (TextView) findViewById(R.id.activity_myorder_details_tv_restaurant_address);
		mTvRestaurantState = (TextView) findViewById(R.id.activity_myorder_details_tv_restaurant_state);
		mBtnShare = (ImageButton) findViewById(R.id.activity_myorder_details_imbt_share);

		// For sharing order details
		mLayoutShare = (LinearLayout) findViewById(R.id.activity_myorder_details_layout_share);
		// For Click on MyOrder Logo
		mLayoutLogoBackPress = (LinearLayout) findViewById(R.id.activity_myorder_details_layout_LogoBack);
		mLayoutLogoBackPress.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// Get order chain object from MyOrderByLocationActivity
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mChainObject = (MyOrderChainObject) getIntent().getExtras()
					.getSerializable(StringExtraUtils.KEY_MYORDER_BYLOCATION);
			mOrderByLocationObject = (OrderByLocationEntity) getIntent()
					.getExtras().getSerializable(
							StringExtraUtils.KEY_MYORDER_DETAILS);
		}
		// Set info for Restaurant
		mImageLoader.DisplayImage(ServerURL.URL + mChainObject.getLogo(),
				mIvRestaurantLogo);
		mTvRestaurantName.setText(mChainObject.getName());
		mTvRestaurantAddress.setText(mChainObject.getAddress());
		mTvRestaurantState.setText(mChainObject.getCity() + ","
				+ mChainObject.getState() + " " + mChainObject.getZip());

		// Initialize server info
		mServerInfos = new ArrayList<ServerDetailsEntity>();
		// Call service to load the list of all order items
		new ListOrderDetails().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Call service to load the list of all order items
		// if (!mIsFirstTimeToLoad) {
		// new ListOrderDetails().execute();
		// }
		// mIsFirstTimeToLoad = false;
	}

	@Override
	public void onBackPressed() {
		if (mLayoutMenu.isShown()) {
			mLayoutMenu.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
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
	 * @Description Processing click event on layout share to share order
	 *              details by SMS or Email
	 */
	public void onClick_shareOrder(View v) {
		// Get order details for sending SMS
		getOrderDetailSMS();
		getOrderDetailEmail();
		Intent intent = new Intent(MyOrderDetailsActivity.this,
				MyOrderShare.class);
		intent.putExtra(StringExtraUtils.KEY_MYORDER_ORDER_MESSAGE_SMS,
				mOrderDetailSMS.toString());
		intent.putExtra(StringExtraUtils.KEY_MYORDER_ORDER_MESSAGE_EMAIL,
				mOrderDetailEmail.toString());
		startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYORDER_DETAILS);
	}

	/**
	 * @Description Get message containing all information of order detail to
	 *              send through SMS
	 */
	public void getOrderDetailSMS() {
		mOrderDetailSMS = new StringBuilder();
		mOrderDetailSMS.append(UserObject
				.getInstance(MyOrderDetailsActivity.this).mUserName
				+ " has shared one order detail to you as:\n"
				+ "Restaurant name: "
				+ mTvRestaurantName.getText().toString()
				+ "; Address: "
				+ mTvRestaurantAddress.getText().toString()
				+ ". \n");
		mOrderDetailSMS.append("Store #: " + mTvStoreId.getText().toString()
				+ "; Receipt #: " + mTvReceipt.getText().toString()
				+ "; Date Time: " + mTvDate.getText().toString() + " - "
				+ mTvTime.getText().toString() + ". \n");
		mOrderDetailSMS.append("Order details: \n");
		for (int i = 0; i < mListOrderItems.size(); i++) {
			String temp;
			if (mListOrderItems.get(i).getUsePoint() != 0) {
				// use_point !=0: Paid by Point
				temp = " - Points: " + mListOrderItems.get(i).getUsePoint()
						+ ". \n";
				mOrderDetailSMS.append(i
						+ 1
						+ "."
						+ mListOrderItems.get(i).getItemName()
						+ "("
						+ mListOrderItems.get(i).getNote()
						+ ") - Qty: "
						+ mListOrderItems.get(i).getQuantity()
						+ " - Rate: "
						+ Utils.exchangeRateGetExtra(mListOrderItems.get(i)
								.getRating()) + temp);
			} else {
				// use_point =0: Paid by Cash money
				temp = " - Price: $" + mListOrderItems.get(i).getPrice()
						+ ". \n";
				mOrderDetailSMS.append(i
						+ 1
						+ "."
						+ mListOrderItems.get(i).getItemName()
						+ "("
						+ mListOrderItems.get(i).getNote()
						+ ") - Qty: "
						+ mListOrderItems.get(i).getQuantity()
						+ " - Rate: "
						+ Utils.exchangeRateGetExtra(mListOrderItems.get(i)
								.getRating()) + temp);
			}

		}
		mOrderDetailSMS.append("Server: " + mTvServerName.getText().toString()
				+ ". \n");
		mOrderDetailSMS.append("Subtotal: " + mTvSubtotal.getText().toString()
				+ "; Tax: " + mTvTax.getText().toString() + "; Tip ("
				+ mTvTipPercent.getText().toString() + "): "
				+ mTvTip.getText().toString() + ". \n");
		mOrderDetailSMS.append("Total: " + mTvTotal.getText().toString());
	}

	/**
	 * @Description Get message containing all information of order detail to
	 *              send through Email
	 */
	public void getOrderDetailEmail() {
		mOrderDetailEmail = new StringBuilder();
		mOrderDetailEmail.append(UserObject
				.getInstance(MyOrderDetailsActivity.this).mUserName
				+ " has shared one order detail to you as:<br>"
				+ "<b>*General Information:</b><br>"
				+ "Restaurant name: "
				+ mTvRestaurantName.getText().toString()
				+ "<br> Address: "
				+ mTvRestaurantAddress.getText().toString());
		mOrderDetailEmail.append("<br>Store #: "
				+ mTvStoreId.getText().toString() + "<br>Receipt #: "
				+ mTvReceipt.getText().toString() + "<br>Date Time: "
				+ mTvDate.getText().toString() + " - "
				+ mTvTime.getText().toString() + ". <br>");
		mOrderDetailEmail.append("<b>*Order details:</b><br>");
		for (int i = 0; i < mListOrderItems.size(); i++) {
			String temp;
			if (mListOrderItems.get(i).getUsePoint() != 0) {
				// use_point !=0: Paid by Point
				temp = "<br>Points: " + mListOrderItems.get(i).getUsePoint()
						+ ". <br>";
				mOrderDetailEmail.append(i
						+ 1
						+ "."
						+ mListOrderItems.get(i).getItemName()
						+ "("
						+ mListOrderItems.get(i).getNote()
						+ ")<br>Qty: "
						+ mListOrderItems.get(i).getQuantity()
						+ "<br>Rate: "
						+ Utils.exchangeRateGetExtra(mListOrderItems.get(i)
								.getRating()) + temp);
			} else {
				// use_point =0: Paid by Cash money
				temp = "<br>Price: $" + mListOrderItems.get(i).getPrice()
						+ ".<br>";
				mOrderDetailEmail.append(i
						+ 1
						+ "."
						+ mListOrderItems.get(i).getItemName()
						+ "("
						+ mListOrderItems.get(i).getNote()
						+ ")<br>Qty: "
						+ mListOrderItems.get(i).getQuantity()
						+ "<br>Rate: "
						+ Utils.exchangeRateGetExtra(mListOrderItems.get(i)
								.getRating()) + temp);
			}

		}
		mOrderDetailEmail.append("<br><b>*Server:</b> "
				+ mTvServerName.getText().toString() + ".<br>");
		mOrderDetailEmail.append("<br><b>*Total detail:</b> " + "<br>Subtotal:"
				+ mTvSubtotal.getText().toString() + "<br>Tax: "
				+ mTvTax.getText().toString() + "<br>Tip ("
				+ mTvTipPercent.getText().toString() + "): "
				+ mTvTip.getText().toString() + ".<br>");
		mOrderDetailEmail.append("Total: " + mTvTotal.getText().toString()
				+ ".");
	}

	public class ListOrderDetails extends
			AsyncTask<Void, Void, MyOrderDetailsEntity> {
		@Override
		protected void onPreExecute() {
			if (mIsFirstTimeToLoad) {
				showProgress();
			}
		}

		@Override
		protected MyOrderDetailsEntity doInBackground(Void... params) {
			try {
				InputStream source = Server
						.requestGet(ServerURL.getUrlGetMyOrderDetails(
								UserObject
										.getInstance(MyOrderDetailsActivity.this).mAccess_token,
								mChainObject.getId(), mOrderByLocationObject
										.getId()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyOrderDetailsEntity response = gson.fromJson(json,
						MyOrderDetailsEntity.class);

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MyOrderDetailsEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyOrderDetailsActivity.this)) {
						showToastMessage(MyOrderDetailsActivity.this
								.getResources().getString(
										R.string.mess_error_server));
					} else {
						showToastMessage(MyOrderDetailsActivity.this
								.getResources().getString(
										R.string.mess_error_network));
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
				// List order items information
				if (mListOrderItems == null) {
					mListOrderItems = new ArrayList<OrderItemEntity>();
				}
				mListOrderItems.clear();
				mListOrderItems.addAll(result.mListItems);
				// Collect all the same item order into one
				lumpedArrayList(mListOrderItems);

				// Server information
				mServerInfos.clear();
				mServerInfos.addAll(result.mServerInfos);
				if (mServerInfos.size() != 0) {
					mTvServerName.setVisibility(View.VISIBLE);
					mIvServerAvatar.setVisibility(View.VISIBLE);
					mTvServerName.setText(mServerInfos.get(0).getName());
					mImageLoader.DisplayImage(
							ServerURL.URL + mServerInfos.get(0).getAvatar(),
							mIvServerAvatar);
				}else{
					mTvServerName.setVisibility(View.INVISIBLE);
					mIvServerAvatar.setVisibility(View.INVISIBLE);
				}

				// Set information for Order Detail
				mTvStoreId.setText(" " + result.getStoreId());
				mTvReceipt.setText(" " + result.getReceipt());

				parseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date date = parseFormat.parse(result.getOrderDate());
				mTvTime.setText(printFormatTime.format(date));
				mTvDate.setText(" " + printFormatDate.format(date));

				/*
				 * mTvTime.setText(Utils.parseTime(result.getOrderDate()));
				 * 
				 * mTvDate.setText(" " + result.getOrderDate().substring(0, 10)
				 * .replace("-", "."));
				 */
				// calculate SubTotal by adding all item price
				// for (OrderItemEntity item : mListOrderItems) {
				// if (item.getUsePoint() == 0) {
				// mSubtotal += item.getPrice() * item.getQuantity();
				// }
				// }
				mSubtotal = result.getSubTotal();
				mTvSubtotal
						.setText("$" + Utils.formatNumber(mSubtotal, "0.00"));
				mTvTax.setText("$"
						+ Utils.formatNumber(result.getTax(), "0.00"));
				mTvTip.setText("$"
						+ Utils.formatNumber(result.getTip(), "0.00"));
				if (mSubtotal != 0f) {
					mTipPercent = (int) ((result.getTip() / mSubtotal) * 100);
				} else {
					mTipPercent = 10;
				}
				mTvTipPercent.setText(" " + mTipPercent + "%");
				// mTotal = mSubtotal + result.getTax() + result.getTip();
				mTotal = result.getTotal();
				mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));

				mAdapter = new MyOrderDetailsAdapter(
						MyOrderDetailsActivity.this,
						R.layout.myorder_details_item, mListOrderItems);
				mLvOrder.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				dismissProgress();

			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}

		}

	}

	@Override
	public void onInviteClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(this, InviteFriendActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_DETAILS);
	}

	@Override
	public void onMyAccountClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		startActivityForResult(new Intent(MyOrderDetailsActivity.this,
				MyAccountActivity.class),
				CodeRequest.CODE_REQUEST_MYORDER_DETAILS);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CodeRequest.CODE_REQUEST_MYORDER_DETAILS) {
			if (resultCode == CodeRequest.CODE_RESULT_LOGOUT) {
				setResult(CodeRequest.CODE_RESULT_LOGOUT);
				finish();
			} else if (resultCode == CodeResult.CODE_RESULT_MYSEARCH_PROFILE) {
				setResult(CodeResult.CODE_RESULT_MYSEARCH_PROFILE, data);
				finish();
			}
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
	public void onNextTimeClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
	}

	@Override
	public void onStatsClickListener() {
		mLayoutMenu.setVisibility(View.GONE);
		DialogStatInfo dialog = new DialogStatInfo(MyOrderDetailsActivity.this,
				null, "", 0, true);
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
			mDialogMyFeedback = new DialogMyFeedback(
					MyOrderDetailsActivity.this, true, true,
					new ProcessDialogFeedback() {

						@Override
						public void click_Ok() {
							if (Utils
									.isNetworkConnected(MyOrderDetailsActivity.this)) {
								mDialogConfirmRating = new DialogConFirmRating(
										MyOrderDetailsActivity.this,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(MyOrderDetailsActivity.this)) {
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
										MyOrderDetailsActivity.this)
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
					if (!Utils.isNetworkConnected(MyOrderDetailsActivity.this)) {
						showToastMessage(getResources().getString(
								R.string.mess_error_network));
					} else {
						showToastMessage(getResources().getString(
								R.string.mess_error_server));
					}
					dismissProgress();
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

	public void lumpedArrayList(List<OrderItemEntity> mList) {
		for (int i = 0; i < mList.size(); i++) {
			for (int j = i + 1; j < mList.size(); j++) {
				if (mList.get(i).getId() == mList.get(j).getId()
						&& mList.get(i).getCategoryId() == mList.get(j)
								.getCategoryId()
						&& mList.get(i).getMenuId() == mList.get(j).getMenuId()
						&& (mList.get(i).getUsePoint() == 0 && mList.get(j)
								.getUsePoint() == 0)
						&& mList.get(j).getQuantity() != -1) {
					// Collect if there is the same item used by money
					mList.get(i).setQuantity(
							mList.get(i).getQuantity()
									+ mList.get(j).getQuantity());
					mList.get(j).setQuantity(-1);
				} else if (mList.get(i).getId() == mList.get(j).getId()
						&& mList.get(i).getCategoryId() == mList.get(j)
								.getCategoryId()
						&& mList.get(i).getMenuId() == mList.get(j).getMenuId()
						&& (mList.get(i).getUsePoint() != 0 && mList.get(j)
								.getUsePoint() != 0)
						&& mList.get(j).getQuantity() != -1) {
					// Collect if there is the same item used by points
					mList.get(i).setQuantity(
							mList.get(i).getQuantity()
									+ mList.get(j).getQuantity());
					mList.get(j).setQuantity(-1);
				}
			}
		}
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).getQuantity() == -1) {
				mList.remove(i);
				i--;
			}
		}
	}
}
