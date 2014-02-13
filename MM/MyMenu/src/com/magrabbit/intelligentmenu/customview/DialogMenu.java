package com.magrabbit.intelligentmenu.customview;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.activity.MyOrderActivity;
import com.magrabbit.intelligentmenu.activity.MySearchActivity;
import com.magrabbit.intelligentmenu.activity.MyOrderActivity.getCurrentOrderTask;
import com.magrabbit.intelligentmenu.activity.MyOrderActivity.updateOrderTask;
import com.magrabbit.intelligentmenu.adapter.MenuCategoriesAdapter;
import com.magrabbit.intelligentmenu.adapter.MyOrderAdapter;
import com.magrabbit.intelligentmenu.adapter.ServerAvatarAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.listener.MyOrderListener;
import com.magrabbit.intelligentmenu.model.CategoryObject;
import com.magrabbit.intelligentmenu.model.MyOrderEntity;
import com.magrabbit.intelligentmenu.model.MyOrderObject;
import com.magrabbit.intelligentmenu.model.SearchMenuItemEntity;
import com.magrabbit.intelligentmenu.model.ServerEntity;
import com.magrabbit.intelligentmenu.model.UpdateOrderEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author Hung Hoang
 * 
 */
public class DialogMenu extends BaseDialog implements OnClickListener {
	private ProcessDialogMenu process;

	private Activity mActivity;
	private ListView mLvMenuRestaurant;
	private LinearLayout mLayoutHome;
	// Elements from MySearch function
	public static EditText mEdtSearch;
	private ImageButton mIBtSpeak;
	private ImageButton mIbOrder;
	private ImageButton mIbPay;
	private ImageButton mIbStartOver;
	private ImageButton mIbSearch;
	private ImageView mIvLogo;
	private ImageButton mIbMySearch;
	private MenuCategoriesAdapter mMyAdapter;
	private List<CategoryObject> mListCategory;
	private String mLogoRestaurant;
	private int mIdRestaurant;
	private MyOrderListener mListener;
	private String mWebsite;
	private String mDinnerStatus;
	private float mPointNumber;
	private boolean isActivityFavorite;

	// ==================================================================
	public static final float TIPDEFAULT_PERCENT = 0.16f;
	public static final float VALUE_TIP = 0.01f;
	private float mSubtotal = 0;
	private float mTax;
	private float mTaxDefault;
	private float mTip;
	private float mTotal;
	private float mTipPercent;
	private float mPoint;
	private float mPointUser;
	private int mLocationId;
	private int mServerId;
	private int mOrderId;
	private List<MyOrderObject> mListMyOrder;
	private List<ServerEntity> mListServers;

	// to distinguish opening DialogMenu from RestaurantDetail or MenuItem
	private int mFromRestaunt;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogMenu(Context context, List<CategoryObject> data, String mLogo,
			String website, ProcessDialogMenu pro, MyOrderListener listener,
			String dinnerStatus, float pointNumber, int idRestaurant,
			boolean isActivityFavorite, String mContextSearch,
			int fromRestaurant_OR_MenuItem) {
		super(context);
		this.process = pro;
		mActivity = (Activity) context;
		this.mWebsite = website;
		this.mDinnerStatus = dinnerStatus;
		this.mPointNumber = pointNumber;
		this.mListener = listener;
		this.mIdRestaurant = idRestaurant;
		this.isActivityFavorite = isActivityFavorite;
		this.mFromRestaunt = fromRestaurant_OR_MenuItem;
		if (mListCategory == null) {
			mListCategory = new ArrayList<CategoryObject>();
		}
		mListCategory.clear();
		mListCategory.addAll(data);

		this.mLogoRestaurant = mLogo;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in dialog_custom_menu_restaurant.xml file */
		setContentView(R.layout.dialog_custom_menu_restaurant);

		mLayoutHome = (LinearLayout) findViewById(R.id.dialog_custom_menu_restaurant_ll_home);
		mIbOrder = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_order);
		mIbPay = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_pay);
		mIbStartOver = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_start_over);
		mIbMySearch = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_mySearch);
		mIbSearch = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_Search);
		mLvMenuRestaurant = (ListView) findViewById(R.id.dialog_custom_menu_restaurant_lv);
		mIvLogo = (ImageView) findViewById(R.id.dialog_custom_menu_restaurant_ib_logo);

		// mIvLogo.setImageResource(mLogoRestaurant);

		// Elements from MySearch function
		mEdtSearch = (EditText) this
				.findViewById(R.id.dialog_custom_menu_restaurant_edtSearch);
		mEdtSearch.setText(mContextSearch);
		mEdtSearch.requestFocus();

		mIBtSpeak = (ImageButton) findViewById(R.id.dialog_custom_menu_restaurant_ib_Speak);
		try {
			if (Utils.isNetworkConnected(mActivity)) {
				new DownloadImageTask(mIvLogo).execute(ServerURL.URL
						+ this.mLogoRestaurant);
			} else {
				Toast.makeText(mActivity, R.string.mess_error_network,
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mIbMySearch.setOnClickListener(this);
		mIBtSpeak.setOnClickListener(this);
		mIbOrder.setOnClickListener(this);
		mIbPay.setOnClickListener(this);
		mIbStartOver.setOnClickListener(this);
		mLayoutHome.setOnClickListener(this);
		mIbSearch.setOnClickListener(this);
		// mEdtSearch.setDrawableClickListener(this);
		mMyAdapter = new MenuCategoriesAdapter(context,
				R.layout.menu_item_restaurant, mListCategory, pro,
				mLogoRestaurant, mWebsite, mDinnerStatus, mPointNumber,
				mIdRestaurant, DialogMenu.this.isActivityFavorite);
		mLvMenuRestaurant.setAdapter(mMyAdapter);

		mLvMenuRestaurant.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (mListCategory.get(position).getNumberItem() != 0) {
					process.click_Dialog(mLogoRestaurant,
							mListCategory.get(position).getmId(), mListCategory
									.get(position).getmNameCategory(),
							mWebsite, mDinnerStatus, mPointNumber,
							mIdRestaurant, false, false,
							DialogMenu.this.isActivityFavorite);
					dismiss();
				} else {
					Toast.makeText(mActivity,
							mActivity.getString(R.string.menuitem_search_item),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.dialog_custom_menu_restaurant_ib_order:
			this.mListener.startActivityForResult();
			break;
		case R.id.dialog_custom_menu_restaurant_ib_pay:
			try {
				if (Utils.isNetworkConnected(mActivity)) {
					new getCurrentOrderTask().execute();
				} else {
					Toast.makeText(mActivity, R.string.mess_error_network,
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.dialog_custom_menu_restaurant_ib_start_over:
			process.click_StartOver();
			break;
		case R.id.dialog_custom_menu_restaurant_ll_home:
			process.click_Home();
			break;
		case R.id.dialog_custom_menu_restaurant_ib_mySearch:
			// Change to Advanced Search
			Intent intent = new Intent(mActivity, MySearchActivity.class);
			if (mFromRestaunt == CodeRequest.CODE_REQUEST_RESTAURANTDETAIL) {
				mActivity.startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_RESTAURANTDETAIL);
			} else if (mFromRestaunt == CodeRequest.CODE_REQUEST_MENUITEM) {
				mActivity.startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_MENUITEM);
			}
			break;
		case R.id.dialog_custom_menu_restaurant_ib_Speak:
			process.click_Speak();
			break;
		case R.id.dialog_custom_menu_restaurant_ib_Search:
			try {
				if (!mEdtSearch.getText().toString().equals("")) {
					if (Utils.isNetworkConnected(mActivity)) {
						new SearchItemTask().execute();
					} else {
						Toast.makeText(mActivity, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mActivity,
							R.string.dialog_custom_menu_err_search,
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	/* Get URL string to search */

	public static abstract class ProcessDialogMenu {
		public abstract void click_Dialog(String logo, int idCategory,
				String nameCategory, String website, String dinnerStatus,
				float pointNumber, int mIdRestaurant, boolean isFavorite,
				boolean isNexttime, boolean isActivityFavorite);

		public abstract void click_Speak();

		public abstract void click_StartOver();

		public abstract void click_Home();

		public abstract void click_Order();

		public abstract void click_MenuSearchItem(String logo,
				int isSearchMenuItem, String website, String dinnerStatus,
				float pointNumber, int mIdRestaurant, String keySearch,
				boolean isActivityFavorite,
				SearchMenuItemEntity searchMenuItemEntity);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		mMyAdapter.resetData();
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

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		InputStream in;
		Bitmap mIcon11;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected void onPreExecute() {
			bmImage.setImageResource(R.drawable.ic_load_img_150);
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			mIcon11 = null;
			try {
				in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());

				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			try {
				if (result == null) {
					bmImage.setImageResource(R.drawable.ic_load_img_150);
				} else {
					bmImage.setImageBitmap(result);
				}
				if (in != null) {
					in.close();
					mIcon11 = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public class SearchItemTask extends
			AsyncTask<String, Void, SearchMenuItemEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected SearchMenuItemEntity doInBackground(String... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlSearchMenuItem(mIdRestaurant, UserObject
								.getInstance(mActivity).getAccess_token(),
								mEdtSearch.getText().toString()));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SearchMenuItemEntity response = gson.fromJson(json,
						SearchMenuItemEntity.class);
				return response;

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
					if (!Utils.isNetworkConnected(mActivity)) {
						Toast.makeText(
								mActivity,
								mActivity.getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								mActivity,
								mActivity.getResources().getString(
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
					mActivity.setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(
							mActivity.getApplicationContext(),
							mActivity
									.getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					mActivity.finish();
					return;
				}

				if (result.mError != null) {
					Toast.makeText(mActivity, R.string.mess_error_server,
							Toast.LENGTH_SHORT).show();
					dismissProgress();
					return;
				} else {

					if (result.mListMenuItem != null
							&& result.mListMenuItem.size() != 0) {
						process.click_MenuSearchItem(mLogoRestaurant,
								Constants.CATEGORY_FROM_SEARCH_MENU_ITEM,
								mWebsite, mDinnerStatus, mPointNumber,
								mIdRestaurant, mEdtSearch.getText().toString(),
								DialogMenu.this.isActivityFavorite, result);
					} else {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.menuitem_search_item),
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

	public class updateOrderTask extends
			AsyncTask<Integer, Void, UpdateOrderEntity> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		//
		@Override
		protected UpdateOrderEntity doInBackground(Integer... params) {
			try {
				if (mServerId == 0) {
					if(mListServers!=null&&mListServers.size()!=0){
						mServerId = mListServers.get(0).getId();
					}
				}

				for (int i = 0; i < mListMyOrder.size(); i++) {
					mListMyOrder.get(i).setStatus(1);
				}
				InputStream source = null;
				source = Server.requestPost(ServerURL.getUrlUpdateOrder(),
						ServerURL.getEntityUpdateOrder(mActivity, mOrderId,
								mServerId, mTip, mTax, mSubtotal, mTotal,
								mListMyOrder, 1, 1));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				UpdateOrderEntity response = gson.fromJson(json,
						UpdateOrderEntity.class);
				return response;

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(UpdateOrderEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(mActivity)) {
						Toast.makeText(
								mActivity,
								mActivity
										.getString(R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								mActivity,
								mActivity.getString(R.string.mess_error_server),
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
					mActivity.setResult(CodeRequest.CODE_RESULT_LOGOUT);
					dismissProgress();
					Toast.makeText(
							mActivity,
							mActivity
									.getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					mActivity.finish();
					return;
				}
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)
						|| result.mError != null) {
					Toast.makeText(mContext,
							mContext.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Toast.makeText(mActivity, R.string.my_order_pay_success,
							Toast.LENGTH_SHORT).show();
					Constants.isChangeOrder = false;
					process.click_Order();
					dismissProgress();
				}
			} catch (Exception e) {
				dismissProgress();
				e.printStackTrace();
			}
		}
	}

	public class getCurrentOrderTask extends
			AsyncTask<String, Void, MyOrderEntity[]> {
		@Override
		protected void onPreExecute() {
			showProgress(R.string.mess_waitting);
		}

		@Override
		protected MyOrderEntity[] doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server.requestGet(ServerURL.getUrlCurrentOrder(
						mContext, mIdRestaurant));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MyOrderEntity[] response = gson.fromJson(json,
						MyOrderEntity[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(MyOrderEntity[] result) {
			try {
				if (result == null) {
					if (Utils.isNetworkConnected(mActivity)) {
						Toast.makeText(mActivity, R.string.mess_error_server,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mActivity, R.string.mess_error_network,
								Toast.LENGTH_SHORT).show();
					}
					if (Server.mStatus != 200) {
						mActivity.setResult(CodeRequest.CODE_RESULT_LOGOUT);
						dismissProgress();
						Toast.makeText(
								mActivity,
								mActivity
										.getString(R.string.mess_invalid_accesstoken),
								Toast.LENGTH_LONG).show();
						mActivity.finish();
					}
					dismissProgress();
					return;
				} else {
					if (result.length == 0 || result[0].mListItem.size() == 0) {
						try {
							DialogConfirm d = new DialogConfirm(
									mActivity,
									android.R.drawable.ic_dialog_alert,
									"Order",
									mActivity
											.getString(R.string.my_order_no_order),
									false, new ProcessDialogConfirm() {

										@Override
										public void click_Ok() {
											dismissProgress();
										}

										@Override
										public void click_Cancel() {

										}
									});
							d.show();
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
						}
					} else if (result.length != 0) {
						mOrderId = result[0].mId;
						mServerId = result[0].mServerId;
						if (mListServers == null) {
							mListServers = new ArrayList<ServerEntity>();
						}
						mListServers.clear();
						mListServers.addAll(result[0].mListServer);
						// ====================================================
						if (result[0].mListItem.size() != 0) {
							if (mListMyOrder == null) {
								mListMyOrder = new ArrayList<MyOrderObject>();
							}
							mListMyOrder.clear();
							mListMyOrder.addAll(result[0].mListItem);
							mSubtotal = getSubTotal();
							mTaxDefault = Utils
									.formatPriceOrder(result[0].tax / 100);
							mTax = (mTaxDefault * mSubtotal);
							if (result[0].mTip != 0) {
								mTipPercent = Utils
										.formatPriceOrder(result[0].mTip
												/ mSubtotal);
							} else {
								mTipPercent = TIPDEFAULT_PERCENT;
							}
							mTip = (mTipPercent * mSubtotal);
							mTotal = (mSubtotal + mTip + mTax);
						} else {
							mTipPercent = TIPDEFAULT_PERCENT;
							mSubtotal = 0;
							mTax = 0;
							mTip = 0;
							mTotal = 0;
						}
						try {
							if (Utils.isNetworkConnected(mActivity)) {
								DialogConfirm d = new DialogConfirm(mActivity,
										android.R.drawable.ic_dialog_alert, "Pay",
										mActivity.getString(R.string.my_order_pay_title), true,
										new ProcessDialogConfirm() {
											@Override
											public void click_Ok() {
												new updateOrderTask().execute();
											}

											@Override
											public void click_Cancel() {

											}
										});
								d.show();
							} else {
								Toast.makeText(mActivity,
										R.string.mess_error_network,
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissProgress();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private float getSubTotal() {
		float total = 0;
		for (MyOrderObject item : mListMyOrder) {
			if (item.getUsePoint() == 0) {
				total += item.getQuantity() * (item.getPrice());
			}
		}
		return total;
	}
}
