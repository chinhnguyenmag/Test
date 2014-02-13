package com.magrabbit.intelligentmenu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.model.MenuTypeObject;

public class MenuTypeAdapter extends ArrayAdapter<MenuTypeObject> {

	private List<MenuTypeObject> mList;
	private LayoutInflater mInflator;

	/**
	 * Constructor from a list of items
	 */
	public MenuTypeAdapter(Context context, List<MenuTypeObject> list) {
		super(context, R.layout.pick_contact_item, list);
		this.mList = list;
		mInflator = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		MenuTypeObject item = getItem(position);
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.item_menuitem, null);
			holder = new ViewHolder();
			holder.mName = (TextView) convertView
					.findViewById(R.id.item_menuitem_tv_title);
			holder.mChk = (CheckBox) convertView
					.findViewById(R.id.item_menuitem_cb_select);
			holder.mChk
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton view,
								boolean isChecked) {
							int position = (Integer) view.getTag();
							mList.get(position).mIsChecked = view.isChecked();
							notifyDataSetChanged();
						}
					});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mChk.setTag(position);
		if (item != null) {
			holder.mName.setText(item.mName);
			holder.mChk.setChecked(item.mIsChecked);
		}

		return convertView;
	}

	static class ViewHolder {
		protected TextView mName;
		protected CheckBox mChk;
	}

}
