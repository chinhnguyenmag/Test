package com.magrabbit.intelligentmenu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.magrabbit.intelligentmenu.R;
import com.magrabbit.intelligentmenu.utils.CodeRequest;
import com.magrabbit.intelligentmenu.utils.StringExtraUtils;

public class YoutubePlayActivity extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener {

	public static final String API_KEY = "AIzaSyAZQIG9IW7-jL86LtIeBtol-1dYc7K-JPI";
	public static String VIDEO_ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube_play);
		YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.activity_youtube_playerview);
		youTubePlayerView.initialize(API_KEY, this);

		VIDEO_ID = getIntent().getExtras().getString(
				StringExtraUtils.KEY_YOUTUBE_ID);
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult result) {
		if (result.isUserRecoverableError()) {
			result.getErrorDialog(this, CodeRequest.CODE_REQUEST_UPDATE_YOUTUBE)
					.show();
		} else {
			Toast.makeText(this, "YouTubePlayer failed: " + result.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(VIDEO_ID);
			player.setFullscreen(true);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CodeRequest.CODE_REQUEST_UPDATE_YOUTUBE) {
			Intent intent = new Intent(YoutubePlayActivity.this,
					YoutubePlayActivity.class);
			intent.putExtra(StringExtraUtils.KEY_YOUTUBE_ID, VIDEO_ID);
			if (intent != null) {
				startActivity(intent);
			}
			finish();
		}
	}
}
