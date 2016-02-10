package com.rescuebird.game.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.birdrise.game.AdsController;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication implements AdsController {

    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-4562761250390940/7822965319";
    public static AdView bannerAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Create a gameView and a bannerAd Adview
        View gameview = initializeForView(new BirdRiseGame(this), config);
        setupAds();

        // Define the layout
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layout.addView(bannerAd, params);
        setContentView(layout);
	}

    public void setupAds() {
        bannerAd = new AdView(this);
        bannerAd.setVisibility(View.INVISIBLE);
        bannerAd.setBackgroundColor(0xff000000); // black
        bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
        bannerAd.setAdSize(AdSize.SMART_BANNER);
    }

    @Override
    public void showBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isWifiConnected()) {
                    bannerAd.setVisibility(View.VISIBLE);
                    com.google.android.gms.ads.AdRequest.Builder builder = new com.google.android.gms.ads.AdRequest.Builder();
                    com.google.android.gms.ads.AdRequest ad = builder.build();
                    bannerAd.loadAd(ad);
                }
            }
        });
    }

    @Override
    public void hideBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerAd.setVisibility(View.INVISIBLE);
            }
        });
    }

    public Boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (ni != null && ni.isConnected());
    }
}
