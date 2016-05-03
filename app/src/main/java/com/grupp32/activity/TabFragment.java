package com.grupp32.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import collection.Summoner;
import decoder.ImageStreamDecoder;

/**
 * @author Alexander Johansson, Christoffer Nilsson
 */
public class TabFragment extends FlexibleSpaceWithImageBaseFragment<ObservableScrollView> implements View.OnClickListener {
	private ImageButton btnSpell1;
	private ImageButton btnSpell2;
	private ImageButton btnUltimate;

	private ProgressBar pBarSpell1;
	private ProgressBar pBarSpell2;
	private ProgressBar pBarUltimate;
	private TextView twSpell1;
	private TextView twSpell2;
	private TextView twUltimate;

	private Summoner tabSummoner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);

		final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
		// TouchInterceptionViewGroup should be a parent view other than ViewPager.
		// This is a workaround for the issue #117:
		// https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
		scrollView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

		Bundle args = getArguments();
		if (args != null && args.containsKey(ARG_SCROLL_Y)) {
			final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
			ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, scrollY);
				}
			});
			updateFlexibleSpace(scrollY, view);
		} else {
			updateFlexibleSpace(0, view);
		}

		btnSpell1 = (ImageButton) view.findViewById(R.id.summoner_spell1);
		btnSpell2 = (ImageButton) view.findViewById(R.id.summoner_spell2);
		btnUltimate = (ImageButton) view.findViewById(R.id.summoner_ultimate);
		btnSpell1.setOnClickListener(this);
		btnSpell2.setOnClickListener(this);
		btnUltimate.setOnClickListener(this);

		pBarSpell1 = (ProgressBar) view.findViewById(R.id.progress_bar_spell1);
		pBarSpell2 = (ProgressBar) view.findViewById(R.id.progress_bar_spell2);
		pBarUltimate = (ProgressBar) view.findViewById(R.id.progress_bar_ultimate);

		twSpell1 = (TextView) view.findViewById(R.id.text_spell1);
		twSpell2 = (TextView) view.findViewById(R.id.text_spell2);
		twUltimate = (TextView) view.findViewById(R.id.text_ultimate);

		if (args != null && args.containsKey("summoner")) {
			Summoner summoner = (Summoner) args.getSerializable("summoner");
			tabSummoner = summoner;
			try {
				new ImageSwitcher().execute(
						new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + summoner.getSpell1().getImage()),
						new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + summoner.getSpell2().getImage()),
						new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + summoner.getChampion().getUltimateImage())
						);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		scrollView.setScrollViewCallbacks(this);

		return view;
	}

	@Override
	protected void updateFlexibleSpace(int scrollY) {
		// Sometimes scrollable.getCurrentScrollY() and the real scrollY has different values.
		// As a workaround, we should call scrollVerticallyTo() to make sure that they match.
		Scrollable s = getScrollable();
		s.scrollVerticallyTo(scrollY);

		// If scrollable.getCurrentScrollY() and the real scrollY has the same values,
		// calling scrollVerticallyTo() won't invoke scroll (or onScrollChanged()), so we call it here.
		// Calling this twice is not a problem as long as updateFlexibleSpace(int, View) has idempotence.
		updateFlexibleSpace(scrollY, getView());
	}

	@Override
	protected void updateFlexibleSpace(int scrollY, View view) {
		ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);

		// Also pass this event to parent Activity
		MainActivity parentActivity = (MainActivity) getActivity();
		if (parentActivity != null) {
			parentActivity.onScrollChanged(scrollY, scrollView);
		}
	}

	private void resourceToGrayscale(ImageView view) {
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);

		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		view.setColorFilter(filter);
	}

	private void resetColorFilter(ImageView view) {
		view.setColorFilter(null);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.summoner_spell1:
				if(btnSpell1.getColorFilter() == null) {
					startTimer(tabSummoner.getSpell1().getCooldown(), btnSpell1, pBarSpell1, twSpell1);
				}
				break;
			case R.id.summoner_spell2:
				if(btnSpell2.getColorFilter() == null) {
					startTimer(tabSummoner.getSpell2().getCooldown(), btnSpell2, pBarSpell2, twSpell2);
				}
				break;
			case R.id.summoner_ultimate:
				if(btnUltimate.getColorFilter() == null) {
					int ultMaxRank = tabSummoner.getChampion().getUltimateMaxRank();
					double[] cooldowns = tabSummoner.getChampion().getUltimateCooldowns();
					startTimer((int) cooldowns[cooldowns.length - 1], btnUltimate, pBarUltimate, twUltimate);
				}
				break;
		}
	}

	private void startTimer(final int seconds, final ImageView ivResource, final ProgressBar pResource, final TextView twResource) {
		resourceToGrayscale(ivResource);
		pResource.setVisibility(View.VISIBLE);
		pResource.setMax(seconds);
		new CountDownTimer(seconds * 1000, 500) {
			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long curSecond = leftTimeInMilliseconds / 1000;
				pResource.setProgress(seconds - (int) curSecond);
				if((int) curSecond <= 10) {
					twResource.setTextColor(Color.parseColor("#B20000"));
				} else {
					twResource.setTextColor(Color.parseColor("#ffe066"));
				}
				twResource.setText(Long.toString(curSecond));
			}

			@Override
			public void onFinish() {
				resetColorFilter(ivResource);
				pResource.setVisibility(View.INVISIBLE);
				twResource.setText("");
			}
		}.start();
	}

	private class ImageSwitcher extends AsyncTask<URL, Bitmap[], Bitmap[]> {
		protected Bitmap[] doInBackground(URL... urls) {
			Bitmap[] bitmaps = new Bitmap[4];
			ImageStreamDecoder decoder = new ImageStreamDecoder();
			try {
				bitmaps[0] = decoder.decodeSampledBitmapFromStream((InputStream) urls[0].getContent(), 64, 64);
				bitmaps[1] = decoder.decodeSampledBitmapFromStream((InputStream) urls[1].getContent(), 64, 64);
				bitmaps[2] = decoder.decodeSampledBitmapFromStream((InputStream) urls[2].getContent(), 64, 64);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmaps;
		}

		protected void onPostExecute(Bitmap[] bitmaps) {
			btnSpell1.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[0], btnSpell1.getWidth(), btnSpell1.getHeight(), false));
			btnSpell2.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[1], btnSpell2.getWidth(), btnSpell2.getHeight(), false));
			btnUltimate.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[2], btnUltimate.getWidth(), btnUltimate.getHeight(), false));

			btnSpell1.setVisibility(View.VISIBLE);
			btnSpell2.setVisibility(View.VISIBLE);
			btnUltimate.setVisibility(View.VISIBLE);
		}
	}
}