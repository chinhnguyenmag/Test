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
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.listener.PickContactShareListener;
import com.magrabbit.intelligentmenu.model.ContactObject;
import com.magrabbit.intelligentmenu.utils.Utils;

public class PickContactAdapter extends ArrayAdapter<ContactObject> {

	private LayoutInflater mInflator;
	private Context mContext;
	private PickContactShareListener mListener;
	private boolean mIsTypeEmail = false;

	private List<ContactObject> mOriginalContact;
	private ArrayList<ContactObject> mListContactFilter;
	private ContactFilter filter;

	/**
	 * Constructor from a list of items
	 */
	public PickContactAdapter(Activity context, List<ContactObject> data,
			boolean isTypeEmail, PickContactShareListener listener) {
		super(context, R.layout.pick_contact_item, data);

		this.mOriginalContact = new ArrayList<ContactObject>();
		this.mOriginalContact.addAll(data);
		this.mListContactFilter = new ArrayList<ContactObject>();
		this.mListContactFilter.addAll(data);

		this.mContext = context;
		mListener = listener;
		mInflator = context.getLayoutInflater();

		this.mIsTypeEmail = isTypeEmail;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflator.inflate(R.layout.pick_contact_item,
						null);
				holder = new ViewHolder();
				holder.mNameContact = (TextView) convertView
						.findViewById(R.id.pick_contact_item_tv_name_contact);

				holder.mPhoneNumber = (TextView) convertView
						.findViewById(R.id.pick_contact_item_tv_phone_number);

				holder.mEmailAddress = (TextView) convertView
						.findViewById(R.id.pick_contact_item_tv_email);
				holder.mChk = (CheckBox) convertView
						.findViewById(R.id.pick_contact_item_cb_select);
				holder.mChk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int getPosition = (Integer) v.getTag();
						if (!mListContactFilter.get(getPosition).isSelected()) {

							if (!mIsTypeEmail) {
								if (mListContactFilter.get(getPosition)
										.getPhoneNumber().equals("")) {
									Toast.makeText(
											mContext,
											mContext.getString(R.string.mess_valid_phone_number),
											Toast.LENGTH_SHORT).show();
									mListContactFilter.get(getPosition)
											.setSelected(false);
								} else if (mListContactFilter.get(getPosition)
										.getPhoneNumber().length() < 10) {
									Toast.makeText(
											mContext,
											mContext.getString(R.string.mess_valid_phone_number),
											Toast.LENGTH_SHORT).show();
									mListContactFilter.get(getPosition)
											.setSelected(false);
								} else {
									mListContactFilter.get(getPosition)
											.setSelected(true);
								}
							} else {
								if (mListContactFilter.get(getPosition)
										.getEmail().equals("")) {
									Toast.makeText(
											mContext,
											mContext.getString(R.string.mess_valid_email_blank),
											Toast.LENGTH_SHORT).show();
									mListContactFilter.get(getPosition)
											.setSelected(false);
								} else if (!Utils
										.isValidEmail(mListContactFilter.get(
												getPosition).getEmail())) {
									Toast.makeText(
											mContext,
											mContext.getString(R.string.message_error_email),
											Toast.LENGTH_SHORT).show();
									mListContactFilter.get(getPosition)
											.setSelected(false);
								} else {
									mListContactFilter.get(getPosition)
											.setSelected(true);
								}
							}
						} else {
							mListContactFilter.get(getPosition).setSelected(
									false);
						}
						notifyDataSetChanged();
//						mListener.getListChooseContactShare(mListContactFilter,
//								false);
					}
				});

				convertView.setTag(holder);
				convertView.setTag(R.id.pick_contact_item_tv_name_contact,
						holder.mNameContact);

				convertView.setTag(R.id.pick_contact_item_tv_email,
						holder.mEmailAddress);
				convertView.setTag(R.id.pick_contact_item_cb_select,
						holder.mChk);
				convertView.setTag(R.id.pick_contact_item_tv_phone_number,
						holder.mPhoneNumber);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mChk.setTag(position);

			ContactObject contact = mListContactFilter.get(position);
			if (mIsTypeEmail) {
				holder.mEmailAddress.setText(contact.getEmail());
			} else {
				holder.mPhoneNumber.setText(contact.getPhoneNumber());
			}

			holder.mNameContact.setText(contact.getFullName());
			// holder.mPhoneNumber.setText(mData.get(position).getPhoneNumber());
			// holder.mEmailAddress.setText(mData.get(position).getEmail());
			holder.mChk.setChecked(contact.isSelected());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder {
		protected TextView mNameContact;
		protected TextView mPhoneNumber;
		protected TextView mEmailAddress;
		protected CheckBox mChk;
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
