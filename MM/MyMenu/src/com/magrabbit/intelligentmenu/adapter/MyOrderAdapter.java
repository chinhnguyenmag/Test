package com.magrabbit.intelligentmenu.adapter;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacks;
import com.magrabbit.intelligentmenu.customview.DialogCustomerFeedbacks.ProcessMenuFeedBack;
import com.magrabbit.intelligentmenu.customview.DialogRating;
import com.magrabbit.intelligentmenu.customview.DialogRating.ProcessDialogConfirm_Rating;
import com.magrabbit.intelligentmenu.listener.MyOrderListener;
import com.magrabbit.intelligentmenu.model.MenuItem;
import com.magrabbit.intelligentmenu.model.MenuItemCommentEntity;
import com.magrabbit.intelligentmenu.model.MyOrderObject;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @description Adapter for MyOrder.
 * @author Hung Hoang
 * 
 */
public class MyOrderAdapter extends ArrayAdapter<MyOrderObject> {
	private LayoutInflater mLiInflater;
	private List<MyOrderObject> mData;
	private MyOrderListener mListener;
	private Activity mContext;
	private DialogRating mDialogRating;
	private DialogConFirmRating mDialogConfirmRating;
	private ProgressDialog mProgressDialog;
	private DialogCustomerFeedbacks mDialogFeedback;
	private int mPosition = -1;
	private float mPointUser;
	private float mPoint;
	private boolean mIsUpdateRestaurantRating = false;

	public MyOrderAdapter(Context mContext, MyOrderListener listener,
			int resource, List<MyOrderObject> mData, float pointUser) {
		super(mContext, resource, mData);
		this.mListener = listener;
		this.mData = mData;
		this.mContext = (Activity) mContext;
		mPoint = pointUser;
		mLiInflater = LayoutInflater.from(mContext);
		for (int i = 0; i < this.mData.size(); i++) {
			if (this.mData.get(i).getUsePoint() != 0) {
				this.mData.get(i).setChecked(true);
			} else {
				this.mData.get(i).setChecked(false);
			}
		}
		this.mPointUser = pointUser - getTotalPoint();
		mListener.totalPoint(this.mPointUser);
		mListener.ChangeSubTotal(getSubTotal(), Constants.isChangeOrder);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {

			convertView = mLiInflater.inflate(R.layout.my_order_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvQuantity = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_quantity);
			viewHolder.mTvName = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_name);
			viewHolder.mTvComment = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_comment);
			viewHolder.mTvPrice = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_price);
			viewHolder.mBtAdd = (ImageButton) convertView
					.findViewById(R.id.my_order_item_bt_add);
			viewHolder.mTvStatus = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_status);
			viewHolder.mTvPoint = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_point);
			viewHolder.mBtMinus = (ImageButton) convertView
					.findViewById(R.id.my_order_item_bt_minus);
			viewHolder.mEtComment = (EditText) convertView
					.findViewById(R.id.my_order_item_et_comment);
			viewHolder.mBtEditComment = (ImageButton) convertView
					.findViewById(R.id.my_order_bt_edit_comment);
			viewHolder.mBtDoneComment = (ImageButton) convertView
					.findViewById(R.id.my_order_bt_done_comment);
			viewHolder.mBtRate = (ImageButton) convertView
					.findViewById(R.id.my_order_item_bt_rate);
			viewHolder.mChk = (CheckBox) convertView
					.findViewById(R.id.my_order_item_cb_use_point);
			viewHolder.mTvRate = (TextView) convertView
					.findViewById(R.id.my_order_item_tv_rate);
			viewHolder.mLLUsePoint = (LinearLayout) convertView
					.findViewById(R.id.my_order_item_ll_use_point);
			Typeface type = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/amrtypen.ttf");
			viewHolder.mTvRate.setTypeface(type);
			viewHolder.mChk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int getPosition = (Integer) v.getTag();
					if (!getItem(getPosition).isChecked()) {
						if (Utils.formatPointNumbers(getItem(getPosition)
								.getRedemption())
								* getItem(getPosition).getQuantity() <= Utils
								.formatPointNumbers(mPointUser)) {
							getItem(getPosition).setChecked(true);
							getItem(getPosition).setUsePoint(
									Utils.formatPointNumbers(getItem(
											getPosition).getRedemption())
											* getItem(getPosition)
													.getQuantity());
							mPointUser -= Utils.formatPointNumbers(getItem(
									getPosition).getRedemption())
									* getItem(getPosition).getQuantity();
							mListener.ChangeSubTotal(getSubTotal(), true);
							mListener.totalPoint(mPointUser);
						} else {
							Toast.makeText(
									mContext,
									mContext.getString(R.string.my_order_point),
									Toast.LENGTH_SHORT).show();
						}
					} else {
						getItem(getPosition).setChecked(false);
						getItem(getPosition).setUsePoint(0);
						mPointUser += Utils.formatPointNumbers(getItem(
								getPosition).getRedemption())
								* getItem(getPosition).getQuantity();
						mListener.totalPoint(mPointUser);
						mListener.ChangeSubTotal(getSubTotal(), true);
					}
					notifyDataSetChanged();
				}
			});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.my_order_item_tv_name, viewHolder.mTvName);
			convertView.setTag(R.id.my_order_item_tv_quantity,
					viewHolder.mTvQuantity);
			convertView.setTag(R.id.my_order_item_tv_comment,
					viewHolder.mTvComment);
			convertView.setTag(R.id.my_order_item_tv_status,
					viewHolder.mTvStatus);
			convertView
					.setTag(R.id.my_order_item_tv_price, viewHolder.mTvPrice);
			convertView.setTag(R.id.my_order_item_et_comment,
					viewHolder.mEtComment);
			convertView
					.setTag(R.id.my_order_item_cb_use_point, viewHolder.mChk);
			convertView.setTag(R.id.my_order_item_bt_add, viewHolder.mBtAdd);
			convertView
					.setTag(R.id.my_order_item_bt_minus, viewHolder.mBtMinus);
			convertView.setTag(R.id.my_order_bt_edit_comment,
					viewHolder.mBtEditComment);
			convertView.setTag(R.id.my_order_bt_done_comment,
					viewHolder.mBtDoneComment);
			convertView.setTag(R.id.my_order_item_bt_rate, viewHolder.mBtRate);
			convertView
					.setTag(R.id.my_order_item_tv_point, viewHolder.mTvPoint);
			convertView.setTag(R.id.my_order_item_tv_rate, viewHolder.mTvRate);
			convertView.setTag(R.id.my_order_item_ll_use_point,
					viewHolder.mLLUsePoint);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// get the item from the list
		try {
			// ((SwipeListView)parent).recycle(convertView, position);
			viewHolder.mChk.setTag(position);
			viewHolder.mBtDoneComment.setTag(position);
			viewHolder.mBtEditComment.setTag(position);
			viewHolder.mBtAdd.setTag(position);
			viewHolder.mBtMinus.setTag(position);
			isEnabled(0);
			viewHolder.mTvComment.setText(mData.get(position).getComment());
			viewHolder.mTvName.setText(mData.get(position).getNameItem());
			viewHolder.mTvName.setTextColor(Color.WHITE);
			viewHolder.mTvPrice.setText(Utils.formatNumber(mData.get(position)
					.getPrice(), "0.00")
					+ "");
			viewHolder.mTvQuantity.setText("("
					+ mData.get(position).getQuantity() + ")");
			viewHolder.mTvPoint.setText(Utils.formatPointNumbers(mData.get(
					position).getRedemption()
					* mData.get(position).getQuantity())
					+ "");
			if (mData.get(position).getRedemption() == 0) {
				viewHolder.mLLUsePoint.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.mLLUsePoint.setVisibility(View.VISIBLE);
			}
			if (mData.get(position).getRate() != 0) {
				viewHolder.mBtRate.setVisibility(View.GONE);
				viewHolder.mTvRate.setVisibility(View.VISIBLE);
				viewHolder.mTvRate.setText(Utils.exchangeRateGetExtra(mData
						.get(position).getRate()));
			} else {
				viewHolder.mBtRate.setVisibility(View.VISIBLE);
				viewHolder.mTvRate.setVisibility(View.GONE);
			}
			if (mData.get(position).getStatus() == 1) {
				viewHolder.mTvStatus.setTextColor(mContext.getResources()
						.getColor(R.color.Green));
				viewHolder.mTvStatus.setText("Ordered");
				viewHolder.mBtAdd
						.setBackgroundResource(R.drawable.ic_myorder_plus_disable);
				viewHolder.mBtMinus
						.setBackgroundResource(R.drawable.ic_myorder_minus_disable);
				viewHolder.mBtEditComment.setEnabled(false);
				viewHolder.mChk.setClickable(false);

			} else {
				viewHolder.mChk.setClickable(true);
				viewHolder.mBtEditComment.setEnabled(true);
				viewHolder.mBtAdd
						.setBackgroundResource(R.drawable.button_plus_order);
				viewHolder.mBtMinus
						.setBackgroundResource(R.drawable.button_minus_order);
				viewHolder.mTvStatus.setTextColor(mContext.getResources()
						.getColor(R.color.Yellow));
				viewHolder.mTvStatus.setText("Pending");

			}
			viewHolder.mChk.setChecked(mData.get(position).isChecked());
			viewHolder.mBtAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mData.get(position).getStatus() == 0) {
						if (mData.get(position).isChecked()) {
							if (Utils.formatPointNumbers(mData.get(position)
									.getRedemption()) <= Utils
									.formatPointNumbers(mPointUser)) {
								mData.get(position).setQuantity(
										mData.get(position).getQuantity() + 1);
								viewHolder.mTvQuantity.setText("("
										+ mData.get(position).getQuantity()
										+ ")");
								mData.get(position).setUsePoint(
										Utils.formatPointNumbers(mData.get(
												position).getRedemption())
												* mData.get(position)
														.getQuantity());
								mPointUser = mPoint - getTotalPoint();
								mListener.ChangeSubTotal(getSubTotal(), true);
								mListener.totalPoint(mPointUser);
							} else {
								Toast.makeText(
										mContext,
										mContext.getString(R.string.my_order_point),
										Toast.LENGTH_SHORT).show();
							}
						} else {
							mData.get(position).setQuantity(
									mData.get(position).getQuantity() + 1);
							viewHolder.mTvQuantity.setText("("
									+ mData.get(position).getQuantity() + ")");
							mListener.ChangeSubTotal(getSubTotal(), true);
						}
						notifyDataSetChanged();
					}
				}
			});

			viewHolder.mBtMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mData.get(position).getStatus() == 0) {
						if (mData.get(position).getQuantity() > 1) {
							mData.get(position).setQuantity(
									mData.get(position).getQuantity() - 1);
							viewHolder.mTvQuantity.setText("("
									+ mData.get(position).getQuantity() + ")");
							if (mData.get(position).isChecked()) {
								mData.get(position).setUsePoint(
										Utils.formatPointNumbers(mData.get(
												position).getRedemption())
												* mData.get(position)
														.getQuantity());
								mPointUser = mPoint - getTotalPoint();
								mListener.ChangeSubTotal(getSubTotal(), true);
								mListener.totalPoint(mPointUser);
							} else {
								mListener.ChangeSubTotal(getSubTotal(), true);
							}
							notifyDataSetChanged();
						} else {
							DialogConfirm d = new DialogConfirm(mContext,
									android.R.drawable.ic_dialog_alert,
									"Delete Item", mContext
											.getString(R.string.delete_item),
									true, new ProcessDialogConfirm() {

										@Override
										public void click_Ok() {
											mListener.deleteItem(position);
										}

										@Override
										public void click_Cancel() {

										}
									});
							d.show();
						}
					}
				}
			});
			viewHolder.mBtRate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rating(position);
				}
			});
			viewHolder.mEtComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			viewHolder.mBtEditComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mData.get(position).getStatus() == 0) {
						final int getPosition = (Integer) v.getTag();
						mContext.getWindow()
								.setSoftInputMode(
										WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

						viewHolder.mEtComment.setVisibility(View.VISIBLE);
						viewHolder.mEtComment.setText(viewHolder.mTvComment
								.getText().toString());
						viewHolder.mTvComment.setVisibility(View.GONE);
						InputMethodManager imm = (InputMethodManager) mContext
								.getSystemService(Service.INPUT_METHOD_SERVICE);
						imm.showSoftInput(viewHolder.mEtComment,
								InputMethodManager.SHOW_IMPLICIT);
						viewHolder.mEtComment.clearFocus();
						viewHolder.mEtComment.requestFocus();
						viewHolder.mEtComment
								.setSelection(viewHolder.mEtComment.getText()
										.toString().trim().length());

						viewHolder.mBtDoneComment.setVisibility(View.VISIBLE);
						viewHolder.mBtEditComment.setVisibility(View.GONE);
						mListener.ChangeSubTotal(getSubTotal(), true);
						InputMethodManager inputMethodManager = (InputMethodManager) mContext
								.getSystemService(mContext.INPUT_METHOD_SERVICE);
						// only will trigger it if no physical keyboard is open
						inputMethodManager.showSoftInput(viewHolder.mEtComment,
								InputMethodManager.SHOW_IMPLICIT);
						viewHolder.mEtComment
								.addTextChangedListener(new TextWatcher() {

									@Override
									public void onTextChanged(CharSequence s,
											int start, int before, int count) {
										// TODO Auto-generated method stub

									}

									@Override
									public void beforeTextChanged(
											CharSequence s, int start,
											int count, int after) {
										// TODO Auto-generated method stub

									}

									@Override
									public void afterTextChanged(Editable s) {
										mData.get(getPosition).setComment(
												s.toString());

									}
								});
					}
				}
			});
			viewHolder.mBtDoneComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int getPosition = (Integer) v.getTag();
					mData.get(getPosition).setComment(
							viewHolder.mEtComment.getText().toString());
					viewHolder.mTvComment.setText(mData.get(getPosition)
							.getComment());

					viewHolder.mEtComment.setVisibility(View.GONE);
					viewHolder.mTvComment.setVisibility(View.VISIBLE);
					viewHolder.mBtDoneComment.setVisibility(View.GONE);
					viewHolder.mBtEditComment.setVisibility(View.VISIBLE);
					InputMethodManager in = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(
							viewHolder.mEtComment.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();

		}

		return convertView;
	}

	private float getSubTotal() {
		float total = 0;
		for (MyOrderObject item : mData) {
			if (!item.isChecked()) {
				total += item.getQuantity() * (item.getPrice());
			}
		}
		return total;
	}

	private float getTotalPoint() {
		float total = 0;
		for (MyOrderObject item : mData) {
			if (item.isChecked()) {
				total += item.getUsePoint();
			}
		}
		return total;
	}

	private class ViewHolder {
		ImageButton mBtAdd;
		ImageButton mBtMinus;
		ImageButton mBtEditComment;
		ImageButton mBtDoneComment;
		ImageButton mBtRate;
		Button mBtDelete;
		TextView mTvName;
		TextView mTvStatus;
		TextView mTvQuantity;
		TextView mTvPrice;
		TextView mTvComment;
		TextView mTvPoint;
		TextView mTvRate;
		EditText mEtComment;
		CheckBox mChk;
		LinearLayout mLLUsePoint;
	}

	/**
	 * This method use to rate and comment for customer
	 */
	public void rating(int position) {
		mPosition = position;
		try {
			mDialogRating = new DialogRating(mContext, false,
					new ProcessDialogConfirm_Rating() {

						@Override
						public void click_Submit(int valueRating, String comment) {
							if (Utils.isNetworkConnected(mContext)) {
								mDialogConfirmRating = new DialogConFirmRating(
										mContext,
										new ProcessDialogConfirmRating() {

											@Override
											public void click_PostIt() {
												try {
													if (Utils
															.isNetworkConnected(mContext)) {
														new AddCommentRatingTask().execute(mData
																.get(mPosition));
														mDialogConfirmRating
																.dismiss();
													} else {
														Toast.makeText(
																mContext,
																mContext.getResources()
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
										mContext,
										mContext.getResources().getString(
												R.string.mess_error_network),
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void click_Cancel() {
							mDialogRating.dismiss();
						}
					}, 13, "", mContext.getResources().getString(
							R.string.custom_seebar_rate_tittle_menuitem));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDialogRating.show();
	}

	public class AddCommentRatingTask extends
			AsyncTask<MyOrderObject, Void, MenuItemCommentEntity> {
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		//
		@Override
		protected MenuItemCommentEntity doInBackground(MyOrderObject... params) {
			try {
				InputStream source = null;
				source = Server
						.requestPost(ServerURL.getUrlAddComment(params[0]
								.getIdMenuItem()), ServerURL
								.getEntityAddComment(
										UserObject.getInstance(mContext)
												.getAccess_token(),
										mDialogRating.rateScore(),
										mDialogRating.rateComment(), params[0]
												.getIdCategory(), params[0]
												.getIdMenu()));
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
					if (!Utils.isNetworkConnected(mContext)) {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.mess_error_server),
								Toast.LENGTH_SHORT).show();
					}
					dismissProgress();
					return;
				}

				if (result.mStatus.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					Toast.makeText(mContext, "Connection failed !",
							Toast.LENGTH_SHORT).show();
					dismissProgress();
				} else {
					try {
						if (Utils.isNetworkConnected(mContext)) {
							mData.get(mPosition).setRate(result.mRating);
							feedback(0);
							notifyDataSetChanged();
						} else {
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.mess_error_network),
									Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					dismissProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * show progress dialog.
	 * 
	 */
	public void showProgress() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getResources().getString(
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

	/**
	 * This method will be show after user click on rate score and It's have a
	 * dialog custom to show all comment by customers
	 */
	public void feedback(float ratingNumber) {
		try {
			MenuItem menuItem = new MenuItem();
			menuItem.setId(mData.get(mPosition).getIdMenuItem());
			menuItem.setCategory_id(mData.get(mPosition).getIdCategory());
			menuItem.setMenuId(mData.get(mPosition).getIdMenu());
			mDialogFeedback = new DialogCustomerFeedbacks(mContext, menuItem,
					mIsUpdateRestaurantRating, ratingNumber,
					new ProcessMenuFeedBack() {

						@Override
						public void update_rating(float ratingNumber) {
							// ratings again
							mDialogFeedback.dismiss();
							feedback(0);
						}
					});
			mDialogFeedback.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getQuantity(String s) {
		try {
			s = s.replace("(", "");
			s = s.replace(")", "");
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		for (int i = 0; i < this.mData.size(); i++) {
			if (this.mData.get(i).getUsePoint() != 0) {
				this.mData.get(i).setChecked(true);
			} else {
				this.mData.get(i).setChecked(false);
			}
		}
		mListener.ChangeSubTotal(getSubTotal(), Constants.isChangeOrder);
	}

}
