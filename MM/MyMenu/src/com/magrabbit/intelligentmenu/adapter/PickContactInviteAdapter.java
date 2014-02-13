package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.utils.Utils;

public class PickContactInviteAdapter extends ArrayAdapter<ContactObject> {

	private LayoutInflater mInflator;
	private String mIdSelected = "";
	private List<ContactObject> mOriginalContact;
	private ArrayList<ContactObject> mListContactFilter;
	private Context mContext;
	private boolean mIsTypeEmail;

	private ContactFilter filter;

	/**
	 * Constructor from a list of items
	 */
	public PickContactInviteAdapter(Activity context, List<ContactObject> data,
			boolean isTypeEmail) {
		super(context, R.layout.pick_contact_invite_item, data);
		mInflator = context.getLayoutInflater();
		this.mContext = context;

		this.mOriginalContact = new ArrayList<ContactObject>();
		this.mOriginalContact.addAll(data);
		this.mListContactFilter = new ArrayList<ContactObject>();
		this.mListContactFilter.addAll(data);

		this.mIsTypeEmail = isTypeEmail;
		for (ContactObject object : mOriginalContact) {
			if (object.isSelected()) {
				mIdSelected = object.getIdContact();
				break;
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflator.inflate(
						R.layout.pick_contact_invite_item, null);
				holder = new ViewHolder();
				holder.mNameContact = (TextView) convertView
						.findViewById(R.id.pick_contact_invite_item_tv_name_contact);

				holder.mPhoneNumber = (TextView) convertView
						.findViewById(R.id.pick_contact_invite_item_tv_phone_number);

				holder.mEmailAddress = (TextView) convertView
						.findViewById(R.id.pick_contact_invite_item_tv_email);
				holder.mChk = (RadioButton) convertView
						.findViewById(R.id.pick_contact_invite_item_rb_select);
				holder.mChk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!mIsTypeEmail) {
							if (getItem((Integer) v.getTag()).getPhoneNumber()
									.equals("")) {
								Toast.makeText(
										mContext,
										mContext.getString(R.string.mess_valid_phone_number),
										Toast.LENGTH_SHORT).show();

							} else if (getItem((Integer) v.getTag())
									.getPhoneNumber().length() < 10) {
								Toast.makeText(
										mContext,
										mContext.getString(R.string.mess_valid_phone_number),
										Toast.LENGTH_SHORT).show();
							} else {
								mIdSelected = getItem((Integer) v.getTag())
										.getIdContact();
							}
						} else {
							if (getItem((Integer) v.getTag()).getEmail()
									.equals("")) {
								Toast.makeText(
										mContext,
										mContext.getString(R.string.mess_valid_email_blank),
										Toast.LENGTH_SHORT).show();

							} else if (!Utils.isValidEmail(getItem(
									(Integer) v.getTag()).getEmail())) {
								Toast.makeText(
										mContext,
										mContext.getString(R.string.message_error_email),
										Toast.LENGTH_SHORT).show();
							} else {
								mIdSelected = getItem((Integer) v.getTag())
										.getIdContact();
							}
						}
						notifyDataSetChanged();
					}
				});
				convertView.setTag(holder);
				convertView.setTag(
						R.id.pick_contact_invite_item_tv_name_contact,
						holder.mNameContact);
				convertView.setTag(R.id.pick_contact_invite_item_tv_email,
						holder.mEmailAddress);
				convertView.setTag(R.id.pick_contact_invite_item_rb_select,
						holder.mChk);
				convertView.setTag(
						R.id.pick_contact_invite_item_tv_phone_number,
						holder.mPhoneNumber);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mChk.setTag(position);

			ContactObject contact = mListContactFilter.get(position);
			holder.mNameContact.setText(contact.getFullName());

			if (mIsTypeEmail) {
				holder.mEmailAddress.setText(contact.getEmail());
			} else {
				holder.mPhoneNumber.setText(contact.getPhoneNumber());
			}
			if (getItem(position).getIdContact().equals(mIdSelected)) {
				holder.mChk.setChecked(true);
				for (int i = 0; i < mOriginalContact.size(); i++) {
					if (getItem(position).getIdContact().equals(
							mOriginalContact.get(i).getIdContact())) {
						mOriginalContact.get(i).setSelected(true);
					} else {
						mOriginalContact.get(i).setSelected(false);
					}
				}
			} else {
				holder.mChk.setChecked(false);
				// getItem(position).setSelected(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public void resetData() {
		if (mOriginalContact != null) {
			mOriginalContact.clear();
			mOriginalContact = null;
		}
		if (mListContactFilter != null) {
			mListContactFilter.clear();
			mListContactFilter = null;
		}
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ContactFilter();
		}
		return filter;
	}

	static class ViewHolder {
		protected TextView mNameContact;
		protected TextView mPhoneNumber;
		protected TextView mEmailAddress;
		protected RadioButton mChk;
	}

	private class ContactFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			try {

				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					ArrayList<ContactObject> filteredItems = new ArrayList<ContactObject>();

					for (int i = 0, l = mOriginalContact.size(); i < l; i++) {
						ContactObject contact = mOriginalContact.get(i);
						if (mIsTypeEmail) {
							if (contact.getFullName().toLowerCase()
									.contains(constraint)
									|| contact.getEmail().toLowerCase()
											.contains(constraint))
								filteredItems.add(contact);
						} else {
							if (contact.getFullName().toLowerCase()
									.contains(constraint)
									|| contact.getPhoneNumber().toLowerCase()
											.contains(constraint))
								filteredItems.add(contact);
						}

					}
					result.count = filteredItems.size();
					result.values = filteredItems;
				} else {
					synchronized (this) {
						result.values = mOriginalContact;
						result.count = mOriginalContact.size();
					}
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			try {

				if (results.values == null) {
					return;
				}
				mListContactFilter = (ArrayList<ContactObject>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = mListContactFilter.size(); i < l; i++)
					add(mListContactFilter.get(i));
				notifyDataSetInvalidated();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
