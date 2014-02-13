package com.magrabbit.intelligentmenu.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.magrabbit.intelligentmenu.listener.LogoutListener;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
import com.magrabbit.intelligentmenu.model.GoogleAccount;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.social.twitter.TwitterSession;

public class LogoutSocialTask extends AsyncTask<Void, Void, Boolean> {

	private final WeakReference<Context> mContext;
	private final WeakReference<LogoutListener> mListener;
	private final WeakReference<OnAccessRevokedListener> mRevokedListener;
	private AppPreferences mAppPreferences;

	public static void execute(Context context, LogoutListener listener,
			OnAccessRevokedListener gRevokedListener) {
		new LogoutSocialTask(context, listener, gRevokedListener).execute();
	}

	private LogoutSocialTask(Context context, LogoutListener listener,
			OnAccessRevokedListener gAccessRevoked) {
		mContext = new WeakReference<Context>(context);
		mListener = new WeakReference<LogoutListener>(listener);
		mRevokedListener = new WeakReference<OnAccessRevokedListener>(
				gAccessRevoked);
		mAppPreferences = new AppPreferences(context);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Context context = mContext.get();
		boolean result = true;
		try {

			if (context != null) {
				mAppPreferences.resetdata();

				try {

					if (TwittSharing.getInstance().getTwitter() != null) {
						TwittSharing.getInstance().getTwitter()
								.resetAccessToken();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					FaceBookAccount.getInstance(context).clearFacebookData(
							context);
					if (FaceBookAccount.getInstance(context).getFacebook() != null) {
						FaceBookAccount.getInstance(context).getFacebook()
								.logout(context);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					CookieSyncManager.createInstance(context);
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.removeSessionCookie();

				} catch (Exception e) {
					e.printStackTrace();
				}

				TwitterSession twitter = new TwitterSession(context);
				twitter.clearTwitterData(context);

				// InstagramSession instagram = new InstagramSession(context);
				// instagram.clearInstagramData(context);
				UserObject.getInstance(context).resetUser(context);
				try {
					if (GoogleAccount.getInstance(mContext.get())
							.getPlusClient() != null) {
						GoogleAccount.getInstance(mContext.get())
								.getPlusClient().clearDefaultAccount();
						GoogleAccount
								.getInstance(mContext.get())
								.getPlusClient()
								.revokeAccessAndDisconnect(
										mRevokedListener.get());
						GoogleAccount.getInstance(mContext.get())
								.setPlusClient(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		LogoutListener listener = mListener.get();
		if (result) {
			listener.LogoutFinish();
		} else {
			listener.LogoutError();
		}
	}

}
