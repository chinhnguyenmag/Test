package com.magrabbit.intelligentmenu.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.magrabbit.intelligentmenu.listener.ContactPhoneListener;
import com.magrabbit.intelligentmenu.model.ContactObject;

public class ContactPhoneTask extends AsyncTask<Void, Void, Boolean> {

	private final WeakReference<Context> mContext;
	private final WeakReference<ContactPhoneListener> mListener;
	private List<ContactObject> mListContactPhone;

	public static void execute(Context context, ContactPhoneListener listener) {
		new ContactPhoneTask(context, listener).execute();
	}

	private ContactPhoneTask(Context context, ContactPhoneListener listener) {
		mContext = new WeakReference<Context>(context);
		mListener = new WeakReference<ContactPhoneListener>(listener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Context context = mContext.get();
		mListContactPhone = new ArrayList<ContactObject>();
		try {
			ContactObject mibileItem = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[] { ContactsContract.Contacts._ID,
							ContactsContract.Contacts.DISPLAY_NAME,
							ContactsContract.Contacts.HAS_PHONE_NUMBER }, null,
					null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

			// id of the Contact to return.
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					mibileItem = new ContactObject();
					String ContactID = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					String hasPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					if (Integer.parseInt(hasPhone) == 1) {

						String displayName = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						mibileItem.setIdContact(ContactID);
						mibileItem.setFullName(displayName);
						mibileItem.setSelected(false);
						if (mListContactPhone != null && mibileItem != null) {
							mListContactPhone.add(mibileItem);
						}

						// ======================================================================
						// get firstName & lastName
						// Cursor nameCur = context.getContentResolver().query(
						// ContactsContract.Data.CONTENT_URI,
						// null,
						// ContactsContract.Data.MIMETYPE + " = ? AND "
						// + StructuredName.CONTACT_ID + " = ?",
						// new String[] {
						// StructuredName.CONTENT_ITEM_TYPE,
						// Long.valueOf(ContactID).toString() },
						// null);
						// mibileItem.setIdContact(ContactID);
						// if (nameCur != null) {
						// if (nameCur.moveToFirst()) {
						//
						// String displayName = nameCur
						// .getString(nameCur
						// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						// if (displayName == null)
						// displayName = "";
						//
						// String firstName = nameCur
						// .getString(nameCur
						// .getColumnIndex(StructuredName.GIVEN_NAME));
						// if (firstName == null)
						// firstName = "";
						//
						// String middleName = nameCur
						// .getString(nameCur
						// .getColumnIndex(StructuredName.MIDDLE_NAME));
						// if (middleName == null)
						// middleName = "";
						//
						// String lastName = nameCur
						// .getString(nameCur
						// .getColumnIndex(StructuredName.FAMILY_NAME));
						// if (lastName == null)
						// lastName = "";
						//
						// lastName = middleName
						// + (middleName.length() > 0 ? " " : "")
						// + lastName;
						// mibileItem.setFristName(firstName);
						// mibileItem.setLastName(lastName);
						// mibileItem.setFullName(displayName);
						// }
						// nameCur.close();
						// }
						// if (Integer.parseInt(hasPhone) == 1) {
						// Cursor phoneCursor = context
						// .getContentResolver()
						// .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						// null,
						// ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						// + "='" + ContactID + "'",
						// null, null);
						//
						// while (phoneCursor.moveToNext()) {
						// String number = phoneCursor
						// .getString(phoneCursor
						// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						//
						// number = number.replace("-", "");
						// number = number.replace("(", "");
						// number = number.replace(")", "");
						// number = number.replace("+", "");
						//
						// mibileItem.setPhoneNumber(number);
						// mibileItem.setSelected(false);
						// if (mListContactPhone != null
						// && mibileItem != null) {
						// mListContactPhone.add(mibileItem);
						// }
						// }
						// phoneCursor.close();
						// }
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

		ContactPhoneListener listener = mListener.get();
		if (result) {
			listener.loadContactFinish(mListContactPhone);
		} else {
			listener.loadContactError();
		}
	}

}
