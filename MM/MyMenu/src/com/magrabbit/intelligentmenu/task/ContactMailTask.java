package com.magrabbit.intelligentmenu.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.magrabbit.intelligentmenu.listener.ContactMailListener;
import com.magrabbit.intelligentmenu.model.ContactObject;

public class ContactMailTask extends AsyncTask<Void, Void, Boolean> {

	private final WeakReference<Context> mContext;
	private final WeakReference<ContactMailListener> mListener;
	private List<ContactObject> mListContactEmail;

	public static void execute(Context context, ContactMailListener listener) {
		new ContactMailTask(context, listener).execute();
	}

	private ContactMailTask(Context context, ContactMailListener listener) {
		mContext = new WeakReference<Context>(context);
		mListener = new WeakReference<ContactMailListener>(listener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Context context = mContext.get();
		mListContactEmail = new ArrayList<ContactObject>();
		try {
			ContentResolver cr = context.getContentResolver();
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					ContactsContract.Contacts.DISPLAY_NAME + " ASC");
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ContactObject mibileItem = new ContactObject();
					String ContactID = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					Cursor cur1 = cr.query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID
									+ " = ?", new String[] { ContactID }, null);
					mibileItem.setIdContact(ContactID);
					// ======================================================================
					// get firstName & lastName
					Cursor nameCur = context.getContentResolver().query(
							ContactsContract.Data.CONTENT_URI,
							null,
							ContactsContract.Data.MIMETYPE + " = ? AND "
									+ StructuredName.CONTACT_ID + " = ?",
							new String[] { StructuredName.CONTENT_ITEM_TYPE,
									Long.valueOf(ContactID).toString() }, null);
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

							String middleName = nameCur
									.getString(nameCur
											.getColumnIndex(StructuredName.MIDDLE_NAME));
							if (middleName == null)
								middleName = "";

							String lastName = nameCur
									.getString(nameCur
											.getColumnIndex(StructuredName.FAMILY_NAME));
							if (lastName == null)
								lastName = "";

							lastName = middleName
									+ (middleName.length() > 0 ? " " : "")
									+ lastName;
							mibileItem.setFristName(firstName);
							mibileItem.setLastName(lastName);
							mibileItem.setFullName(displayName);
						}
						nameCur.close();
					}
					while (cur1.moveToNext()) {
						// to get the contact names
						String email = cur1
								.getString(cur1
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						if (email != null) {

							mibileItem.setEmail(email);
							mibileItem.setSelected(false);
							if (mListContactEmail != null && mibileItem != null) {
								mListContactEmail.add(mibileItem);
							}
						}
					}
					cur1.close();
				}
			}
			cursor.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		ContactMailListener listener = mListener.get();
		if (result) {
			listener.loadContactMailFinish(mListContactEmail);
		} else {
			listener.loadContactMailError();
		}
	}

}
