package com.magrabbit.intelligentmenu.loader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.model.ContactObject;

public class ContactLoader {

	private Map<TextView, ContactObject> mContacts = Collections
			.synchronizedMap(new WeakHashMap<TextView, ContactObject>());
	ExecutorService executorService;
	Context mContext;

	public ContactLoader(Context context) {
		executorService = Executors.newFixedThreadPool(5);
		mContext = context;
	}

	public void DisplayImage(ContactObject contactObject, TextView textView) {
		mContacts.put(textView, contactObject);
		queuePhoto(contactObject, textView);
	}

	private void queuePhoto(ContactObject contactObject, TextView textView) {
		PhotoToLoad p = new PhotoToLoad(contactObject, textView);
		executorService.submit(new PhotosLoader(p));
	}

	private String getBitmap(ContactObject contactObject) {
		String number = "";
		try {
			Cursor nameCur = mContext.getContentResolver().query(
					ContactsContract.Data.CONTENT_URI,
					null,
					ContactsContract.Data.MIMETYPE + " = ? AND "
							+ StructuredName.CONTACT_ID + " = ?",
					new String[] {
							StructuredName.CONTENT_ITEM_TYPE,
							Long.valueOf(contactObject.getIdContact())
									.toString() }, null);

			if (nameCur != null) {
				if (nameCur.moveToFirst()) {

					String displayName = nameCur
							.getString(nameCur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (displayName == null)
						displayName = "";

					String firstName = nameCur.getString(nameCur
							.getColumnIndex(StructuredName.GIVEN_NAME));
					if (firstName == null)
						firstName = "";

					String middleName = nameCur.getString(nameCur
							.getColumnIndex(StructuredName.MIDDLE_NAME));
					if (middleName == null)
						middleName = "";

					String lastName = nameCur.getString(nameCur
							.getColumnIndex(StructuredName.FAMILY_NAME));
					if (lastName == null)
						lastName = "";

					lastName = middleName
							+ (middleName.length() > 0 ? " " : "") + lastName;
					contactObject.setFristName(firstName);
					contactObject.setLastName(lastName);
					contactObject.setFullName(displayName);
				}
				nameCur.close();
			}

			Cursor phoneCursor = mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "='"
							+ contactObject.getIdContact() + "'", null, null);

			while (phoneCursor.moveToNext()) {
				number = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				number = number.replace("-", "");
				number = number.replace("(", "");
				number = number.replace(")", "");
				number = number.replace("+", "");
				contactObject.setPhoneNumber(number);
			}

			Cursor cur1 = mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
					new String[] { contactObject.getIdContact() }, null);
			while (cur1.moveToNext()) {
				// to get the contact names
				String email = cur1
						.getString(cur1
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				if (email != null) {
					contactObject.setEmail(email);
				}
			}
			cur1.close();
			phoneCursor.close();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return number;
	}

	// Task for the queue
	private class PhotoToLoad {
		public ContactObject mContactObject;
		public TextView imageView;

		public PhotoToLoad(ContactObject contactObject, TextView i) {
			mContactObject = contactObject;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			// if (imageViewReused(photoToLoad))
			// return;
			String number = getBitmap(photoToLoad.mContactObject);
			// if (imageViewReused(photoToLoad))
			// return;
			BitmapDisplayer bd = new BitmapDisplayer(number, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	// boolean imageViewReused(PhotoToLoad photoToLoad) {
	// String tag = mContacts.get(photoToLoad.imageView);
	// if (tag == null
	// || !tag.equals(photoToLoad.mContactObject.getIdContact()))
	// return true;
	// return false;
	// }

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		String number;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(String n, PhotoToLoad p) {
			number = n;
			photoToLoad = p;
		}

		public void run() {
			// if (imageViewReused(photoToLoad))
			// return;
			if (number != null)
				photoToLoad.imageView.setText(number);
			else {
				photoToLoad.imageView.setText("");
			}
		}
	}

}
