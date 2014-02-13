package com.magrabbit.intelligentmenu.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.listener.OptionMenuClickListener;

/**
 * This class is the OptionMenu, it will custom the way to show click button
 * menu of phone and handle the event
 * 
 * @author ChinhNguyen
 * 
 */
public class OptionMenu extends LinearLayout {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context contain this control
	 */
	public OptionMenu(Context context) {
		this(context, null);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context contain this control
	 * @param attrs
	 *            define the way custome this view
	 */
	public OptionMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.optionmenu, this, true);
	}

	/**
	 * Init the footer bar
	 * 
	 * @param flag
	 * @param listener
	 */
	public void initFooterBar(boolean invite, boolean myaccount,
			boolean nexttime, boolean stats, boolean instructions,
			boolean thumbnail, boolean search, boolean feedback,
			final OptionMenuClickListener listener) {

		// init invite button
		if (invite) {
			Button btnInvite = (Button) findViewById(R.id.option_menu_btn_invite);
			if (btnInvite != null) {
				btnInvite.setVisibility(View.VISIBLE);
				btnInvite.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onInviteClickListener();
					}
				});
			}
		}

		// init nexttime button
		if (nexttime) {
			Button btNextTime = (Button) findViewById(R.id.option_menu_btn_nexttime);
			if (btNextTime != null) {
				btNextTime.setVisibility(View.VISIBLE);
				btNextTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onNextTimeClickListener();
					}
				});
			}
		}

		// init stats button
		if (stats) {
			Button btStats = (Button) findViewById(R.id.option_menu_btn_stats);
			if (btStats != null) {
				btStats.setVisibility(View.VISIBLE);
				btStats.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onStatsClickListener();
					}
				});
			}
		}

		// init myaccount button.
		if (myaccount) {
			Button btMyAccount = (Button) findViewById(R.id.option_menu_btn_myaccount);
			if (btMyAccount != null) {
				btMyAccount.setVisibility(View.VISIBLE);
				btMyAccount.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onMyAccountClickListener();
					}
				});
			}
		}
		// init instructions button
		if (instructions) {
			Button btInstructions = (Button) findViewById(R.id.option_menu_btn_instructions);
			if (btInstructions != null) {
				btInstructions.setVisibility(View.VISIBLE);
				btInstructions.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onIntructionsClickListener();
					}
				});
			}
		}
		// init thumbnail button
		if (thumbnail) {
			Button btThumbnail = (Button) findViewById(R.id.option_menu_btn_thumbnail);
			if (btThumbnail != null) {
				btThumbnail.setVisibility(View.VISIBLE);
				btThumbnail.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onThumbnailClickListener();
					}
				});
			}
		}
		// init search button
		if (search) {
			Button btSearch = (Button) findViewById(R.id.option_menu_btn_search);
			if (btSearch != null) {
				btSearch.setVisibility(View.VISIBLE);
				btSearch.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onSearchClickListener();
					}
				});
			}
		}
		// init feedback button
		if (feedback) {
			Button btFeedback = (Button) findViewById(R.id.option_menu_btn_feedback);
			if (btFeedback != null) {
				btFeedback.setVisibility(View.VISIBLE);
				btFeedback.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onFeedbackClickListener();
					}
				});
			}
		}
	}
}
