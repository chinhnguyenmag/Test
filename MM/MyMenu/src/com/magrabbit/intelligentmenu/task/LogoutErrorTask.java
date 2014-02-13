package com.magrabbit.intelligentmenu.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.magrabbit.intelligentmenu.model.AppPreferences;
import com.magrabbit.intelligentmenu.model.FaceBookAccount;
import com.magrabbit.intelligentmenu.model.GoogleAccount;
import com.magrabbit.intelligentmenu.model.UserObject;
import com.magrabbit.intelligentmenu.social.twitter.TwittSharing;
import com.magrabbit.intelligentmenu.social.twitter.TwitterSession;

public class LogoutErrorTask extends AsyncTask<Void, Void, Boolean> implements
		OnAccessRevokedListener {

	private final WeakReference<Context> mContext;
	private final WeakReference<OnAccessRevokedListener> mRevokedListener;
	private AppPreferences mAppPreferences;

	public static void execute(Context context) {
		new LogoutErrorTask(context).execute();
	}

	private LogoutErrorTask(Context context) {
		mContext = new WeakReference<Context>(context);
		mRevokedListener = new WeakReference<OnAccessRevokedListener>(this);
		mAppPreferences = new AppPreferences(context);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Context context = mContext.get();
		boolean result = true;
		try {

			if (context != null) {
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

				GoogleAccount google = new GoogleAccount(context);
				google.clearGoogleData();

				mAppPreferences.resetdata();
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
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {

	}

}
