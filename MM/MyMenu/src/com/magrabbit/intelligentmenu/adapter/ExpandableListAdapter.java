package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyFriendListEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;

/**
 * 
 * @author baonguyen
 * @Description: Adapter for applying Group header and Child header
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> mListDataHeader; // header titles
	private List<MyFriendListEntity> mListDataChild;// child data
	// in

	private TextView mTvFriendName;
	private ImageView mIvAvatar;
	private ImageView mIvArrow;
	// Process radio buttons
	private RadioButton mRdbButton;
	private int mIndexOfFriend = -1;
	private TextView mTvStatus;

	private Process_ExpandableListAdapter mProcessSelect;

	// Loader for images
	private ImageLoader mImageLoader;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			List<MyFriendListEntity> listChildData, int indexOfFriend,
			Process_ExpandableListAdapter process) {
		this.mContext = context;
		this.mIndexOfFriend = indexOfFriend;
		this.mProcessSelect = process;
		if (this.mListDataHeader == null) {
			this.mListDataHeader = new ArrayList<String>();
		}
		this.mListDataHeader.clear();
		this.mListDataHeader.addAll(listDataHeader);
		// Add list of friends into ExpandableList
		if (this.mListDataChild == null) {
			this.mListDataChild = new ArrayList<MyFriendListEntity>();
		}
		this.mListDataChild.clear();
		this.mListDataChild.addAll(listChildData);

		// Image Loader
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(60, 60);
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.mListDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.mListDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.expandable_group, null);
		}
		// Set image of arrow when click on Group title
		mIvArrow = (ImageView) convertView
				.findViewById(R.id.expandable_group_iv_arrow);
		if (isExpanded) {
			mIvArrow.setBackgroundResource(R.drawable.ic_arrow_select);
		} else {
			mIvArrow.setBackgroundResource(R.drawable.ic_arrow_unselect);

		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.mListDataChild.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.expandable_item, null);
		}
		try {

			mTvFriendName = (TextView) convertView
					.findViewById(R.id.expandable_item_tv_friendname);
			mTvFriendName.setText(mListDataChild.get(childPosition).getFirstName()
					+ " " + mListDataChild.get(childPosition).getLastName());
			mTvStatus = (TextView)convertView.findViewById(R.id.expandable_item_tv_status);

			if (mListDataChild.get(childPosition).getStatus().toUpperCase().equals(
					"PENDING")) {
				mTvStatus.setTextColor(mContext.getResources().getColor(
						R.color.myfriend_list_pending));
			} else if (mListDataChild.get(childPosition).getStatus().toUpperCase()
					.equals("REGISTERED")) {
				mTvStatus.setTextColor(mContext.getResources().getColor(
						R.color.myfriend_list_registered));
			}
			mTvStatus.setText(mListDataChild.get(childPosition).getStatus());

			mIvAvatar = (ImageView) convertView
					.findViewById(R.id.expandable_item_iv_avatar);
			mImageLoader.DisplayImage(
					ServerURL.URL + mListDataChild.get(childPosition).getAvatar(),
					mIvAvatar);

			mRdbButton = (RadioButton) convertView
					.findViewById(R.id.expandable_item_rdb_select);

			// Processing when click on RadioButton and apply it into layout
			mRdbButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mIndexOfFriend = childPosition;
					notifyDataSetChanged();
					// Transfer click event
					mProcessSelect.click_radioButton(mIndexOfFriend);
				}
			});
			if (mIndexOfFriend == -1) {
				mRdbButton.setChecked(false);
			} else if (childPosition == mIndexOfFriend) {
				mRdbButton.setChecked(true);
			} else {
				mRdbButton.setChecked(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.mListDataChild.size();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setIndexFriend(int index) {
		this.mIndexOfFriend = index;
	}

	public void clear() {
		try {
			if (mImageLoader != null) {
				mImageLoader.clearCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static abstract class Process_ExpandableListAdapter {
		public abstract void click_radioButton(int indexOfFriend);
	}
}
