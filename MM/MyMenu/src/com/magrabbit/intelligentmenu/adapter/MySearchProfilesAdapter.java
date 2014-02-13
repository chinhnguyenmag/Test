package com.magrabbit.intelligentmenu.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.customview.DialogConfirm;
import com.magrabbit.intelligentmenu.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.MessageErrorEntity;
import com.magrabbit.intelligentmenu.model.SearchProfile;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.server.Server;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.magrabbit.intelligentmenu.utils.Utils;

public class MySearchProfilesAdapter extends ArrayAdapter<SearchProfile> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;

	// Edit Mode for profiles
	private boolean mIsEditMode;
	private int mEditedProfilePostion;
	// Set default search profile id
	int search_profile_id;

	private List<SearchProfile> mListSearchProfiles;
	private ProgressDialog mProgressDialog;
	private ProcessDialogConfirm_MySearchAdapter mProcessmove;
	AppPreferences mAppPreferences;

	public MySearchProfilesAdapter(Context mContext, int resource,
			List<SearchProfile> mListProfiles,
			ProcessDialogConfirm_MySearchAdapter mProcessmove) {
		super(mContext, resource, mListProfiles);
		this.mContext = mContext;
		mAppPreferences = new AppPreferences(mContext);
		this.mProcessmove = mProcessmove;
		mLayoutInflater = LayoutInflater.from(mContext);
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.mess_waitting));

		if (this.mListSearchProfiles == null)
			this.mListSearchProfiles = new ArrayList<SearchProfile>();
		this.mListSearchProfiles.clear();
		if (mListProfiles != null) {
			for (SearchProfile s : mListProfiles) {
				this.mListSearchProfiles.add(s);
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		SearchProfile itemProfile = getItem(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.mysearch_profile_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mIbtEdit = (Button) convertView
					.findViewById(R.id.mysearch_profile_item_imbt_edit);
			viewHolder.mTvProfileName = (TextView) convertView
					.findViewById(R.id.mysearch_profile_item_tv_profileName);
			viewHolder.mIbtDelete = (Button) convertView
					.findViewById(R.id.mysearch_profile_item_imbt_delete);
			viewHolder.mRbtDefault = (RadioButton) convertView
					.findViewById(R.id.mysearch_profile_item_rbt_default);
			viewHolder.mLayoutEdit = (LinearLayout) convertView
					.findViewById(R.id.mysearch_profile_item_layout1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (itemProfile != null) {
			try {
				viewHolder.mTvProfileName.setText(itemProfile.getNameProfile());

				// Call service to Set Default profile and load list view again
				viewHolder.mRbtDefault
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									if (Utils.isNetworkConnected(mContext)) {
										new SetDefaultProfileTask()
												.execute(new Integer[] {
														position,
														mListSearchProfiles
																.get(position)
																.getId() });
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
						});
				// Check RadioButton whether it is chosen as default profile or
				// not
				if (mListSearchProfiles.get(position).getIsDefault() == 1) {
					viewHolder.mRbtDefault.setChecked(true);
				} else {
					viewHolder.mRbtDefault.setChecked(false);
				}

				// Call service to Delete selected profile
				viewHolder.mIbtDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							if (Utils.isNetworkConnected(mContext)) {
								String content;
								if (mListSearchProfiles.get(position)
										.getIsDefault() == 1) {
									content = mContext
											.getString(R.string.dialog_advanced_search_delete_default_confirm);
								} else {
									content = mContext
											.getString(R.string.dialog_advanced_search_delete_confirm);
								}
								DialogConfirm dialog = new DialogConfirm(
										mContext,
										android.R.drawable.ic_dialog_alert,
										"Delete", content, true,
										new ProcessDialogConfirm() {

											@Override
											public void click_Ok() {
												if (Utils
														.isNetworkConnected(mContext)) {
													// Call service to delete
													// profile
													new deleteProfileTask()
															.execute(new Integer[] {
																	position,
																	getItem(
																			position)
																			.getId() });
												} else {
													showToastMessage(mContext
															.getResources()
															.getString(
																	R.string.mess_error_network));
												}
											}

											@Override
											public void click_Cancel() {

											}
										});
								dialog.show();

							} else {
								showToastMessage(mContext.getResources()
										.getString(R.string.mess_error_network));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
				// Call service to Edit profile
				// viewHolder.mLayoutEdit
				// .setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// if (Utils.isNetworkConnected(mContext)) {
				// // Set variables for editing mode
				// mIsEditMode = true;
				// mEditedProfilePostion = position;
				// // Open Dialog for users to change settings
				// DialogCreateNewProfile dialog = new DialogCreateNewProfile(
				// mContext,
				// mIsEditMode,
				// mEditedProfilePostion,
				// getItem(position).getIsDefault(),
				// mListSearchProfiles,
				// new ProcessDialogConfirm_CreateNewProfile() {
				//
				// @Override
				// public void click_Search(
				// SearchProfile searchprofile) {
				// mProcessmove
				// .click_Search(searchprofile);
				// }
				//
				// @Override
				// public void click_Save(
				// List<SearchProfile> listSearchProfiles) {
				// if (Utils
				// .isNetworkConnected(mContext)) {
				// // Update Adapter to
				// // refresh
				// // list view
				// mListSearchProfiles
				// .clear();
				// mListSearchProfiles
				// .addAll(listSearchProfiles);
				// mProcessmove
				// .click_UpdateListView(
				// mListSearchProfiles,
				// mIndexOfDefault);
				// } else {
				// showToastMessage(mContext
				// .getResources()
				// .getString(
				// R.string.mess_error_network));
				// }
				// }
				//
				// @Override
				// public void click_ChangeButtonBackground() {
				// // TODO Auto-generated
				// // method stub
				//
				// }
				// });
				// dialog.show();
				// } else {
				// showToastMessage(mContext
				// .getResources()
				// .getString(
				// R.string.mess_error_network));
				// }
				// }
				// });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	private class ViewHolder {
		Button mIbtEdit;
		TextView mTvProfileName;
		RadioButton mRbtDefault;
		Button mIbtDelete;
		LinearLayout mLayoutEdit;

	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Delete selected profile
	 */
	public class deleteProfileTask extends
			AsyncTask<Integer, Void, MessageErrorEntity> {
		int position, id;

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}

		@Override
		protected MessageErrorEntity doInBackground(Integer... params) {
			try {
				position = params[0];
				id = params[1];
				InputStream source = Server.requestPost(
						ServerURL.getUrlDeleteSearchProfile(),
						ServerURL.getEntityDeleteMySearch(mContext, id));

				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				MessageErrorEntity response = gson.fromJson(json,
						MessageErrorEntity.class);

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(MessageErrorEntity result) {
			try {
				if (result == null) {
					mProgressDialog.dismiss();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
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
					mProcessmove.process_lostAccessToken();
					return;
				}
				if (result.mStatus != null
						&& result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(mContext
							.getString(R.string.common_mess_delete_error));
				} else {
					showToastMessage(mContext
							.getString(R.string.common_mess_delete_success));

					int isDefault = mListSearchProfiles.get(position)
							.getIsDefault();
					mListSearchProfiles.remove(position);

					// Check whether users want to delete default search profile
					// or
					// not
					if (isDefault == 1) {
						if (mListSearchProfiles.size() != 0) {
							// Call service to set default to the first profile
							// of
							// the list
							new SetDefaultProfileTask().execute(new Integer[] {
									0, mListSearchProfiles.get(0).getId() });
						} else {
							// Set default profile = 0 locally
							UserObject.getInstance(mContext).mDefaultProfileId = 0;
							mAppPreferences.setDefaultsearchprofile(0);
						}

					}
					// Update Adapter to refresh list view
					mProcessmove.click_UpdateListView(mListSearchProfiles,
							position);
				}
				mProgressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				mProgressDialog.dismiss();
			}
		}
	}

	/**
	 * 
	 * @author baonguyen
	 * @Description: Get all search profiles when going to MySearch screen
	 */
	public class getAllSearchProfilesTask extends
			AsyncTask<Void, Void, SearchProfile[]> {
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}

		@Override
		protected SearchProfile[] doInBackground(Void... params) {
			try {
				InputStream source = Server.requestGet(ServerURL
						.getUrlGetAllSearchProfile(mContext));
				Gson gson = new Gson();

				String json = Server.inputStreamToString(source).toString();
				SearchProfile[] response = gson.fromJson(json,
						SearchProfile[].class);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchProfile[] result) {
			try {
				if (result == null) {
					mProgressDialog.dismiss();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
					if (Server.mStatus != 200) {
						mProgressDialog.dismiss();
						showToastMessage(mContext.getResources().getString(
								R.string.mess_invalid_accesstoken));
						// Process when losing access_token by setResult Logout
						// and
						// finish Activity
						mProcessmove.process_lostAccessToken();
					}
					return;
				} else if (result.length == 0) {
					showToastMessage(mContext.getResources().getString(
							R.string.search_profile_no_result));
				}
				mListSearchProfiles.clear();
				for (int i = 0; i < result.length; i++) {
					mListSearchProfiles.add(result[i]);
				}
				notifyDataSetChanged();
				mProgressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				mProgressDialog.dismiss();
			}

		}
	}

	public class SetDefaultProfileTask extends
			AsyncTask<Integer, Void, MessageErrorEntity> {

		int list_position;

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}

		@Override
		protected MessageErrorEntity doInBackground(Integer... params) {
			list_position = params[0];
			search_profile_id = params[1];
			try {
				InputStream inputStream = Server.requestPost(ServerURL
						.getUrlSetDefaultSearchProfile(), ServerURL
						.getEntitySetDefaultMySearch(mContext,
								search_profile_id));
				Gson gson = new Gson();
				String json = Server.inputStreamToString(inputStream)
						.toString();
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
			try {
				if (result == null) {
					mProgressDialog.dismiss();
					if (Utils.isNetworkConnected(mContext)) {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_server));
					} else {
						showToastMessage(mContext.getResources().getString(
								R.string.mess_error_network));
					}
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
					mProcessmove.process_lostAccessToken();
					return;
				}
				if (result.mStatus != null
						&& result.mStatus
								.equalsIgnoreCase(Constants.CONNECT_ERROR)) {
					showToastMessage(mContext
							.getString(R.string.common_mess_setdefault_error));
				} else {
					showToastMessage(mContext
							.getString(R.string.common_mess_setdefault_success));

					// Update default profile id into UserObject
					UserObject.getInstance(mContext).mDefaultProfileId = search_profile_id;
					// Load list of search profiles again
					loadListSearchProfilesAgain(search_profile_id);
					// Update data locally
					mProcessmove.click_UpdateListView(mListSearchProfiles,
							list_position);
				}
				mProgressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				mProgressDialog.dismiss();
			}

		}

	}

	/**
	 * @param search_default_id
	 * @Description Load list of search profiles again to notify list view
	 *              correctly
	 */
	public void loadListSearchProfilesAgain(int search_default_id) {
		for (SearchProfile s : mListSearchProfiles) {
			if (s.getId() == search_default_id) {
				s.setIsDefault(1);
			} else {
				s.setIsDefault(0);
			}
		}
	}

	/**
	 * Show Toast message template
	 * 
	 * @param message
	 */
	public void showToastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	public static abstract class ProcessDialogConfirm_MySearchAdapter {

		public abstract void click_Search(SearchProfile searchprofile);

		public abstract void click_UpdateListView(
				List<SearchProfile> listSearchProfiles,
				int indexOfDefaultProfile);

		public abstract void process_lostAccessToken();
	}

}
