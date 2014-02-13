package com.magrabbit.intelligentmenu.activity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.adapter.ContextualUndoAdapter.DeleteItemCallback;
import com.magrabbit.intelligentmenu.adapter.MyOrderAdapter;
import com.magrabbit.intelligentmenu.adapter.ServerAvatarAdapter;
import com.magrabbit.intelligentmenu.adapter.ServerCommentAdapter;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacksServer;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacksServer.ProcessServerFeedBack;
import com.magrabbit.intelligentmenu.customview.DialogRating;
import com.magrabbit.intelligentmenu.customview.DialogRating.ProcessDialogConfirm_Rating;
import com.magrabbit.intelligentmenu.listener.MyOrderListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener;
import com.magrabbit.intelligentmenu.listener.SwipeListViewTouchListener.OnSwipeCallback;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.CommentsAddServerRating;
import com.magrabbit.intelligentmenu.model.CommentsServerObject;
import com.magrabbit.intelligentmenu.model.MessageErrorEntity;
import com.magrabbit.intelligentmenu.model.MyOrderEntity;
import com.magrabbit.intelligentmenu.model.MyOrderObject;
import com.magrabbit.intelligentmenu.model.PayOrderEntity;
import com.magrabbit.intelligentmenu.model.ServerEntity;
import com.magrabbit.intelligentmenu.model.UpdateOrderEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author HungHoang
 * @description List Order of restaurant.
 */
public class MyOrderActivity extends BaseActivity implements MyOrderListener,
		OnPageChangeListener {
	public static final float TIPDEFAULT_PERCENT = 0.16f;
	public static final float VALUE_TIP = 0.01f;
	private TextView mTvSubtotal;
	private TextView mTvTax;
	private TextView mTvTip;
	private TextView mTvTipPercent;
	private TextView mTvTotal;
	private TextView mTvPoint;
	private TextView mTvStore;
	private TextView mTvReceipt;
	private TextView mTvTime;
	private TextView mTvDate;
	private TextView mTvUser;
	private EditText mEtTip;
	private ImageButton mBtEditTip;
	private ImageButton mBtDoneTip;
	private Button mBtOrder;
	private ListView mLvOrder;
	private MyOrderAdapter myAdapter;
	private ImageView mIvLogo;
	private ImageView mIvAvatar;
	private float mSubtotal = 0;
	private float mTax;
	private float mTaxDefault;
	private float mTip;
	private float mTotal;
	private float mTipPercent;
	private float mPoint;
	private float mPointUser;
	private List<MyOrderObject> mListMyOrder;
	private List<MyOrderObject> mListMyOrderDefault;
	private List<Integer> mListItemOrder;
	private ImageLoader mImageLoader;
	/******************************* BaoNguyen add code here ****************************************/
	private ImageView mIvAvatarDefault;
	private DialogCustomerFeedbacksServer mDialogFeedback;
	private ImageButton mIbtChooseServer;
	private ImageButton mIbtAddFavourite;
	private ImageButton mIbtShareServer;
	private ImageButton mIbtRatingServer;
	private RelativeLayout mLayoutServer;
	private int mServerAvatarPosition = 0;
	// position of server who is chosen
	private int mServerChosenIndex = 0;
	private int mServerChosenId;
	// Server info who is shared
	private String mServerInfo;
	// Customer feedback to Servers
	private float mAvgRating;
	private List<ServerEntity> mListServers;
	private ViewPager mViewPagerAvatar;
	private ServerAvatarAdapter mServerAvatarAdapter;
	private int mLocationId;
	private int mServerId;
	private int mOrderId;
	private int mReverseSortedPositions[];
	private List<CommentsServerObject> mListServerComments;
	private ServerCommentAdapter mServerCommentAdapter;
	private boolean mIsUpdateRestaurantRating = false;
	private String mLogo;
	private DialogRating mDialogRating;
	private DialogConFirmRating mDialogConfirmRating;
	private boolean mUpdateOrder = false;
	private boolean mOnBack = false;
	private boolean mIsChange = false;
	SimpleDateFormat parseFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	SimpleDateFormat printFormatTime = new SimpleDateFormat("HH:mm aa");
	SimpleDateFormat printFormatDate = new SimpleDateFormat("MM.dd.yyyy");
	private int mPosition = -1;
	private int mIsPay = 0;
	private String mRestaurantName;

	// private SwipeListView swipeListView;
	// private int swipeMode = SwipeListView.SWIPE_MODE_LEFT;
	// private boolean swipeOpenOnLongPress = true;
	// private long swipeAnimationTime = 0;
	// private float swipeOffsetLeft = 0;
	// private float swipeOffsetRight = 0;
	// private int swipeActionLeft = SwipeListView.SWIPE_ACTION_REVEAL;
	// private int swipeActionRight = SwipeListView.SWIPE_ACTION_REVEAL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_order);
		if (mListItemOrder == null) {
			mListItemOrder = new ArrayList<Integer>();
			mListItemOrder.add(0);
		}
		if (null != getIntent().getExtras()) {
			mLogo = getIntent().getExtras().getString(
					StringExtraUtils.KEY_LOGO_RESTAURANT);
			mPoint = getIntent().getExtras().getFloat(
					StringExtraUtils.KEY_POINT);
			mLocationId = getIntent().getExtras().getInt(
					StringExtraUtils.KEY_ID_LOCATION);
			mRestaurantName = getIntent().getExtras().getString(
					StringExtraUtils.KEY_RESTAURANT_NAME);
			new getCurrentOrderTask().execute();
		}
		// Initialize list of Servers
		if (mListServers == null) {
			mListServers = new ArrayList<ServerEntity>();
		}
		mImageLoader = new ImageLoader(this);
		/****************************** BaoNguyen add code here ***************************************/
		// Inflate from layout
		mIvAvatarDefault = (ImageView) findViewById(R.id.activity_my_order_iv_avatar_default);
		mViewPagerAvatar = (ViewPager) findViewById(R.id.activity_my_order_viewpager_avatar);
		mIbtChooseServer = (ImageButton) findViewById(R.id.activity_my_order_ibt_chooseServer);
		mIbtAddFavourite = (ImageButton) findViewById(R.id.activity_my_order_ibt_favourite);
		mIbtShareServer = (ImageButton) findViewById(R.id.activity_my_order_ibt_share);
		mIbtRatingServer = (ImageButton) findViewById(R.id.activity_my_order_ibt_rating);
		mLayoutServer = (RelativeLayout) findViewById(R.id.activity_my_order_layout_server);

		/**********************************************************************************************/
		if (mListMyOrder == null) {
			mListMyOrder = new ArrayList<MyOrderObject>();
		}
		if (mListMyOrderDefault == null) {
			mListMyOrderDefault = new ArrayList<MyOrderObject>();
		}
		// swipeListView = (SwipeListView) findViewById(R.id.my_order_lv_item);
		mLvOrder = (ListView) findViewById(R.id.my_order_lv_item);
		mTvSubtotal = (TextView) findViewById(R.id.my_order_tv_subtotal);
		mTvTax = (TextView) findViewById(R.id.my_order_tv_tax);
		mTvTip = (TextView) findViewById(R.id.my_order_tv_tip);
		mTvStore = (TextView) findViewById(R.id.my_order_tv_store);
		mTvReceipt = (TextView) findViewById(R.id.my_order_tv_receipt);
		mTvTime = (TextView) findViewById(R.id.my_order_tv_time);
		mTvDate = (TextView) findViewById(R.id.my_order_tv_date);
		mTvPoint = (TextView) findViewById(R.id.my_order_tv_point);
		mTvUser = (TextView) findViewById(R.id.my_order_tv_name_user);
		mTvTipPercent = (TextView) findViewById(R.id.my_order_tv_tip_percent);
		mTvTotal = (TextView) findViewById(R.id.my_order_tv_total);
		mEtTip = (EditText) findViewById(R.id.my_order_et_tip);
		mBtDoneTip = (ImageButton) findViewById(R.id.my_order_bt_done_tip);
		mBtEditTip = (ImageButton) findViewById(R.id.my_order_bt_edit_tip);
		mBtOrder = (Button) findViewById(R.id.my_order_bt_order);
		mIvLogo = (ImageView) findViewById(R.id.my_order_iv_logo);
		mIvAvatar = (ImageView) findViewById(R.id.my_order_iv_avatar);
		mTvUser.setText(UserObject.getInstance(this).getUserName());
		try {
			mImageLoader.DisplayImage(ServerURL.URL + mLogo, mIvLogo);
			if (UserObject.getInstance(this).getProfileAvatar() != null
					&& !UserObject.getInstance(this).getProfileAvatar()
							.equalsIgnoreCase("")) {
				mIvAvatar.setScaleType(ScaleType.FIT_XY);
				mImageLoader.DisplayImage(ServerURL.URL
						+ UserObject.getInstance(this).getProfileAvatar(),
						mIvAvatar);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPointUser = mPoint;
		extraPoint(mPointUser);
		mEtTip.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		mEtTip.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					MyOrderActivity.this.getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				}
				return false;
			}
		});

		mEtTip.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					if (mListMyOrder.size() != 0 && mSubtotal > 0) {
						mTipPercent = Utils.formatPriceOrder((Float
								.parseFloat((s.toString())) / mSubtotal));
						mTotal = ((mSubtotal + Float.parseFloat((s.toString())) + mTax));

						mTvTipPercent.setText(Math.round(mTipPercent * 100)
								+ "%");
						mTvTotal.setText("$"
								+ Utils.formatNumber(mTotal, "0.00"));
					}
				} catch (NumberFormatException e) {
					mTipPercent = 0;
					mTotal = ((mSubtotal + 0 + mTax));

					mTvTipPercent.setText(Math.round(mTipPercent * 100) + "%");
					mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));
				}

			}
		});
		// swipeListView.setSwipeListViewListener(new SwipeListViewListener() {
		//
		// @Override
		// public void onStartOpen(int position, int action, boolean right) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onStartClose(int position, boolean right) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onOpened(int position, boolean toRight) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onMove(int position, float x) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onListChanged() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onLastListItem() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onFirstListItem() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onDismiss(int[] reverseSortedPositions) {
		// for (int position : reverseSortedPositions) {
		// mListMyOrder.remove(position);
		// }
		// myAdapter.notifyDataSetChanged();
		// }
		//
		// @Override
		// public void onClosed(int position, boolean fromRight) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onClickFrontView(int position) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onClickBackView(int position) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onChoiceStarted() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onChoiceEnded() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onChoiceChanged(int position, boolean selected) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public int onChangeSwipeMode(int position) {
		// // TODO Auto-generated method stub
		// return 0;
		// }
		// });
		SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(
				mLvOrder, new OnSwipeCallback() {

					@Override
					public void onSwipeRight(ListView listView,
							int[] reverseSortedPositions) {
					}

					@Override
					public void onSwipeLeft(ListView listView,
							int[] reverseSortedPositions) {
						mReverseSortedPositions = reverseSortedPositions;
						try {
							if (mListMyOrder.get(mReverseSortedPositions[0])
									.getStatus() == 1) {
								DialogConfirm d = new DialogConfirm(
										MyOrderActivity.this,
										android.R.drawable.ic_dialog_alert,
										"Delete Item",
										getString(R.string.delete_item_order),
										false, new ProcessDialogConfirm() {

											@Override
											public void click_Ok() {
											}

											@Override
											public void click_Cancel() {

											}
										});
								d.show();
							} else {
								DialogConfirm d = new DialogConfirm(
										MyOrderActivity.this,
										android.R.drawable.ic_dialog_alert,
										"Delete Item",
										getString(R.string.delete_item), true,
										new ProcessDialogConfirm() {

											@Override
											public void click_Ok() {
												for (int position : mReverseSortedPositions) {
													for (int i = 0; i < mListMyOrderDefault
															.size(); i++) {
														if (myAdapter
																.getItem(
																		position)
																.getOrderItemId() == mListMyOrderDefault
																.get(i)
																.getOrderItemId()) {
															mListMyOrderDefault
																	.get(i)
																	.setDelete(
																			1);
															break;
														}
													}
													myAdapter.remove(myAdapter
															.getItem(position));
													removeItem();
												}
												Constants.isChangeOrder = true;
												mIsChange = true;
												myAdapter
														.notifyDataSetChanged();
											}

											@Override
											public void click_Cancel() {

											}
										});
								d.show();
							}
						} catch (ActivityNotFoundException e) {
						}

					}
				}, false, false, mListItemOrder);
		mLvOrder.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		mLvOrder.setOnScrollListener(touchListener.makeScrollListener());

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

	public void onClick_AddTip(View v) {
		Constants.isChangeOrder = true;
		changeButtonOrder(true);
		if (mListMyOrder.size() != 0 && mSubtotal > 0) {
			mTipPercent += VALUE_TIP;
			mTip = (mTipPercent * mSubtotal);
			mTotal = (mSubtotal + mTip + mTax);
			mTvTipPercent.setText(Math.round(mTipPercent * 100) + "%");
			mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
			mEtTip.setText(Utils.formatNumber(mTip, "0.00") + "");
			mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));
		} else {
			mTipPercent += VALUE_TIP;
			mTvTipPercent.setText(Math.round(mTipPercent * 100) + "%");
		}
	}

	public void onClick_MinusTip(View v) {
		Constants.isChangeOrder = true;
		changeButtonOrder(true);
		if (Math.round(mTipPercent * 100) > 0) {
			if (mListMyOrder.size() != 0 && mSubtotal > 0) {
				mTipPercent -= VALUE_TIP;
				mTip = (mTipPercent * mSubtotal);
				mTotal = (mSubtotal + mTip + mTax);

				mTvTipPercent.setText(Math.round(mTipPercent * 100) + "%");
				if (mTip == 0.0) {
					mTvTip.setText("$0.00");
					mEtTip.setText("0.00");
				} else {
					mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
					mEtTip.setText(Utils.formatNumber(mTip, "0.00") + "");
				}
				mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));
			} else {
				mTipPercent -= VALUE_TIP;
				mTvTipPercent.setText(Math.round(mTipPercent * 100) + "%");
			}
		}
	}

	public void onClick_EditTip(View v) {
		if (mListMyOrder.size() != 0 && mSubtotal > 0) {
			Constants.isChangeOrder = true;
			mIsChange = true;
			changeButtonOrder(true);
			this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

			mEtTip.setVisibility(View.VISIBLE);
			mBtEditTip.setVisibility(View.GONE);
			mBtDoneTip.setVisibility(View.VISIBLE);
			mEtTip.setText(Utils.formatNumber(mTip, "0.00") + "");
			mEtTip.requestFocus();
			mEtTip.setSelection(mEtTip.getText().toString().trim().length());
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
			// only will trigger it if no physical keyboard is open
			inputMethodManager.showSoftInput(mEtTip,
					InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public void onClick_DoneTip(View v) {
		try {
			MyOrderActivity.this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			mTip = (Float.parseFloat(mEtTip.getText().toString()));
			mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
			mEtTip.setVisibility(View.GONE);
			mBtEditTip.setVisibility(View.VISIBLE);
			mBtDoneTip.setVisibility(View.GONE);
		} catch (NumberFormatException e) {
			mTip = 0;
			mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
			mEtTip.setVisibility(View.GONE);
			mBtEditTip.setVisibility(View.VISIBLE);
			mBtDoneTip.setVisibility(View.GONE);
		}
	}

	public void onClick_Cancel(View v) {
		if (mIsChange) {
			onBackPressed();
		} else {
			finish();
		}
	}

	public void onClick_Order(View v) {
		try {
			if (Utils.isNetworkConnected(MyOrderActivity.this)) {
				if (mListMyOrderDefault != null
						&& mListMyOrderDefault.size() != 0) {
					for (int i = 0; i < mListMyOrder.size(); i++) {
						for (int j = 0; j < mListMyOrderDefault.size(); j++) {
							if (mListMyOrder.get(i).getOrderItemId() == mListMyOrderDefault
									.get(j).getOrderItemId()) {
								mListMyOrderDefault.set(j, mListMyOrder.get(i));
								mListMyOrderDefault.get(j).setDelete(0);
							}

						}
					}
					if (mOnBack) {
						mUpdateOrder = false;
					} else {
						mUpdateOrder = true;
					}
					Integer[] isPay = new Integer[1];
					isPay[0] = 0;
					new updateOrderTask().execute(isPay);
				} else {
					if (mOnBack) {
						setResult(CodeRequest.CODE_RESULT_ORDER);
						finish();
					} else {
						changeButtonOrder(false);
					}
				}
			} else {
				Toast.makeText(
						this,
						MyOrderActivity.this
								.getString(R.string.mess_error_network),
						Toast.LENGTH_SHORT).show();
				if (mOnBack) {
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick_Pay(View v) {
		try {
			if (Utils.isNetworkConnected(MyOrderActivity.this)) {
				if (mListMyOrderDefault != null
						&& mListMyOrderDefault.size() != 0) {
					DialogConfirm d = new DialogConfirm(MyOrderActivity.this,
							android.R.drawable.ic_dialog_alert, "Pay",
							getString(R.string.my_order_pay_title), true,
							new ProcessDialogConfirm() {
								@Override
								public void click_Ok() {
									for (int i = 0; i < mListMyOrder.size(); i++) {
										for (int j = 0; j < mListMyOrderDefault
												.size(); j++) {
											if (mListMyOrder.get(i)
													.getOrderItemId() == mListMyOrderDefault
													.get(j).getOrderItemId()) {
												mListMyOrderDefault.set(j,
														mListMyOrder.get(i));
												mListMyOrderDefault.get(j)
														.setDelete(0);
											}

										}
									}
									if (mOnBack) {
										mUpdateOrder = false;
									} else {
										mUpdateOrder = true;
									}
									Integer[] isPay = new Integer[1];
									isPay[0] = 1;
									new updateOrderTask().execute(isPay);
								}

								@Override
								public void click_Cancel() {

								}
							});
					d.show();
				} else {
					Toast.makeText(
							this,
							MyOrderActivity.this
									.getString(R.string.my_order_no_item_pay),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(
						this,
						MyOrderActivity.this
								.getString(R.string.mess_error_network),
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recognition change of subtotal.
	 */
	@Override
	public void ChangeSubTotal(float total, boolean isChange) {
		Constants.isChangeOrder = isChange;
		mIsChange = isChange;
		changeButtonOrder(Constants.isChangeOrder);
		mSubtotal = total;
		if (mSubtotal <= 0 || mListMyOrder.size() == 0) {
			mTax = 0;
			mTip = 0;
			mTotal = 0;
		} else {
			mTax = (mTaxDefault * mSubtotal);
			mTip = (mTipPercent * mSubtotal);
			mTotal = (mSubtotal + mTip + mTax);

		}
		mTvTax.setText("$" + Utils.formatNumber(mTax, "0.00"));
		mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
		mEtTip.setText(Utils.formatNumber(mTip, "0.00") + "");
		mTvSubtotal.setText("$" + Utils.formatNumber(mSubtotal, "0.00"));
		mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));
	}

	@Override
	public void startActivityForResult() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		try {
			DialogConfirm d = new DialogConfirm(MyOrderActivity.this,
					android.R.drawable.ic_dialog_alert, "Cancel",
					getString(R.string.my_order_cancel), true,
					new ProcessDialogConfirm() {

						@Override
						public void click_Ok() {
							setResult(CodeRequest.CODE_RESULT_ORDER);
							finish();
						}

						@Override
						public void click_Cancel() {

						}
					});
			d.show();
		} catch (ActivityNotFoundException e) {
		}

	}

	@Override
	protected void onDestroy() {
		if (mListMyOrder != null) {
			mListMyOrder.clear();
			mListMyOrder = null;
		}
		super.onDestroy();
	}

	private void removeItem() {
		float total = 0;
		float totalPoint = 0;
		for (MyOrderObject item : mListMyOrder) {
			if (item.isChecked()) {
				totalPoint += item.getQuantity()
						* Utils.formatPointNumbers(item.getRedemption());
			} else {
				total += item.getQuantity() * (item.getPrice());
			}
		}

		mSubtotal = total;
		mPointUser = mPoint - totalPoint;
		mTip = (mTipPercent * mSubtotal);
		mTotal = (mSubtotal + mTip + mTax);
		if (mListMyOrder.size() == 0 || mSubtotal <= 0) {
			mTax = 0;
			mTip = 0;
			mSubtotal = 0;
			mTotal = 0;
		}
		mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));
		mTvSubtotal.setText("$" + Utils.formatNumber(mSubtotal, "0.00"));
		mTvTax.setText("$" + Utils.formatNumber(mTax, "0.00"));
		mTvTotal.setText("$" + Utils.formatNumber(mTotal, "0.00"));
		mEtTip.setText(Utils.formatNumber(mTip, "0.00") + "");
		extraPoint(mPointUser);
	}

	/**
	 * @Description Add favorite to server
	 * @param v
	 */
	public void onClick_AddServerFavourite(View v) {
		try {
			if (mListServers.size() != 0) {
				// get Server Id to pass into URL
				mServerId = mListServers.get(mServerAvatarPosition).getId();
				if (mListServers.get(mServerAvatarPosition).getIsFavourite() == 1) {
					// Call service to remove favorite of your server
					if (Utils.isNetworkConnected(MyOrderActivity.this)) {
						new AddFavouriteTask().execute(new Integer[] {
								mServerId, 0 });
					} else {
						showToastMessage(MyOrderActivity.this
								.getString(R.string.mess_error_network));
					}

				} else {
					// Call service to add favorite to your server
					if (Utils.isNetworkConnected(MyOrderActivity.this)) {
						new AddFavouriteTask().execute(new Integer[] {
								mServerId, 1 });
					} else {
						showToastMessage(MyOrderActivity.this
								.getString(R.string.mess_error_network));
					}
				}
			} else {
				showToastMessage(MyOrderActivity.this
						.getString(R.string.activity_myorder_add_favourite_confirm));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Description Rating for server who you love
	 * @param v
	 */
	public void onClick_ServerRating(View v) {
		if (mListServers.size() != 0) {
			rating();
		} else {
			showToastMessage(MyOrderActivity.this
					.getString(R.string.activity_myorder_rate_server_confirm));
		}
	}

	/**
	 * @Description Share server information via social media
	 * @param v
	 */
	public void onClick_ServerShare(View v) {
		try {
			if (mListServers.size() != 0) {
				mServerInfo = mListServers.get(mServerAvatarPosition)
						.toString();
				Intent intent = new Intent(MyOrderActivity.this,
						ShareLocationActivity.class);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_SHARE_SERVER, true);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_SERVER_INFO,
						mServerInfo);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_SERVER_NAME,
						mListServers.get(mServerAvatarPosition).getName());
				intent.putExtra(StringExtraUtils.KEY_MYORDER_SERVER_AVATAR,
						mListServers.get(mServerAvatarPosition).getAvatar());
				intent.putExtra(StringExtraUtils.KEY_RESTAURANT_NAME,
						mRestaurantName);
				intent.putExtra(StringExtraUtils.KEY_MYORDER_SERVER_TITLE,
						"Share Server Information");

				startActivityForResult(intent, CodeRequest.CODE_REQUEST_MYORDER);
			} else {
				showToastMessage(MyOrderActivity.this
						.getString(R.string.activity_myorder_share_server_confirm));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Description Click to choose server
	 * @param v
	 */
	// public void onClick_ServerChoose(View v) {
	// try {
	// if (mListServers.size() != 0) {
	// mIsChange = true;
	// changeButtonOrder(mIsChange);
	// mServerChosenIndex = mServerAvatarPosition;
	// mServerChosenId = mListServers.get(mServerAvatarPosition)
	// .getId();
	// mIbtChooseServer
	// .setBackgroundResource(R.drawable.ic_myorder_choose_focused);
	// } else {
	// showToastMessage(MyOrderActivity.this
	// .getString(R.string.activity_myorder_choose_server_confirm));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

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
										.isNetworkConnected(MyOrderActivity.this)) {
									mDialogConfirmRating = new DialogConFirmRating(
											MyOrderActivity.this,
											new ProcessDialogConfirmRating() {

												@Override
												public void click_PostIt() {
													try {
														if (Utils
																.isNetworkConnected(MyOrderActivity.this)) {
															/*
															 * Call service to
															 * rate Server
															 */
															new RateAndCommentSaveTask()
																	.execute(new Integer[] { mListServers
																			.get(mServerAvatarPosition)
																			.getId() });
															mDialogConfirmRating
																	.dismiss();
														} else {
															showToastMessage(MyOrderActivity.this
																	.getString(R.string.mess_error_network));
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
									Toast.makeText(MyOrderActivity.this,
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
					},
					13,
					"",
					getString(R.string.activity_myorder_rate_server_seekbar_title));
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialogRating.show();

	}

	public class AddFavouriteTask extends
			AsyncTask<Integer, Void, MessageErrorEntity> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MessageErrorEntity doInBackground(Integer... params) {
			try {
				int server_id = params[0];
				int is_favorite = params[1];
				InputStream source = Server
						.requestPost(
								ServerURL.getUrlAddFavoriteMyOrder(),
								ServerURL.getEntityAddFavoriteMyOrder(
										UserObject
												.getInstance(MyOrderActivity.this).mAccess_token,
										server_id, is_favorite));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				MessageErrorEntity message = gson.fromJson(json,
						MessageErrorEntity.class);
				return message;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MessageErrorEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					dismissProgress();
					if (Utils.isNetworkConnected(MyOrderActivity.this)) {
						showToastMessage(MyOrderActivity.this.getResources()
								.getString(R.string.mess_error_server));
					} else {
						showToastMessage(MyOrderActivity.this.getResources()
								.getString(R.string.mess_error_network));
					}
					return;
				}
				if (result.mStatus != null
						&& result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(MyOrderActivity.this
							.getString(R.string.common_mess_add_favourite_error));
				}
				// Set ICON
				if (mListServers.get(mServerAvatarPosition).getIsFavourite() == 1) {
					// Set icon to notify removing favorite
					mIbtAddFavourite
							.setBackgroundResource(R.drawable.ic_myorder_favourite);
					mListServers.get(mServerAvatarPosition).setIsFavourite(0);
				} else {
					// Set icon to notify setting favorite
					mIbtAddFavourite
							.setBackgroundResource(R.drawable.ic_myorder_favourite_focused);
					mListServers.get(mServerAvatarPosition).setIsFavourite(1);
				}
				dismissProgress();
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
			}
		}
	}

	/**
	 * Input filter that limits the number of decimal digits that are allowed to
	 * be entered.
	 */
	public class DecimalDigitsInputFilter implements InputFilter {

		private final int decimalDigits;

		/**
		 * Constructor.
		 * 
		 * @param decimalDigits
		 *            maximum decimal digits
		 */
		public DecimalDigitsInputFilter(int decimalDigits) {
			this.decimalDigits = decimalDigits;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			int dotPos = -1;
			int len = dest.length();
			for (int i = 0; i < len; i++) {
				char c = dest.charAt(i);
				if (c == '.' || c == ',') {
					dotPos = i;
					break;
				}
			}
			if (dotPos >= 0) {

				// protects against many dots
				if (source.equals(".") || source.equals(",")) {
					return "";
				}
				// if the text is entered before the dot
				if (dend <= dotPos) {
					return null;
				}
				if (len - dotPos > decimalDigits) {
					return "";
				}
			}

			return null;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void totalPoint(float point) {
		extraPoint(point);
	}

	public class getCurrentOrderTask extends
			AsyncTask<String, Void, MyOrderEntity[]> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected MyOrderEntity[] doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server.requestGet(ServerURL.getUrlCurrentOrder(
						MyOrderActivity.this, mLocationId));
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
					if (Utils.isNetworkConnected(MyOrderActivity.this)) {
						showToastMessage(MyOrderActivity.this.getResources()
								.getString(R.string.mess_error_server));
					} else {
						showToastMessage(MyOrderActivity.this.getResources()
								.getString(R.string.mess_error_network));
					}
					if (Server.mStatus != 200) {
						setResult(CodeRequest.CODE_RESULT_LOGOUT);
						dismissProgress();
						Toast.makeText(getApplicationContext(),
								getString(R.string.mess_invalid_accesstoken),
								Toast.LENGTH_LONG).show();
						finish();
					}
					dismissProgress();
					return;
				} else {
					if (result.length == 0 || result[0].mListItem.size() == 0) {
						if (!mUpdateOrder) {
							try {
								DialogConfirm d = new DialogConfirm(
										MyOrderActivity.this,
										android.R.drawable.ic_dialog_alert,
										"Order",
										getString(R.string.my_order_no_order),
										false, new ProcessDialogConfirm() {

											@Override
											public void click_Ok() {
												setResult(CodeRequest.CODE_RESULT_ORDER);
												finish();
											}

											@Override
											public void click_Cancel() {

											}
										});
								d.show();
								d.setOnKeyListener(new Dialog.OnKeyListener() {

									@Override
									public boolean onKey(DialogInterface arg0,
											int keyCode, KeyEvent event) {
										if (keyCode == KeyEvent.KEYCODE_BACK) {
											finish();
										}
										return true;
									}
								});

							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
							}
						} else {
							dismissProgress();
							changeButtonOrder(Constants.isChangeOrder);
						}
					} else if (result.length != 0) {
						mOrderId = result[0].mId;
						mServerId = result[0].mServerId;
						parseFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
						Date date = parseFormat.parse(result[0].mOrderDate);
						mTvStore.setText(result[0].mStoreNo + "");
						mTvReceipt.setText(result[0].mReceiptNo + "");
						mTvTime.setText(printFormatTime.format(date));
						mTvDate.setText(printFormatDate.format(date));
						// ====================================================
						if (result[0].mListItem.size() != 0) {
							mListMyOrder.clear();
							mListMyOrderDefault.clear();
							mListMyOrder.addAll(result[0].mListItem);
							mListMyOrderDefault.addAll(result[0].mListItem);
							mSubtotal = 0;
							lumpedArrayList(mListMyOrder);
							for (MyOrderObject mo : mListMyOrder) {
								mSubtotal += (mo.getPrice()) * mo.getQuantity();
							}

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

						mTvSubtotal.setText("$"
								+ Utils.formatNumber(mSubtotal, "0.00"));
						mTvTax.setText("$" + Utils.formatNumber(mTax, "0.00"));
						mTvTip.setText("$" + Utils.formatNumber(mTip, "0.00"));

						mTvTipPercent.setText(Math
								.round(TIPDEFAULT_PERCENT * 100) + "%");
						mTvTotal.setText("$"
								+ Utils.formatNumber(mTotal, "0.00"));
						/************************** BaoNguyen add code here ******************/
						// Get list of Servers and set adapter for ViewPager
						mListServers.clear();
						mListServers.addAll(result[0].mListServer);
						if (mListServers.size() == 0) {
							// mIvAvatarDefault.setVisibility(View.VISIBLE);
							// set layout of Server interaction invisible
							mLayoutServer.setVisibility(View.INVISIBLE);
							// set layout elements for display server info and
							// button for processing
							RelativeLayout.LayoutParams marginParams = (RelativeLayout.LayoutParams) mIbtAddFavourite
									.getLayoutParams();
							marginParams.setMargins(0, 15, 0, 0);
							mIbtAddFavourite.setLayoutParams(marginParams);
							// Rating Button
							marginParams = (RelativeLayout.LayoutParams) mIbtRatingServer
									.getLayoutParams();
							marginParams.setMargins(0, 15, 0, 0);
							mIbtRatingServer.setLayoutParams(marginParams);
							// Share Button
							marginParams = (RelativeLayout.LayoutParams) mIbtShareServer
									.getLayoutParams();
							marginParams.setMargins(0, 0, 0, 15);
							mIbtShareServer.setLayoutParams(marginParams);
							// Choose Button
							marginParams = (RelativeLayout.LayoutParams) mIbtChooseServer
									.getLayoutParams();
							marginParams.setMargins(0, 0, 0, 15);
							mIbtChooseServer.setLayoutParams(marginParams);

						} else {
							mIvAvatarDefault.setVisibility(View.GONE);
							mLayoutServer.setVisibility(View.VISIBLE);
						}
						mServerAvatarAdapter = new ServerAvatarAdapter(
								MyOrderActivity.this, mListServers);

						mViewPagerAvatar.setAdapter(mServerAvatarAdapter);
						mViewPagerAvatar
								.setOnPageChangeListener(MyOrderActivity.this);
						if (!mUpdateOrder) {
							myAdapter = new MyOrderAdapter(
									MyOrderActivity.this, MyOrderActivity.this,
									R.layout.my_order_item, mListMyOrder,
									mPoint);
							// swipeListView.setAdapter(myAdapter);
							// reload();
							mLvOrder.setAdapter(myAdapter);
							changeButtonOrder(Constants.isChangeOrder);
							mIsChange = false;
						} else {
							myAdapter.notifyDataSetChanged();
							mIsChange = true;
						}
						// Get Server ID which is chosen
						mServerChosenId = result[0].mServerId;
						// Set whether icon is focused or not for the FIRST
						// SERVER

						if (mListServers.size() != 0) {
							// set icon focus for favorite server
							if (mListServers.get(0).getIsFavourite() == 1) {
								mIbtAddFavourite
										.setBackgroundResource(R.drawable.ic_myorder_favourite_focused);
							} else {
								mIbtAddFavourite
										.setBackgroundResource(R.drawable.ic_myorder_favourite);
							}
							// set icon focus for chosen server for the FIRST
							// SERVER
							if (mListServers.get(0).getId() == mServerChosenId) {
								mIbtChooseServer
										.setBackgroundResource(R.drawable.ic_myorder_choose_focused);
							}

							// move the ViewPager to the selected server in the
							// past
							for (ServerEntity server : mListServers) {
								if (server.getId() == mServerChosenId) {
									mServerChosenIndex = mListServers
											.indexOf(server);
									mViewPagerAvatar
											.setCurrentItem(mListServers
													.indexOf(server));
									break;
								}
							}
						}
						dismissProgress();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class updateOrderTask extends
			AsyncTask<Integer, Void, UpdateOrderEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected UpdateOrderEntity doInBackground(Integer... params) {
			try {
				if (mListServers.size() != 0) {
					mServerId = mListServers.get(mServerAvatarPosition).getId();
				} else {
					mServerId = 0;
				}
				if (!mOnBack) {
					for (int i = 0; i < mListMyOrderDefault.size(); i++) {
						mListMyOrderDefault.get(i).setStatus(1);
					}
				}
				mIsPay = params[0];
				InputStream source = null;
				source = Server.requestPost(ServerURL.getUrlUpdateOrder(),
						ServerURL.getEntityUpdateOrder(MyOrderActivity.this,
								mOrderId, mServerId, mTip, mTax, mSubtotal,
								mTotal, mListMyOrderDefault, 1, params[0]));
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
					if (!Utils.isNetworkConnected(MyOrderActivity.this)) {
						Toast.makeText(
								MyOrderActivity.this,
								MyOrderActivity.this
										.getString(R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								MyOrderActivity.this,
								MyOrderActivity.this
										.getString(R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					if (mOnBack) {
						finish();
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
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)
						|| result.mError != null) {
					Toast.makeText(
							MyOrderActivity.this,
							MyOrderActivity.this
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
					if (mOnBack) {
						finish();
					}
				} else {
					if (mIsPay == 1) {
						Toast.makeText(MyOrderActivity.this,
								getString(R.string.my_order_pay_success),
								Toast.LENGTH_SHORT).show();
						Constants.isChangeOrder = false;
						setResult(CodeRequest.CODE_RESULT_ORDER);
						finish();
						return;
					}
					if (!mOnBack) {
						Constants.isChangeOrder = false;
						mIsChange = true;
						Toast.makeText(MyOrderActivity.this,
								getString(R.string.my_order_update),
								Toast.LENGTH_SHORT).show();
						dismissProgress();
						new getCurrentOrderTask().execute();
					} else {
						if (!Constants.isChangeOrder) {
							Constants.isChangeOrder = false;
						} else {
							Constants.isChangeOrder = true;
						}
						mIsChange = true;
						setResult(CodeRequest.CODE_RESULT_ORDER);
						finish();
					}
				}
			} catch (Exception e) {
				dismissProgress();
				e.printStackTrace();
			}
		}
	}

	public void extraPoint(float point) {
		if (point <= 1) {
			mTvPoint.setText("Point = " + Utils.formatPointNumbers(point) + "");
		} else {
			mTvPoint.setText("Points = " + Utils.formatPointNumbers(point) + "");
		}
	}

	public class RateAndCommentSaveTask extends
			AsyncTask<Integer, Void, CommentsAddServerRating> {
		int server_id;

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected CommentsAddServerRating doInBackground(Integer... params) {
			try {
				server_id = params[0];
				InputStream source = Server.requestPost(ServerURL
						.getUrlAddCommentServer(), ServerURL
						.getEntityAddCommentServer(
								UserObject.getInstance(MyOrderActivity.this)
										.getAccess_token(), server_id,
								mDialogRating.rateScore(), mDialogRating
										.rateComment()));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				CommentsAddServerRating response = gson.fromJson(json,
						CommentsAddServerRating.class);
				return response;

			} catch (Exception e) {
				e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(CommentsAddServerRating result) {
			super.onPostExecute(result);
			try {

				if (result == null) {
					Toast.makeText(MyOrderActivity.this,
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
					Toast.makeText(MyOrderActivity.this,
							result.mError.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else if (result.mStatus.equals(Constants.CONNECT_ERROR)) {
					Toast.makeText(MyOrderActivity.this,
							result.mStatus.toString(), Toast.LENGTH_SHORT)
							.show();
					dismissProgress();
				} else {
					// Get average rating of Server
					mAvgRating = result.mRating;
					// Load all customer feedback for Servers
					mIsUpdateRestaurantRating = false;
					feedback(0);
					dismissProgress();
				}
			} catch (Exception e) {
				e.toString();
				dismissProgress();
			}
		}
	}

	/**
	 * This method will be show after user click on rate score and It's have a
	 * dialog custom to show all comment by customers
	 */
	public void feedback(float ratingNumber) {
		try {
			mDialogFeedback = new DialogCustomerFeedbacksServer(
					MyOrderActivity.this,
					mListServers.get(mServerAvatarPosition),
					mIsUpdateRestaurantRating, ratingNumber,
					new ProcessServerFeedBack() {

						@Override
						public void update_rating(float ratingNumber) {
							// Set the average rating returned from
							// Server after editing
							mAvgRating = ratingNumber;
							mIsUpdateRestaurantRating = true;
							feedback(mAvgRating);
						}
					});
			mDialogFeedback.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mOnBack = true;
			if (mIsChange) {
				onClick_Order(null);
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void changeButtonOrder(boolean isChange) {
		if (isChange) {
			mBtOrder.setTextColor(getResources().getColor(R.color.White));
			mBtOrder.setClickable(true);
		} else {
			mBtOrder.setTextColor(getResources().getColor(R.color.Gray));
			mBtOrder.setClickable(false);
		}
	}

	@Override
	public void onPageSelected(int pos) {
		try {
			// indicate that there is a change in server chosen
			if (pos != mServerChosenIndex) {
				Constants.isChangeOrder = true;
				changeButtonOrder(Constants.isChangeOrder);
			}
			mServerAvatarPosition = pos;
			// set icon focus for chosen server
			if (mListServers.get(pos).getId() == mServerChosenId) {
				mIbtChooseServer
						.setBackgroundResource(R.drawable.ic_myorder_choose_focused);
			} else {
				mIbtChooseServer
						.setBackgroundResource(R.drawable.ic_myorder_choose);
			}
			// set icon focus for favorite server
			if (mListServers.get(pos).getIsFavourite() == 1) {
				mIbtAddFavourite
						.setBackgroundResource(R.drawable.ic_myorder_favourite_focused);
			} else {
				mIbtAddFavourite
						.setBackgroundResource(R.drawable.ic_myorder_favourite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class MyDeleteItemCallback implements DeleteItemCallback {

		@Override
		public void deleteItem(int position) {
			mPosition = position;
			try {
				DialogConfirm d = new DialogConfirm(MyOrderActivity.this,
						android.R.drawable.ic_dialog_alert, "Delete Item",
						getString(R.string.delete_item), true,
						new ProcessDialogConfirm() {

							@Override
							public void click_Ok() {
								for (int i = 0; i < mListMyOrderDefault.size(); i++) {
									if (myAdapter.getItem(mPosition)
											.getIdMenuItem() == mListMyOrderDefault
											.get(i).getIdMenuItem()) {
										mListMyOrderDefault.get(i).setDelete(1);
										break;
									}
								}
								myAdapter.remove(myAdapter.getItem(mPosition));
								removeItem();
								myAdapter.notifyDataSetChanged();
							}

							@Override
							public void click_Cancel() {

							}
						});
				d.show();
			} catch (ActivityNotFoundException e) {
			}
		}
	}

	private void reload() {
		// swipeListView.setSwipeMode(swipeMode);
		// swipeListView.setSwipeActionLeft(swipeActionLeft);
		// swipeListView.setSwipeActionRight(swipeActionRight);
		// swipeListView.setOffsetLeft(convertDpToPixel(swipeOffsetLeft));
		// swipeListView.setOffsetRight(convertDpToPixel(swipeOffsetRight));
		// swipeListView.setAnimationTime(swipeAnimationTime);
		// swipeListView.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public class payOrderTask extends AsyncTask<String, Void, PayOrderEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected PayOrderEntity doInBackground(String... params) {
			try {
				InputStream source = null;
				source = Server
						.requestPost(
								ServerURL.getUrlPayOrder(),
								ServerURL.getEntityPayOrder(
										UserObject
												.getInstance(MyOrderActivity.this).mAccess_token,
										mOrderId, 1));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(source).toString();
				PayOrderEntity response = gson.fromJson(json,
						PayOrderEntity.class);
				return response;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(PayOrderEntity result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					if (!Utils.isNetworkConnected(MyOrderActivity.this)) {
						Toast.makeText(
								MyOrderActivity.this,
								MyOrderActivity.this
										.getString(R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								MyOrderActivity.this,
								MyOrderActivity.this
										.getString(R.string.mess_error_server),
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
					dismissProgress();
					Toast.makeText(getApplicationContext(),
							getString(R.string.mess_invalid_accesstoken),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)
						|| result.mError != null) {
					Toast.makeText(
							MyOrderActivity.this,
							MyOrderActivity.this
									.getString(R.string.mess_error_server),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					Toast.makeText(MyOrderActivity.this,
							getString(R.string.my_order_update),
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				}
			} catch (Exception e) {
				dismissProgress();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void deleteItem(int position) {
		for (int i = 0; i < mListMyOrderDefault.size(); i++) {
			if (myAdapter.getItem(position).getOrderItemId() == mListMyOrderDefault
					.get(i).getOrderItemId()) {
				mListMyOrderDefault.get(i).setDelete(1);
				break;
			}
		}
		myAdapter.remove(myAdapter.getItem(position));
		removeItem();
		Constants.isChangeOrder = true;
		mIsChange = true;
		myAdapter.notifyDataSetChanged();
	}

	public void lumpedArrayList(List<MyOrderObject> mList) {
		for (int i = 0; i < mList.size(); i++) {
			for (int j = i + 1; j < mList.size(); j++) {
				if (mList.get(i).getIdMenuItem() == mList.get(j)
						.getIdMenuItem()
						&& mList.get(i).getIdCategory() == mList.get(j)
								.getIdCategory()
						&& mList.get(i).getIdMenu() == mList.get(j).getIdMenu()
						&& (mList.get(i).getStatus() == 1 && mList.get(j)
								.getStatus() == 1)
						&& (mList.get(i).getUsePoint() == 0 && mList.get(j)
								.getUsePoint() == 0)
						&& mList.get(j).getQuantity() != -1) {
					mList.get(i).setQuantity(
							mList.get(i).getQuantity()
									+ mList.get(j).getQuantity());
					mList.get(j).setQuantity(-1);
				} else if (mList.get(i).getIdMenuItem() == mList.get(j)
						.getIdMenuItem()
						&& mList.get(i).getIdCategory() == mList.get(j)
								.getIdCategory()
						&& mList.get(i).getIdMenu() == mList.get(j).getIdMenu()
						&& (mList.get(i).getStatus() == 1 && mList.get(j)
								.getStatus() == 1)
						&& (mList.get(i).getUsePoint() != 0 && mList.get(j)
								.getUsePoint() != 0)
						&& mList.get(j).getQuantity() != -1) {
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
