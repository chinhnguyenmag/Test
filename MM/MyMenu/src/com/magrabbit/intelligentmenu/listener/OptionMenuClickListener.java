package com.magrabbit.intelligentmenu.listener;

/**
 * Define the method to handle for button on Menu
 * 
 * @author ChinhNguyen
 */
public interface OptionMenuClickListener {

	/** action for invite */
	void onInviteClickListener();

	/** action for my account */
	void onMyAccountClickListener();

	/** action for next time */
	void onNextTimeClickListener();

	/** action for stats */
	void onStatsClickListener();

	/** action for intruction */
	void onIntructionsClickListener();

	/** action for thumbnail */
	void onThumbnailClickListener();

	/** action for search */
	void onSearchClickListener();

	/** action for feedback */
	void onFeedbackClickListener();
}
