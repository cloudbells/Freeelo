package com.grupp32.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import collection.Summoner;

/**
 * @author
 */
public class TabFragment extends Fragment implements View.OnClickListener {
	private ImageView background;
	private int bHeight, bWidth;

	private ImageButton iBtnSpell1;
	private ImageButton iBtnSpell2;
	private ImageButton iBtnUltimate;

	private ProgressBar pBarSpell1;
	private ProgressBar pBarSpell2;
	private ProgressBar pBarUltimate;
	private TextView twSpell1;
	private TextView twSpell2;
	private TextView twUltimate;

	private Summoner tabSummoner;

	public static TabFragment newInstance() {
		return new TabFragment();
	}

	public static TabFragment newInstance(int num, Summoner summoner) {
		TabFragment fragment = new TabFragment();

		Bundle args = new Bundle();
		args.putSerializable("summoner", summoner);
		args.putInt("position", num);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab, container, false);
		background = (ImageView) view.findViewById(R.id.background);
		bWidth = background.getWidth();
		bHeight = background.getHeight();

		iBtnSpell1 = (ImageButton) view.findViewById(R.id.summonerSpell1);
		iBtnSpell2 = (ImageButton) view.findViewById(R.id.summonerSpell2);
		iBtnUltimate = (ImageButton) view.findViewById(R.id.ultimate);
		iBtnSpell1.setOnClickListener(this);
		iBtnSpell2.setOnClickListener(this);
		iBtnUltimate.setOnClickListener(this);

		pBarSpell1 = (ProgressBar) view.findViewById(R.id.pBarSpell1);
		pBarSpell2 = (ProgressBar) view.findViewById(R.id.pBarSpell2);
		pBarUltimate = (ProgressBar) view.findViewById(R.id.pBarUltimate);

		twSpell1 = (TextView) view.findViewById(R.id.twSpell1);
		twSpell2 = (TextView) view.findViewById(R.id.twSpell2);
		twUltimate = (TextView) view.findViewById(R.id.twUltimate);

		final TextView summonerName = (TextView) view.findViewById(R.id.summonerName);
		final TextView championName = (TextView) view.findViewById(R.id.championName);
		final TextView masteries = (TextView) view.findViewById(R.id.masteries);
		final TextView runes = (TextView) view.findViewById(R.id.runes);
		final TextView rank = (TextView) view.findViewById(R.id.tierDivision);
		final ImageView tier = (ImageView) view.findViewById(R.id.tier);

		final Context context = getActivity();
		Log.e("PRE BUNDLE", "Fragment instantiated");
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			tabSummoner = (Summoner) bundle.getSerializable("summoner");
			if (tabSummoner != null) {
				summonerName.setText(tabSummoner.getName());
				championName.setText(tabSummoner.getChampion().getName());
				masteries.setText(tabSummoner.getMasteries());
				runes.setText(tabSummoner.getRunes().toString());
				String rankText = tabSummoner.getTier() + " " + tabSummoner.getDivision();
				rank.setText(rankText);

				int imageResource;
				String tierText = tabSummoner.getTier().toLowerCase();
				String divisionText = tabSummoner.getDivision().toLowerCase();
				if (tierText.equals("challenger") || tierText.equals("master") || tierText.equals("provisional")) {
					imageResource = context.getResources().getIdentifier("@drawable/" + tierText, null, context.getPackageName());
				} else {
					imageResource = context.getResources().getIdentifier("@drawable/" + tierText + "_" + divisionText, null, context.getPackageName());
				}

				tier.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), imageResource, 100, 100));

				try {
					new ImageSwitcher().execute(
							new URL("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + tabSummoner.getChampion().getKey() + "_0.jpg"),
							new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + tabSummoner.getSpell1().getImage()),
							new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + tabSummoner.getSpell2().getImage()),
							new URL("http://ddragon.leagueoflegends.com/cdn/6.8.1/img/spell/" + tabSummoner.getChampion().getUltimateImage())
							);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		} else {
			Log.e("BUNDLE", "BUNDLE EMPTY");
		}

		return view;
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		for (int i = 5; i > 0; i--) {
			if (Math.pow(2, i) <= inSampleSize) {
				inSampleSize = (int) Math.pow(2, i);
				break;
			}
		}

		return inSampleSize;
	}

	private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	private Bitmap decodeSampledBitmapFromStream(InputStream is, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		InputStream aux = inputStreamCopy(is);
		BitmapFactory.decodeStream(aux, null, options);
		try {
			aux.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(aux, null, options);
	}

	private InputStream inputStreamCopy(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		byte[] buffer = new byte[1024];

		try {
			while ((len = is.read(buffer)) > -1) baos.write(buffer, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(baos.toByteArray());
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
			case R.id.summonerSpell1:
				if(iBtnSpell1.getColorFilter() == null) {
					startTimer(tabSummoner.getSpell1().getCooldown(), iBtnSpell1, pBarSpell1, twSpell1);
				}
				break;
			case R.id.summonerSpell2:
				if(iBtnSpell2.getColorFilter() == null) {
					startTimer(tabSummoner.getSpell2().getCooldown(), iBtnSpell2, pBarSpell2, twSpell2);
				}
				break;
			case R.id.ultimate:
				if(iBtnUltimate.getColorFilter() == null) {
					int ultMaxRank = tabSummoner.getChampion().getUltimateMaxRank();
					startTimer((int)tabSummoner.getChampion().getUltimateCooldowns()[ultMaxRank - 1], iBtnUltimate, pBarUltimate, twUltimate);
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
				long seconds = leftTimeInMilliseconds / 1000;
				pResource.setProgress((int) seconds);
				twResource.setText(Long.toString(seconds));
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
			Bitmap[] bitmaps = new Bitmap[5];
			try {
				bitmaps[0] = decodeSampledBitmapFromStream((InputStream) urls[0].getContent(), bWidth, bHeight);
				bitmaps[1] = decodeSampledBitmapFromStream((InputStream) urls[1].getContent(), 64, 64);
				bitmaps[2] = decodeSampledBitmapFromStream((InputStream) urls[2].getContent(), 64, 64);
				bitmaps[3] = decodeSampledBitmapFromStream((InputStream) urls[3].getContent(), 64, 64);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmaps;
		}

		protected void onPostExecute(Bitmap[] bitmaps) {
			background.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[0], background.getWidth(), background.getHeight(), false));
			iBtnSpell1.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[1], iBtnSpell1.getWidth(), iBtnSpell1.getHeight(), false));
			iBtnSpell2.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[2], iBtnSpell2.getWidth(), iBtnSpell2.getHeight(), false));
			iBtnUltimate.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[3], iBtnUltimate.getWidth(), iBtnUltimate.getHeight(), false));

			iBtnSpell1.setVisibility(View.VISIBLE);
			iBtnSpell2.setVisibility(View.VISIBLE);
			iBtnUltimate.setVisibility(View.VISIBLE);
		}
	}
}
