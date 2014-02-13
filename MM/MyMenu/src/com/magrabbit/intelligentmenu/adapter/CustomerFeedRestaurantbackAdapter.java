package com.magrabbit.intelligentmenu.adapter;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating;
import com.magrabbit.intelligentmenu.customview.DialogConFirmRating.ProcessDialogConfirmRating;
import com.magrabbit.intelligentmenu.customview.DialogRating;
import com.magrabbit.intelligentmenu.customview.DialogRating.ProcessDialogConfirm_Rating;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.CommentUpdate;
import com.magrabbit.intelligentmenu.model.CommentsRestaurantEntity;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * @author Hung Hoang
 * 
 */
public class CustomerFeedRestaurantbackAdapter extends
		ArrayAdapter<CommentsRestaurantEntity> {

	private Activity mContext;
	private LayoutInflater mLiInflater;
	private ImageLoader mImageLoader;

	// For editing
	private boolean mIsEditMode = false;
	// For showing progress
	private ProgressDialog mProgressDialog;

	// For processing event after editing
	private ProcessLoadRatingList mProcess;
	private float mRating;

	public CustomerFeedRestaurantbackAdapter(Context context, int resource,
			List<CommentsRestaurantEntity> lstFeebbac,
			ProcessLoadRatingList process) {
		super(context, resource, lstFeebbac);
		this.mContext = (Activity) context;
		this.mProcess = process;
		mLiInflater = LayoutInflater.from(this.mContext);
		mImageLoader = new ImageLoader(context);
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.mess_waitting));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		// get the item from the list
		final CommentsRestaurantEntity cusFeedback = getItem(position);

		if (cusFeedback == null) {
			return null;
		}

		viewHolder = new ViewHolder();
		if (position % 2 == 0) {
			convertView = mLiInflater
					.inflate(R.layout.feedback_item_left, null);
			viewHolder.mTvRateScore = (TextView) convertView
					.findViewById(R.id.tv_rate_score_left);
			viewHolder.mTvComment = (TextView) convertView
					.findViewById(R.id.tv_comment_left);
			viewHolder.mTvDateTime = (TextView) convertView
					.findViewById(R.id.tv_datetime_left);
			viewHolder.mTvCustomerName = (TextView) convertView
					.findViewById(R.id.tv_name_customer_left);
			viewHolder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.logo_comment);
		} else {
			convertView = mLiInflater.inflate(R.layout.feedback_item_right,
					null);
			viewHolder.mTvRateScore = (TextView) convertView
					.findViewById(R.id.tv_rate_score_right);
			viewHolder.mTvComment = (TextView) convertView
					.findViewById(R.id.tv_comment_right);
			viewHolder.mTvDateTime = (TextView) convertView
					.findViewById(R.id.tv_datetime_right);
			viewHolder.mTvCustomerName = (TextView) convertView
					.findViewById(R.id.tv_name_customer_right);
			viewHolder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.logo_comment);
		}
		if (cusFeedback.mRating >= 13) {
			viewHolder.mTvRateScore.setText("F");
		} else {
			viewHolder.mTvRateScore.setText(Utils
					.exchangeRateGetExtra(cusFeedback.mRating));
		}
		if (cusFeedback.mRatingContent == null) {
			viewHolder.mTvComment.setText("");
		} else {
			viewHolder.mTvComment.setText(cusFeedback.mRatingContent.trim());
		}
		viewHolder.mTvCustomerName.setText(cusFeedback.mUserName);
		viewHolder.mTvCustomerName.setTextColor(Color.WHITE);

		if (cusFeedback.mDateUpdated != null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM,
					Locale.US);
			Date d = new Date(cusFeedback.mDateUpdated.substring(0, 12));
			String formattedDate = df.format(d);
			// Check whether this comment is edited or not
			if (!cusFeedback.mDateCreated.equals(cusFeedback.mDateUpdated)) {
				viewHolder.mTvDateTime.setText("edited on: " + formattedDate);
			} else {
				viewHolder.mTvDateTime.setText(formattedDate);
			}
		} 
		Typeface type = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/amrtypen.ttf");
		viewHolder.mTvComment.setTypeface(type);
		viewHolder.mTvRateScore.setTypeface(type);
		viewHolder.mTvCustomerName.setTypeface(type);
		viewHolder.mTvDateTime.setTypeface(type);
		mImageLoader.DisplayImage(ServerURL.URL + cusFeedback.mUserAvatar,
				viewHolder.mIvAvatar);

		// Process event of click on each rating item to edit,
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Need to check whether it is your rating or not
				if (cusFeedback.mUserName.equals(UserObject.getInstance(
						mContext).getUserName())) {
					mIsEditMode = true;
					DialogRating dialog = new DialogRating(
							mContext,
							mIsEditMode,
							new ProcessDialogConfirm_Rating() {

								@Override
								public void click_Submit(int valueRating,
										String comment) {
									// Update new rating and comment
									cusFeedback.mRating = valueRating;
									cusFeedback.mRatingContent = comment;
									// Open dialog for confirmation
									DialogConFirmRating dialogConfirm = new DialogConFirmRating(
											mContext,
											new ProcessDialogConfirmRating() {

												@Override
												public void click_PostIt() {
													try {
														if (Utils
																.isNetworkConnected(mContext)) {
															// Add code to run
															// service update
															// rating
															new UpdateRestaurantCommentTask()
																	.execute(cusFeedback);
														} else {
															showToastMessage(mContext
																	.getResources()
																	.getString(
																			R.string.mess_error_network));
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												}

												@Override
												public void click_Cancel() {
													// TODO Auto-generated
													// method stub

												}
											},
											Utils.exchangeRateGetExtra(valueRating),
											comment);

									dialogConfirm.show();

								}

								@Override
								public void click_Cancel() {
									// TODO Auto-generated method stub

								}
							},
							cusFeedback.mRating,
							cusFeedback.mRatingContent,
							mContext.getResources()
									.getString(
											R.string.custom_seebar_rate_tittle_restaurant));
					dialog.show();
				} else {
					showToastMessage(mContext.getResources().getString(
							R.string.details_restaurant_mess_wrong_comment));
				}

			}
		});

		return convertView;
	}

	private class ViewHolder {
		TextView mTvRateScore;
		TextView mTvComment;
		TextView mTvDateTime;
		TextView mTvCustomerName;
		ImageView mIvAvatar;
	}

	/**
	 * 
	 * @Description This task is to update restaurant comments of users after
	 *              editing
	 */
	public class UpdateRestaurantCommentTask extends
			AsyncTask<CommentsRestaurantEntity, Void, CommentUpdate> {

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}

		@Override
		protected CommentUpdate doInBackground(
				CommentsRestaurantEntity... params) {
			try {
				CommentsRestaurantEntity cusFeedBack = params[0];
				InputStream inputStream = Server
						.requestPost(
								ServerURL
										.getUrlUpdateRestaurantComment(cusFeedBack.mLocationId),
								ServerURL.getEntityUpdateRestaurantComment(
										UserObject.getInstance(mContext).mAccess_token,
										cusFeedBack.mRating,
										cusFeedBack.mRatingContent,
										cusFeedBack.mMessageId));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(inputStream)
						.toString();
				CommentUpdate response = gson.fromJson(json,
						CommentUpdate.class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(CommentUpdate result) {
			if (result == null) {
				mProgressDialog.dismiss();
				showToastMessage(mContext.getString(R.string.mess_error_server));
				return;
			}
			if ((result.mError != null && result.mError.length() > 0 && result.mError
					.equalsIgnoreCase(Constants.MESSAGE_INVALID_TOKEN))
					|| (result.mMessage != null && result.mMessage.length() > 0)
					&& result.mMessage
							.equalsIgnoreCase(Constants.MESSAGE_NO_ACCESSTOKEN)) {
				mProgressDialog.dismiss();
				showToastMessage(mContext
						.getString(R.string.mess_invalid_accesstoken));
				// Process when losing access_token by setResult Logout and
				// finish Activity
				mProcess.process_lostAccessToken();
				return;
			}
			if (!result.getStatus().equals("")) {
				showToastMessage(mContext
						.getString(R.string.mess_success_update_rating));
			}
			mRating = Float.parseFloat(result.getRating());
			// Put the rating number got from Server to the
			// RestaurantDetails to load the list of ratings again
			mProcess.loadRatingListAgain(mRating);
			mProgressDialog.dismiss();

		}

	}

	/**
	 * Show Toast message template
	 * 
	 * @param message
	 */
	void showToastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	public static abstract class ProcessLoadRatingList {
		public abstract void loadRatingListAgain(float ratingNumber);

		public abstract void process_lostAccessToken();
	}
}
