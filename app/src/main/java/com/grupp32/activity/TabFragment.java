package com.grupp32.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import collection.Summoner;
import decoder.ImageStreamDecoder;

/**
 * @author Alexander Johansson, Christoffer Nilsson
 */
public class TabFragment extends FlexibleSpaceFragment<ObservableScrollView> implements View.OnClickListener, View.OnLongClickListener {
	private ImageButton btnSpell1;
	private ImageButton btnSpell2;
	private ImageButton btnUltimate;

	private ProgressBar pBarSpell1;
	private ProgressBar pBarSpell2;
	private ProgressBar pBarUltimate;
	private TextView twSpell1;
	private TextView twSpell2;
	private TextView twUltimate;

	private int twColor;
	private int twCritialColor;
	private TextToSpeech textToSpeech;

	private CountDownTimer[] timers = new CountDownTimer[3];

	private Summoner tabSummoner;

	private static final String DDRAGON_SPELL_URL = "http://ddragon.leagueoflegends.com/cdn/%s/img/spell/";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);

		final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
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
		btnSpell1.setOnLongClickListener(this);
		btnSpell2.setOnLongClickListener(this);
		btnUltimate.setOnLongClickListener(this);

		pBarSpell1 = (ProgressBar) view.findViewById(R.id.progress_bar_spell1);
		pBarSpell2 = (ProgressBar) view.findViewById(R.id.progress_bar_spell2);
		pBarUltimate = (ProgressBar) view.findViewById(R.id.progress_bar_ultimate);

		twSpell1 = (TextView) view.findViewById(R.id.text_spell1);
		twSpell2 = (TextView) view.findViewById(R.id.text_spell2);
		twUltimate = (TextView) view.findViewById(R.id.text_ultimate);

		twColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorProgressNumber)));
		twCritialColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorProgressNumberCritical)));

		textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					textToSpeech.setLanguage(Locale.UK);
				}
			}
		});

		final TextView twRunes = (TextView) view.findViewById(R.id.runes);
		final TextView twMasteries = (TextView) view.findViewById(R.id.masteries);
		final TextView twTier = (TextView) view.findViewById(R.id.tier_text);

		final ImageView ivTier = (ImageView) view.findViewById(R.id.imgTier);

		final TextView twLpWinLoss = (TextView) view.findViewById(R.id.lp_win_loss);
		final TextView twWinRatio = (TextView) view.findViewById(R.id.win_ratio);
		final TextView twChampWinRatio = (TextView) view.findViewById(R.id.champ_win_ratio);

		if (args != null && args.containsKey(ARG_SUMMONER)) {
			Summoner summoner = (Summoner) args.getSerializable(ARG_SUMMONER);
			tabSummoner = summoner;
			if(summoner != null) {
				String patchVersion = args.getString(ARG_VERSION);
				twRunes.setText(summoner.getRunes().toString());
				twMasteries.setText(summoner.getMasteries());

				int wins = summoner.getWins();
				int losses = summoner.getLosses();
				int champWins = summoner.getChampion().getWins();
				int champLosses = summoner.getChampion().getLosses();
				if((wins + losses) != 0) {
					twLpWinLoss.setText(String.format(twLpWinLoss.getText().toString(), summoner.getLeaguePoints(), wins, losses));
					double percentage = (double) wins / ((double) wins + (double) losses);
					twWinRatio.setText(String.format(twWinRatio.getText().toString(), percentage * 100, "%"));
				} else {
					twLpWinLoss.setText(String.format(twLpWinLoss.getText().toString(), summoner.getLeaguePoints(), wins, losses));
					twWinRatio.setText(String.format(twWinRatio.getText().toString(), 0.0, "%"));
				}

				if((champWins + champLosses) != 0) {
					double percentage = (double) champWins / ((double) champWins + (double) champLosses);
					twChampWinRatio.setText(String.format(twChampWinRatio.getText().toString(), percentage * 100, "%"));
				} else {
					twChampWinRatio.setText("");
				}

				Context context = getActivity();
				int imageResource;
				String tierText = summoner.getTier().toLowerCase();
				String divisionText = summoner.getDivision().toLowerCase();
				if (tierText.equals("challenger") || tierText.equals("master") || tierText.equals("provisional")) {
					imageResource = context.getResources().getIdentifier("@drawable/" + tierText, null, context.getPackageName());
					twTier.setText(summoner.getTier());
				} else {
					imageResource = context.getResources().getIdentifier("@drawable/" + tierText + "_" + divisionText, null, context.getPackageName());
					String tempText = summoner.getTier() + " " + summoner.getDivision();
					twTier.setText(tempText);
				}

				ImageStreamDecoder decoder = new ImageStreamDecoder();
				ivTier.setImageBitmap(decoder.decodeSampledBitmapFromResource(context.getResources(), imageResource, 200, 200));

				try {
					new ImageSwitcher().execute(
							new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getSpell1().getImage()),
							new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getSpell2().getImage()),
							new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getChampion().getUltimateImage())
					);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
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
		String spellReadySpeech = "";
		switch(v.getId()) {
			case R.id.summoner_spell1:
				if(btnSpell1.getColorFilter() == null) {
					spellReadySpeech = tabSummoner.getChampion().getName() + " " + tabSummoner.getSpell1().getName() + " ready";
					timers[0] = startTimer(tabSummoner.getSpell1().getCooldown(), btnSpell1, pBarSpell1, twSpell1, spellReadySpeech);
				}
				break;
			case R.id.summoner_spell2:
				if(btnSpell2.getColorFilter() == null) {
					spellReadySpeech = tabSummoner.getChampion().getName() + " " + tabSummoner.getSpell2().getName() + " ready";
					timers[1] = startTimer(tabSummoner.getSpell2().getCooldown(), btnSpell2, pBarSpell2, twSpell2, spellReadySpeech);
				}
				break;
			case R.id.summoner_ultimate:
				if(btnUltimate.getColorFilter() == null) {
					spellReadySpeech = tabSummoner.getChampion().getName() + " ultimate ready";
					double[] cooldowns = tabSummoner.getChampion().getUltimateCooldowns();
					timers[2] = startTimer((int) cooldowns[cooldowns.length - 1], btnUltimate, pBarUltimate, twUltimate, spellReadySpeech);
				}
				break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
			case R.id.summoner_spell1:
				resetTimer(0, btnSpell1, pBarSpell1, twSpell1);
				break;
			case R.id.summoner_spell2:
				resetTimer(1, btnSpell2, pBarSpell2, twSpell2);
				break;
			case R.id.summoner_ultimate:
				resetTimer(2, btnUltimate, pBarUltimate, twUltimate);
				break;
		}

		return true;
	}

	private void resetTimer(final int timerIndex, final ImageView ivResource, final ProgressBar pResource, final TextView twResource) {
		if(timers[timerIndex] != null) {
			timers[timerIndex].cancel();
		}
		resetColorFilter(ivResource);
		pResource.setVisibility(View.INVISIBLE);
		twResource.setText("");

		Toast.makeText(getActivity(), getString(R.string.reset_spell), Toast.LENGTH_LONG).show();
	}

	private CountDownTimer startTimer(final int seconds, final ImageView ivResource, final ProgressBar pResource, final TextView twResource, final String spellReadySpeech) {
		resourceToGrayscale(ivResource);
		pResource.setVisibility(View.VISIBLE);
		pResource.setMax(seconds);
		CountDownTimer timer = new CountDownTimer(seconds * 1000, 500) {
			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long curSecond = leftTimeInMilliseconds / 1000;
				pResource.setProgress(seconds - (int) curSecond);
				if((int) curSecond <= 10) {
					twResource.setTextColor(twCritialColor);
				} else {
					twResource.setTextColor(twColor);
				}
				twResource.setText(Long.toString(curSecond));
			}

			@Override
			public void onFinish() {
				textToSpeech.speak(spellReadySpeech, TextToSpeech.QUEUE_ADD, null, null);
				resetColorFilter(ivResource);
				pResource.setVisibility(View.INVISIBLE);
				twResource.setText("");
			}
		}.start();

		return timer;
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