package com.magrabbit.intelligentmenu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.magrabbit.intelligentmenu.R;

/**
 * @author baonguyen
 * 
 */
public class BalloonsAdapter implements InfoWindowAdapter {
	private LayoutInflater mInflater = null;
	private TextView mTvTitle;
	private TextView mTvSnipse;
	private ImageView mIvClose;

	public BalloonsAdapter(LayoutInflater inflater) {
		this.mInflater = inflater;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		View v = mInflater.inflate(R.layout.balloon_overlay, null);
		if (marker != null) {
			mTvTitle = (TextView) v.findViewById(R.id.balloon_item_title);
			mTvTitle.setText(marker.getTitle());

			mTvSnipse = (TextView) v.findViewById(R.id.balloon_item_snippet);
			mTvSnipse.setText(marker.getSnippet());

			mIvClose = (ImageView) v.findViewById(R.id.balloon_close);
			mIvClose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					marker.hideInfoWindow();
				}
			});
		}
		return (v);
	}

	@Override
	public View getInfoContents(Marker marker) {
		return (null);
	}
}
