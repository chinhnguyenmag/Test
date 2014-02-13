package com.magrabbit.intelligentmenu.customview;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.PhoneLookup;
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
public class DialogAddContact extends Dialog implements OnClickListener {
	private Activity mActivity;
	private Button mBtSave;
	private Button mBtCancel;
	private EditText mEtFirstName;
	private EditText mEtLastName;
	private EditText mEtPhoneNumber;
	private EditText mEtEmailAddress;
	private AddContactListener mListener;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogAddContact(Context context, AddContactListener listener) {
		super(context);
		mActivity = (Activity) context;
		mListener = listener;
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

		mBtSave.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_add_contact_bt_save_contact:
			if (validate()) {
				addContact(mEtFirstName.getText().toString(), mEtLastName
						.getText().toString(), mEtPhoneNumber.getText()
						.toString(), mEtEmailAddress.getText().toString());
				ContactObject co = new ContactObject();
				co.setEmail(mEtEmailAddress.getText().toString());
				co.setPhoneNumber(mEtPhoneNumber.getText().toString());
				co.setFullName(mEtFirstName.getText().toString() + " "
						+ mEtLastName.getText().toString());
				mListener.upDateContact(true, co);
				co = null;
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
			Toast.makeText(mActivity, R.string.mess_valid_phone_number_blank,
					Toast.LENGTH_SHORT).show();
			mEtPhoneNumber.requestFocus();
			return false;
		} else if (mEtPhoneNumber.getText().toString().length() < 10) {
			Toast.makeText(mActivity, R.string.mess_valid_phone_number,
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
			Toast.makeText(mActivity, "Email Address is already existing.",
					Toast.LENGTH_SHORT).show();
			mEtEmailAddress.requestFocus();
			return false;
		}
		return true;
	}

	public boolean phoneNumberExists(Activity _activity, String number) {
		if (number != null) {
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
			return false;
		} else {
			return false;
		}
	}// contactExists

	public boolean emailExists(Activity _activity, String email) {
		if (email != null) {
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
			return false;
		} else {
			return false;
		}
	}// contactExists
}
