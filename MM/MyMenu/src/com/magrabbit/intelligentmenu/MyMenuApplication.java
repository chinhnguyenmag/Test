package com.magrabbit.intelligentmenu;

import android.app.Application;

import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.task.LoadContactPhoneTask;
import com.magrabbit.intelligentmenu.utils.Constants;
import com.testflightapp.lib.TestFlight;

public class MyMenuApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			// Initialize TestFlight with your app token.
			TestFlight.takeOff(this, Constants.TESTFLIGHT_TOKEN);
			// Load contact on phone
			if (ContactObject.mLoadStatus == ContactObject.LOAD_ERROR) {
				LoadContactPhoneTask.execute(getApplicationContext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
