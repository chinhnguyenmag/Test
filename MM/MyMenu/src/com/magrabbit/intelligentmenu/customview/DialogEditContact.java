package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.listener.AddContactListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author Hung Hoang.
 * 
 */
public class DialogEditContact extends Dialog implements OnClickListener {
	private Activity mActivity;
	private Button mBtSave;
	private Button mBtCancel;
	private EditText mEtFirstName;
	private EditText mEtLastName;
	private EditText mEtPhoneNumber;
	private EditText mEtEmailAddress;
	private AddContactListener mListener;
	private ContactObject co;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogEditContact(Context context, AddContactListener listener,
			ContactObject contactObject) {
		super(context);
		mActivity = (Activity) context;
		mListener = listener;
		// this.mIdContact = idContact;
		co = new ContactObject();
		co = contactObject;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_add_contact);
		mEtFirstName = (EditText) findViewById(R.id.dialog_add_contact_et_firstname);
		mEtLastName = (EditText) findViewById(R.id.dialog_add_contact_et_lastname);
		mEtPhoneNumber = (EditText) findViewById(R.id.dialog_add_contact_et_phone);
		mEtEmailAddress = (EditText) findViewById(R.id.dialog_add_contact_et_email);
		mBtSave = (Button) findViewById(R.id.dialog_add_contact_bt_save_contact);
		mBtCancel = (Button) findViewById(R.id.dialog_add_contact_bt_cancel);
		// co = getContact(idContact);
		mEtFirstName.setText(co.getFristName());
		mEtLastName.setText(co.getLastName());
		mEtPhoneNumber.setText(co.getPhoneNumber());
		mEtEmailAddress.setText(co.getEmail());
		mBtSave.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);
		mEtFirstName.requestFocus();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_add_contact_bt_save_contact:
			if (validate()) {
				updateContact(mEtFirstName.getText().toString(), mEtLastName
						.getText().toString(), mEtPhoneNumber.getText()
						.toString(), mEtEmailAddress.getText().toString(),
						co.getIdContact());
				co.setEmail(mEtEmailAddress.getText().toString());
				co.setFristName(mEtFirstName.getText().toString());
				co.setFullName(mEtFirstName.getText().toString() + " "
						+ mEtLastName.getText().toString());
				co.setPhoneNumber(mEtPhoneNumber.getText().toString());
				mListener.upDateContact(true, co);
				dismiss();
			}
			break;
		case R.id.dialog_add_contact_bt_cancel:
			dismiss();
			break;
		default:
			break;
		}

	}

	public boolean updateContact(String firstname, String lastname,
			String number, String email, String ContactId) {
		boolean success = true;
		try {
			firstname = firstname.trim();
			lastname = lastname.trim();
			email = email.trim();
			number = number.trim();
			ContentResolver contentResolver = mActivity.getContentResolver();

			String where = ContactsContract.Data.CONTACT_ID + " = ? AND "
					+ ContactsContract.Data.MIMETYPE + " = ?";

			String[] emailParams = new String[] { ContactId,
					ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE };
			String[] nameParams = new String[] {
					ContactId,
					ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
			String[] numberParams = new String[] { ContactId,
					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE };
			ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

			if (co.getEmail().equals("") || co.getPhoneNumber().equals("")) {
				String[] args = new String[] { ContactId };
				ops.add(ContentProviderOperation
						.newDelete(RawContacts.CONTENT_URI)
						.withSelection(RawContacts.CONTACT_ID + "=?", args)
						.build());
				contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
				addContact(firstname, lastname, number, email);
			} else {
				if (!email.equals("")) {
					ops.add(android.content.ContentProviderOperation
							.newUpdate(
									android.provider.ContactsContract.Data.CONTENT_URI)
							.withSelection(where, emailParams)
							.withValue(Email.DATA, email).build());
				}

				if (!firstname.equals("")) {
					ops.add(android.content.ContentProviderOperation
							.newUpdate(
									android.provider.ContactsContract.Data.CONTENT_URI)
							.withSelection(where, nameParams)
							.withValue(StructuredName.GIVEN_NAME, firstname)
							.build());
				}
				if (!lastname.equals("")) {
					ops.add(android.content.ContentProviderOperation
							.newUpdate(
									android.provider.ContactsContract.Data.CONTENT_URI)
							.withSelection(where, nameParams)
							.withValue(StructuredName.FAMILY_NAME, lastname)
							.build());
				}
				if (!lastname.equals("") && !firstname.equals("")) {
					ops.add(android.content.ContentProviderOperation
							.newUpdate(
									android.provider.ContactsContract.Data.CONTENT_URI)
							.withSelection(where, nameParams)
							.withValue(StructuredName.DISPLAY_NAME,
									firstname + " " + lastname).build());
				}
				if (!number.equals("")) {
					ops.add(android.content.ContentProviderOperation
							.newUpdate(
									android.provider.ContactsContract.Data.CONTENT_URI)
							.withSelection(where, numberParams)
							.withValue(
									ContactsContract.CommonDataKinds.Phone.TYPE,
									ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
							.withValue(Phone.NUMBER, number).build());
				}
				contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	public boolean validate() {
		if (mEtFirstName.getText().toString().equals("")) {
			Toast.makeText(mActivity, R.string.message_error_first_name,
					Toast.LENGTH_SHORT).show();
			mEtFirstName.requestFocus();
			return false;
		} else if (mEtLastName.getText().toString().equals("")) {
			Toast.makeText(mActivity, R.string.message_error_last_name,
					Toast.LENGTH_SHORT).show();
			mEtLastName.requestFocus();
			return false;
		} else if (mEtPhoneNumber.getText().toString().equals("")) {
			Toast.makeText(mActivity, "Phone Number is required.",
					Toast.LENGTH_SHORT).show();
			mEtPhoneNumber.requestFocus();
			return false;
		} else if (mEtPhoneNumber.getText().toString().length() < 10) {
			Toast.makeText(mActivity,
					"Please enter a valid phone number with 10 digit or more.",
					Toast.LENGTH_SHORT).show();
			mEtPhoneNumber.requestFocus();
			return false;

		} else if (phoneNumberExists(mActivity, mEtPhoneNumber.getText()
				.toString())) {
			Toast.makeText(mActivity, "Phone Number is already existing",
					Toast.LENGTH_SHORT).show();
			mEtPhoneNumber.requestFocus();
			return false;
		} else if (mEtEmailAddress.getText().toString().equals("")) {
			Toast.makeText(mActivity, "Email Address is required.",
					Toast.LENGTH_SHORT).show();
			mEtEmailAddress.requestFocus();
			return false;
		} else if (!Utils.isValidEmail(mEtEmailAddress.getText().toString())) {
			Toast.makeText(mActivity, R.string.message_error_email,
					Toast.LENGTH_SHORT).show();
			mEtEmailAddress.requestFocus();
			return false;
		} else if (emailExists(mActivity, mEtEmailAddress.getText().toString())) {
			Toast.makeText(mActivity, "Email Address is already existing",
					Toast.LENGTH_SHORT).show();
			mEtEmailAddress.requestFocus();
			return false;
		}
		return true;
	}

	public boolean phoneNumberExists(Activity _activity, String number) {
		if (number != null) {
			if (co.getPhoneNumber() != null
					&& co.getPhoneNumber().equals(number)) {
				return false;
			} else {
				Uri lookupUri = Uri.withAppendedPath(
						PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
				String[] mPhoneNumberProjection = { PhoneLookup._ID,
						PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
				Cursor cur = _activity.getContentResolver().query(lookupUri,
						mPhoneNumberProjection, null, null, null);
				try {
					if (cur.moveToFirst()) {
						return true;
					}
				} finally {
					if (cur != null)
						cur.close();
				}
			}
			return false;
		} else {
			return false;
		}
	}// contactExists

	public boolean emailExists(Activity _activity, String email) {
		if (email != null) {
			if (co.getEmail() != null && co.getEmail().equals(email)) {
				return false;
			} else {
				Uri uri = Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI,
						Uri.encode(email));
				Cursor cur = mActivity.getContentResolver().query(
						uri,
						new String[] { Email.CONTACT_ID, Email.DISPLAY_NAME,
								Email.DATA }, null, null, null);
				try {
					if (cur.moveToFirst()) {
						return true;
					}
				} finally {
					if (cur != null)
						cur.close();
				}
			}
			return false;
		} else {
			return false;
		}
	}// contactExists

	// public ContactObject getContact(String idContact) {
	// ContactObject object = new ContactObject();
	// object.setIdContact(idContact);
	// object.setSelected(false);
	// object.setEditmode(true);
	// // get firstName & lastName
	// Cursor nameCur = mActivity.getContentResolver().query(
	// ContactsContract.Data.CONTENT_URI,
	// null,
	// ContactsContract.Data.MIMETYPE + " = ? AND "
	// + StructuredName.CONTACT_ID + " = ?",
	// new String[] { StructuredName.CONTENT_ITEM_TYPE,
	// Long.valueOf(idContact).toString() }, null);
	// // get phone number
	// Cursor phoneCursor = mActivity.getContentResolver().query(
	// ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	// null,
	// ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "='"
	// + idContact + "'", null, null);
	// // get email
	// Cursor emailCursor = mActivity.getContentResolver().query(
	// ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
	// ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
	// new String[] { idContact }, null);
	//
	// try {
	// if (nameCur != null) {
	// if (nameCur.moveToFirst()) {
	//
	// String displayName = nameCur
	// .getString(nameCur
	// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	// if (displayName == null)
	// displayName = "";
	//
	// String firstName = nameCur.getString(nameCur
	// .getColumnIndex(StructuredName.GIVEN_NAME));
	// if (firstName == null)
	// firstName = "";
	//
	// String middleName = nameCur.getString(nameCur
	// .getColumnIndex(StructuredName.MIDDLE_NAME));
	// if (middleName == null)
	// middleName = "";
	//
	// String lastName = nameCur.getString(nameCur
	// .getColumnIndex(StructuredName.FAMILY_NAME));
	// if (lastName == null)
	// lastName = "";
	//
	// lastName = middleName
	// + (middleName.length() > 0 ? " " : "") + lastName;
	// object.setFullName(displayName);
	// object.setFristName(firstName);
	// object.setLastName(lastName);
	// }
	// nameCur.close();
	// }
	// if (phoneCursor != null) {
	// if (phoneCursor.moveToFirst()) {
	// String number = phoneCursor
	// .getString(phoneCursor
	// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	//
	// number = number.replace("-", "");
	// number = number.replace("(", "");
	// number = number.replace(")", "");
	//
	// if (number.startsWith("+84")) {
	// number = "0" + number.substring(3);
	// }
	// if (number.startsWith("-84")) {
	// number = "0" + number.substring(3);
	// }
	// if (number.startsWith("84")) {
	// number = "0" + number.substring(2);
	// }
	// object.setPhoneNumber(number);
	// }
	// phoneCursor.close();
	// }
	// if (emailCursor != null) {
	// if (emailCursor.moveToFirst()) {
	// // to get the contact names
	// String email = emailCursor
	// .getString(emailCursor
	// .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	// if (email == null)
	// email = "";
	//
	// object.setEmail(email);
	// }
	// emailCursor.close();
	// }
	// } finally {
	// if (phoneCursor != null) {
	// phoneCursor.close();
	// }
	// if (emailCursor != null) {
	// emailCursor.close();
	// }
	// if (nameCur != null) {
	// nameCur.close();
	// }
	// }
	// return object;
	// }

	public void addContact(String firstName, String lastName,
			String mobileNumber, String emailAddress) {

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------ Names
		// INSERT NAME
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						firstName + " " + lastName) // Name of the person
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
						lastName) // Name of the person
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
						firstName) // Name of the person
				.build());

		// ------------------------------------------------------ Mobile Number
		if (mobileNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							mobileNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build());
		}

		// ------------------------------------------------------ Email
		if (emailAddress != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							emailAddress)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.build());
		}

		// Asking the Contact provider to create a new contact
		try {
			mActivity.getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
