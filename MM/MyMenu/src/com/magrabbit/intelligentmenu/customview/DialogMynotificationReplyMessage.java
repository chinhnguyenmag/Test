package com.magrabbit.intelligentmenu.customview;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyNotificationMessageListEntity;
import com.magrabbit.intelligentmenu.model.MyNotificationRestaurantListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author vule
 * 
 */
public class DialogMynotificationReplyMessage extends Dialog implements
		android.view.View.OnClickListener {

	private Activity mActivity;
	private Button mBtSubmit;
	private Button mBtCancel;
	private EditText mTvComment;
	private CheckBox mCbAccept;
	private TextView mTvPoint;
	private ImageView mIvResLogo;
	private ImageView mIvTypeLogo;
	private TextView mTvTypeMessage;
	private ImageLoader mImageLoader;
	private MyNotificationRestaurantListEntity restaurantObject;
	private MyNotificationMessageListEntity messageObject;
	private String mRestaurantLogo;
	private RelativeLayout mGroupPoint;
	private int mFromId;
	private int mMessageId;
	private int mRestaurantId;
	private String mAlertType;
	private DialogConfirm dialogConfirm;
	private LinearLayout mLlMessError;
	private boolean mIsShowComment = false;

	private ProcessDialogConfirmRelply mProcessDialogMove;

	public DialogMynotificationReplyMessage(Context context,
			boolean isShowSave, boolean isShowCancel,
			ProcessDialogConfirmRelply processMove, String mLogo,
			MyNotificationMessageListEntity messageObject,
			boolean isShowTvComment, boolean mIsShowOnlyCheckBox) {
		super(context);
		mActivity = (Activity) context;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.dialog_mynotification_reply_message);
		mImageLoader = new ImageLoader(mActivity);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
		mProcessDialogMove = processMove;
		mBtSubmit = (Button) findViewById(R.id.dialog_mynitification_reply_message_bt_submit);
		mBtCancel = (Button) findViewById(R.id.dialog_mynitification_reply_message_bt_cancel);
		mTvComment = (EditText) findViewById(R.id.dialog_mynitification_reply_message_tv_message);
		mCbAccept = (CheckBox) findViewById(R.id.dialog_mynitification_reply_message_cb_accept);
		mTvPoint = (TextView) findViewById(R.id.dialog_mynitification_reply_message_tv_point);
		mIvResLogo = (ImageView) findViewById(R.id.dialog_mynitification_reply_message_iv_logo_restaurant);
		mIvTypeLogo = (ImageView) findViewById(R.id.dialog_mynitification_reply_message_iv_type_logo);
		mTvTypeMessage = (TextView) findViewById(R.id.dialog_mynitification_reply_message_tv_type_name);
		mGroupPoint = (RelativeLayout) findViewById(R.id.dialog_mynitification_reply_message_ll_grouppoint);
		mLlMessError = (LinearLayout) findViewById(R.id.dialog_mynotification_reply_mess_ll_error);
		mBtSubmit.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);
		mIsShowComment = isShowTvComment;
		mCbAccept.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					dialogConfirm = new DialogConfirm(
							mActivity,
							android.R.drawable.ic_dialog_alert,
							"Accept Points",
							"Are you sure you want to receive this number of points?",
							true, new ProcessDialogConfirm() {

								@Override
								public void click_Ok() {
									mCbAccept.isChecked();
									mCbAccept.setEnabled(false);
									dialogConfirm.dismiss();
								}

								@Override
								public void click_Cancel() {
									mCbAccept.setChecked(false);
									mCbAccept.setEnabled(true);
									dialogConfirm.dismiss();
								}
							});
					dialogConfirm.show();
				}
			}
		});

		try {
			if (Utils.isNetworkConnected(mActivity)) {
				new DownloadImageTask(mIvResLogo)
						.execute(ServerURL.URL + mLogo);
				new DownloadImageTask(mIvTypeLogo).execute(ServerURL.URL
						+ messageObject.mAlertLogo);
			} else {
				Toast.makeText(
						mActivity,
						mActivity.getResources().getString(
								R.string.mess_error_network), Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mTvTypeMessage.setText(messageObject.mMsgSubject);
		mTvPoint.setText(Utils.formatPointNumbers(messageObject.mPoint) + "");

		if (mIsShowOnlyCheckBox) {
			mGroupPoint.setVisibility(View.VISIBLE);
		} else {
			mGroupPoint.setVisibility(View.GONE);
		}

		if (!isShowTvComment) {
			mTvComment.setVisibility(View.GONE);
		} else {
			mTvComment.setVisibility(View.VISIBLE);
		}

		mFromId = Integer.parseInt(messageObject.mFromUser.trim());

		mMessageId = messageObject.mMsgId;
		mRestaurantId = messageObject.mRestaurantId;
		mAlertType = messageObject.mAlertType;

		mTvComment.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (mTvComment.getText().length() > 0) {
					mLlMessError.setVisibility(View.GONE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent event) {
	// View v = getCurrentFocus();
	// boolean ret = super.dispatchTouchEvent(event);
	//
	// if (v instanceof EditText) {
	// View w = getCurrentFocus();
	// int scrcoords[] = new int[2];
	// w.getLocationOnScreen(scrcoords);
	// float x = event.getRawX() + w.getLeft() - scrcoords[0];
	// float y = event.getRawY() + w.getTop() - scrcoords[1];
	//
	// if (event.getAction() == MotionEvent.ACTION_UP
	// && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
	// .getBottom())) {
	// InputMethodManager imm = (InputMethodManager) getContext()
	// .getSystemService(Context.INPUT_METHOD_SERVICE);
	// imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
	// .getWindowToken(), 0);
	// }
	// }
	// return ret;
	// }

	@Override
	public void onClick(View v) {

		if (v == mBtCancel) {
			mProcessDialogMove.click_Cancel();
			dismiss();
		} else if (v == mBtSubmit) {

			if (mCbAccept.isChecked() == true) {
				if (mIsShowComment) {
					// From Restaurant
					if (message().length() > 0) {
						mProcessDialogMove.click_Ok(restaurantObject,
								mRestaurantLogo, messageObject);
						dismiss();

					} else {
						mLlMessError.setVisibility(View.VISIBLE);
					}
				} else {
					// From Friend
					mProcessDialogMove.click_Ok(restaurantObject,
							mRestaurantLogo, messageObject);
					dismiss();
				}
			} else {
				// Uncheck Accept
				if (mIsShowComment) {
					// From Restaurant
					if (message().length() > 0) {
						mProcessDialogMove.click_Ok(restaurantObject,
								mRestaurantLogo, messageObject);
						dismiss();

					} else {
						mLlMessError.setVisibility(View.VISIBLE);
					}

				} else {
					// From Friend
					dialogConfirm = new DialogConfirm(
							mActivity,
							android.R.drawable.ic_dialog_alert,
							"Accept Points",
							mActivity
									.getString(R.string.mess_error_validate_accept_point),
							true, new ProcessDialogConfirm() {

								@Override
								public void click_Ok() {
									dialogConfirm.dismiss();
								}

								@Override
								public void click_Cancel() {
									dialogConfirm.dismiss();
								}
							});
					dialogConfirm.show();
				}

			}
			// if (!mCbAccept.isChecked() && mIsShowComment == false) {
			// dialogConfirm = new DialogConfirm(
			// mActivity,
			// android.R.drawable.ic_dialog_alert,
			// "Accept Points",
			// mActivity
			// .getString(R.string.mess_error_validate_accept_point),
			// true, new ProcessDialogConfirm() {
			//
			// @Override
			// public void click_Ok() {
			// dialogConfirm.dismiss();
			// }
			//
			// @Override
			// public void click_Cancel() {
			// dialogConfirm.dismiss();
			// }
			// });
			// dialogConfirm.show();
			//
			// } else if (mIsShowComment == true) {
			// if (message().length() > 0) {
			// mProcessDialogMove.click_Ok(restaurantObject,
			// mRestaurantLogo, messageObject);
			// dismiss();
			//
			// } else {
			// mLlMessError.setVisibility(View.VISIBLE);
			// }
			// } else if (mCbAccept.isChecked() && mIsShowComment == false) {
			// mProcessDialogMove.click_Ok(restaurantObject, mRestaurantLogo,
			// messageObject);
			// dismiss();
			// }
		}
	}

	/**
	 * @return value when user commented
	 */
	public String message() {
		return mTvComment.getText().toString().trim();
	}

	public int messageId() {
		return mMessageId;
	}

	public String alertType() {
		return mAlertType;
	}

	public int restaurantId() {
		return mRestaurantId;
	}

	public int fromUser() {
		return mFromId;
	}

	public double points() {
		return Double.parseDouble(mTvPoint.getText().toString());
	}

	/**
	 * @return value when user checked on checkbox
	 */
	public int accepted() {
		int accept;
		if (mCbAccept.isChecked()) {
			accept = 1;
		} else {
			accept = 0;
		}

		return accept;
	}

	public boolean isValidateAcceptBox() {
		return mCbAccept.isChecked();
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

	/**
	 * @author Thanh Vu
	 * 
	 */
	public static abstract class ProcessDialogConfirmRelply {
		public abstract void click_Ok(
				MyNotificationRestaurantListEntity restaurantObject,
				String mLogo, MyNotificationMessageListEntity messageObject);

		public abstract void click_Cancel();
	}

}
