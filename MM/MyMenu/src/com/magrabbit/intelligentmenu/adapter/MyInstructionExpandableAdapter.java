package com.magrabbit.intelligentmenu.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.activity.YoutubePlayActivity;
import com.magrabbit.intelligentmenu.loader.ImageLoader;
import com.magrabbit.intelligentmenu.model.MyInstructionEntity;
import com.magrabbit.intelligentmenu.model.MyInstructionItemEntity;
import com.magrabbit.intelligentmenu.server.ServerURL;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;
import com.magrabbit.intelligentmenu.utils.Utils;

/**
 * 
 * @author baonguyen
 * @Description: Adapter for applying Group header and Child header
 */
public class MyInstructionExpandableAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<MyInstructionEntity> mListDataHeader; // header titles
	// private List<MyIntructionsItemsObject> mListDataChild;// child data
	private HashMap<MyInstructionEntity, List<MyInstructionItemEntity>> listDataChild;
	// in

	private TextView mTvName;
	private TextView mTvTime;
	private Button mIvPlay;
	private ImageView mIvArrow;
	private ImageView mIconHeader;
	private TextView mNameHeader;
	// Process radio buttons

	private process_MyInstructionListAdapter mProcessSelect;

	// Loader for images
	private ImageLoader mImageLoader;

	public MyInstructionExpandableAdapter(
			Context context,
			List<MyInstructionEntity> listDataHeader,
			HashMap<MyInstructionEntity, List<MyInstructionItemEntity>> listDataChild,
			int indexOfFriend, process_MyInstructionListAdapter process) {
		this.mContext = context;
		this.mProcessSelect = process;
		if (this.mListDataHeader == null) {
			this.mListDataHeader = new ArrayList<MyInstructionEntity>();
		}
		this.mListDataHeader.clear();
		this.mListDataHeader.addAll(listDataHeader);
		// Add list of friends into ExpandableList
		if (this.listDataChild == null) {
			this.listDataChild = new HashMap<MyInstructionEntity, List<MyInstructionItemEntity>>();
		}
		this.listDataChild.clear();
		this.listDataChild.putAll(listDataChild);

		// Image Loader
		mImageLoader = new ImageLoader(mContext);
		mImageLoader.setIcoDefault(R.drawable.ic_load_img_150);
		mImageLoader.setSizeBitmap(30, 30);
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
			convertView = inflater.inflate(
					R.layout.my_intructions_expandable_group, null);
		}
		try {
			mIconHeader = (ImageView) convertView
					.findViewById(R.id.my_intructions_expandable_group_logo);
			mNameHeader = (TextView) convertView
					.findViewById(R.id.my_intructions_expandable_group_tv_name);

			mImageLoader.DisplayImage(
					ServerURL.URL
							+ mListDataHeader.get(groupPosition).getIcon(),
					mIconHeader);

			mNameHeader.setText(mListDataHeader.get(groupPosition).mName);

			// Set image of arrow when click on Group title
			mIvArrow = (ImageView) convertView
					.findViewById(R.id.my_intructions_expandable_group_iv_arrow);
			if (isExpanded) {
				mIvArrow.setBackgroundResource(R.drawable.ic_arrow_select);
			} else {
				mIvArrow.setBackgroundResource(R.drawable.ic_arrow_unselect);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.listDataChild.get(mListDataHeader.get(groupPosition)).get(
				childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.my_intructions_expandable_item, null);
		}

		try {
			mTvName = (TextView) convertView
					.findViewById(R.id.my_intructions_expandable_item_tv_name);
			mTvName.setText(listDataChild
					.get(mListDataHeader.get(groupPosition)).get(childPosition)
					.getItemName());

			mTvTime = (TextView) convertView
					.findViewById(R.id.my_intructions_expandable_item_tv_time);
			mTvTime.setText(listDataChild
					.get(mListDataHeader.get(groupPosition)).get(childPosition)
					.getTime());

			mIvPlay = (Button) convertView
					.findViewById(R.id.my_intructions_expandable_item_iv_play_video);
			mIvPlay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							YoutubePlayActivity.class);
					if (!Utils.isNetworkConnected(mContext)) {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.mess_error_network),
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						intent.putExtra(
								StringExtraUtils.KEY_YOUTUBE_ID,
								listDataChild
										.get(mListDataHeader.get(groupPosition))
										.get(childPosition).getYoutubeId());
						if (intent != null) {
							mContext.startActivity(intent);
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int childrenCount = 0;
		try {
			childrenCount = this.listDataChild.get(
					mListDataHeader.get(groupPosition)).size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (childrenCount != 0) {
			return childrenCount;
		} else
			return 0;

	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public static abstract class process_MyInstructionListAdapter {
		public abstract void click_play(int indexOfFriend);
	}
}
