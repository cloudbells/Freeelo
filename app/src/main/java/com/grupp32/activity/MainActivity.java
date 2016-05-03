package com.grupp32.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import collection.Summoner;
import decoder.ImageStreamDecoder;
import widget.SlidingTabLayout;

/**
 * @author Alexander Johansson
 */
public class MainActivity extends BaseActivity {
	private SlidingTabLayout tabLayout;
	private ViewPager viewPager;
	private NavigationAdapter pagerAdapter;

	private ArrayList<Summoner> summoners = new ArrayList<Summoner>();
	private ImageView flexibleImage;
	private TextView flexibleTitle;

	private int flexibleSpaceHeight;
	private int tabHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		flexibleImage = (ImageView) findViewById(R.id.image);
		flexibleTitle = (TextView) findViewById(R.id.title);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			Summoner[] summonerArr = (Summoner[]) bundle.getSerializable("summoners");
			for (Summoner summoner : summonerArr) {
				summoners.add(summoner);
			}
		}

		pagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		pagerAdapter.setSummoners(summoners);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				try {
					new ImageSwitcher().execute(
                            new URL("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + summoners.get(position).getChampion().getKey() + "_0.jpg")
                    );

					flexibleTitle.setText(summoners.get(position).getName());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		flexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);

		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(R.string.app_name);

		tabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		tabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
		tabLayout.setDistributeEvenly(true);
		tabLayout.setViewPager(viewPager);

		ScrollUtils.addOnGlobalLayoutListener(tabLayout, new Runnable() {
			@Override
			public void run() {
				translateTab(0, false);
			}
		});

		viewPager.setCurrentItem(2);
	}

	/**
	 * Called by children Fragments when their scrollY are changed.
	 * They all call this method even when they are inactive
	 * but this Activity should listen only the active child,
	 * so each Fragments will pass themselves for Activity to check if they are active.
	 *
	 * @param scrollY scroll position of Scrollable
	 * @param s       caller Scrollable view
	 */
	public void onScrollChanged(int scrollY, Scrollable s) {
		FlexibleSpaceWithImageBaseFragment fragment = (FlexibleSpaceWithImageBaseFragment) pagerAdapter.getItemAt(viewPager.getCurrentItem());
		if (fragment == null) {
			return;
		}
		View view = fragment.getView();
		if (view == null) {
			return;
		}
		Scrollable scrollable = (Scrollable) view.findViewById(R.id.scroll);
		if (scrollable == null) {
			return;
		}
		if (scrollable == s) {
			// This method is called by not only the current fragment but also other fragments
			// when their scrollY is changed.
			// So we need to check the caller(S) is the current fragment.
			int adjustedScrollY = Math.min(scrollY, flexibleSpaceHeight - tabHeight);
			translateTab(adjustedScrollY, false);
			propagateScroll(adjustedScrollY);
		}
	}

	private void propagateScroll(int scrollY) {
		// Set scrollY for the fragments that are not created yet
		pagerAdapter.setScrollY(scrollY);

		// Set scrollY for the active fragments
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			// Skip current item
			if (i == viewPager.getCurrentItem()) {
				continue;
			}

			// Skip destroyed or not created item
			FlexibleSpaceWithImageBaseFragment f = (FlexibleSpaceWithImageBaseFragment) pagerAdapter.getItemAt(i);
			if (f == null) {
				continue;
			}

			View view = f.getView();
			if (view == null) {
				continue;
			}
			f.setScrollY(scrollY, flexibleSpaceHeight);
			f.updateFlexibleSpace(scrollY);
		}
	}

	private void translateTab(int scrollY, boolean animated) {
		int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
		View imageView = flexibleImage;
		View overlayView = findViewById(R.id.overlay);
		TextView titleView = flexibleTitle;

		// Translate overlay and image
		float flexibleRange = flexibleSpaceImageHeight - getActionBarSize();
		int minOverlayTransitionY = tabHeight - overlayView.getHeight();
		ViewHelper.setTranslationY(overlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
		ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

		// Change alpha of overlay
		ViewHelper.setAlpha(overlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

		// Scale title text
		float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY - tabHeight) / flexibleRange, 0, 0.3f);
		ViewHelper.setPivotX(titleView, 0);
		ViewHelper.setPivotY(titleView, 0);
		ViewHelper.setScaleX(titleView, scale);
		ViewHelper.setScaleY(titleView, scale);

		// Translate title text
		int maxTitleTranslationY = flexibleSpaceImageHeight - tabHeight - getActionBarSize();
		int titleTranslationY = maxTitleTranslationY - scrollY;
		ViewHelper.setTranslationY(titleView, titleTranslationY);

		// If tabs are moving, cancel it to start a new animation.
		ViewPropertyAnimator.animate(tabLayout).cancel();
		// Tabs will move between the top of the screen to the bottom of the image.
		float translationY = ScrollUtils.getFloat(-scrollY + flexibleSpaceHeight - tabHeight, 0, flexibleSpaceHeight - tabHeight);
		if (animated) {
			// Animation will be invoked only when the current tab is changed.
			ViewPropertyAnimator.animate(tabLayout)
					.translationY(translationY)
					.setDuration(200)
					.start();
		} else {
			// When Fragments' scroll, translate tabs immediately (without animation).
			ViewHelper.setTranslationY(tabLayout, translationY);
		}
	}

	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {
		private ArrayList<Summoner> summoners = new ArrayList<Summoner>();

		private int scrollY;

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setSummoners(ArrayList<Summoner> summoners) {
			this.summoners = summoners;
		}

		public void setScrollY(int scrollY) {
			this.scrollY = scrollY;
		}

		@Override
		protected Fragment createItem(int position) {
			FlexibleSpaceWithImageBaseFragment fragment;
			fragment = new TabFragment();
			fragment.setArguments(scrollY, summoners.get(position));
			return fragment;
		}

		@Override
		public int getCount() {
			return summoners.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return summoners.get(position).getChampion().getName();
		}
	}

	private class ImageSwitcher extends AsyncTask<URL, Bitmap, Bitmap> {
		protected Bitmap doInBackground(URL... urls) {
			Bitmap bitmap = null;
			ImageStreamDecoder decoder = new ImageStreamDecoder();
			try {
				bitmap = decoder.decodeSampledBitmapFromStream((InputStream) urls[0].getContent(), 300, 240);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) {
			if(bitmap != null) {
				flexibleImage.setImageBitmap(bitmap);
			}
		}
	}
}