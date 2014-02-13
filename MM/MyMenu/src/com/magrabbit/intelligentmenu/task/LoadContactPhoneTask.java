package com.magrabbit.intelligentmenu.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.utils.Constants;

public class LoadContactPhoneTask extends AsyncTask<Void, Void, Boolean> {

	private final WeakReference<Context> mContext;

	public static void execute(Context context) {
		new LoadContactPhoneTask(context).execute();
	}

	private LoadContactPhoneTask(Context context) {
		mContext = new WeakReference<Context>(context);
		ContactObject.mLoadStatus = ContactObject.LOADING;
		ContactObject.getListContact().clear();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Context context = mContext.get();
		try {
			ContactObject mibileItem = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[] { ContactsContract.Contacts._ID,
							ContactsContract.Contacts.DISPLAY_NAME }, null,
					null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

			// id of the Contact to return.
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					mibileItem = new ContactObject();
					String ContactID = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));

					String displayName = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					mibileItem.setIdContact(ContactID);
					mibileItem.setFullName(displayName);

					Cursor nameCur = context.getContentResolver().query(
							ContactsContract.Data.CONTENT_URI,
							null,
							ContactsContract.Data.MIMETYPE + " = ? AND "
									+ StructuredName.CONTACT_ID + " = ?",
							new String[] { StructuredName.CONTENT_ITEM_TYPE,
									Long.valueOf(ContactID).toString() }, null);

					if (nameCur != null) {
						if (nameCur.moveToFirst()) {

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
					Cursor phoneCursor = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "='" + ContactID + "'", null, null);

					while (phoneCursor.moveToNext()) {
						String number = phoneCursor
								.getString(phoneCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

						number = number.replace("-", "");
						number = number.replace("(", "");
						number = number.replace(")", "");
						number = number.replace("+", "");

						mibileItem.setPhoneNumber(number);
					}
					phoneCursor.close();

					Cursor cur1 = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID
									+ " = ?", new String[] { ContactID }, null);
					while (cur1.moveToNext()) {
						// to get the contact names
						String email = cur1
								.getString(cur1
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						if (email != null) {
							mibileItem.setEmail(email);
						}
					}
					cur1.close();

					mibileItem.setSelected(false);
					if (mibileItem != null) {
						ContactObject.getListContact().add(mibileItem);
					}
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
		if (result) {
			ContactObject.mLoadStatus = ContactObject.LOAD_FINISHED;
			Constants.displayMessage(mContext.get(), Constants.CONNECT_SUCCESS);
		} else {
			ContactObject.mLoadStatus = ContactObject.LOAD_ERROR;
			Constants.displayMessage(mContext.get(), Constants.CONNECT_ERROR);
		}
	}

}
