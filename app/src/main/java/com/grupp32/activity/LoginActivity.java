package com.grupp32.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import junit.runner.Version;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import collection.CurrentGame;
import collection.Summoner;
import json.JSONParser;
import json.JSONRequester;
import version.ResourceUtil;
import version.VersionUtil;

/**
 * @author Alexander Johansson, Sigvard Nilsson
 */
public class LoginActivity extends AppCompatActivity {
	private TextView twSummonerName;
	private Spinner spRegion;
	private CircularProgressButton btnSearch;

	private Summoner[] previouslySearchedSummonerArr;
	private String previouslySearchedPatchVersion;
	private String previouslySearchedSummoner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btnSearch = (CircularProgressButton) findViewById(R.id.search_button);
		btnSearch.setIndeterminateProgressMode(true);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!checkPreviouslySearched()) {
					btnSearch.setProgress(1);
					checkCurrentGame();
				}
			}
		});

		twSummonerName = (TextView) findViewById(R.id.summoner_name);
		twSummonerName.requestFocus();
		twSummonerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					if(!checkPreviouslySearched()) {
						btnSearch.setProgress(1);
						checkCurrentGame();
					}
				}

				return false;
			}
		});

		spRegion = (Spinner) findViewById(R.id.region);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, R.layout.textview_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRegion.setAdapter(adapter);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			previouslySearchedSummonerArr = (Summoner[]) bundle.getSerializable("summoners");
			previouslySearchedPatchVersion = bundle.getString("version");
			previouslySearchedSummoner = bundle.getString("searched_summoner");
			twSummonerName.setText(previouslySearchedSummoner);
			Log.e("OnCreate bundle", previouslySearchedPatchVersion + " " + previouslySearchedSummoner);
		}
	}

	private boolean checkPreviouslySearched() {
		Log.e("checkPrevioslySearched", previouslySearchedPatchVersion + " " + previouslySearchedSummoner);
		if(previouslySearchedPatchVersion != null && previouslySearchedSummonerArr != null && previouslySearchedSummoner != null &&
				previouslySearchedSummoner.equals(twSummonerName.getText().toString().trim())) {
			startIntentToMain(previouslySearchedSummonerArr, previouslySearchedPatchVersion, previouslySearchedSummoner);
			Log.e("checkPrevioslySearched", "true");
			return true;
		}

		Log.e("checkPrevioslySearched", "false");
		return false;
	}

	private void checkCurrentGame() {
		String summoner = twSummonerName.getText().toString().trim();
		if (!summoner.isEmpty()) {
			String region = spRegion.getSelectedItem().toString();

			GetCurrentGame game = new GetCurrentGame();
			game.execute(summoner, region);
		} else {
			Toast.makeText(getApplication(), getString(R.string.enter_name), Toast.LENGTH_LONG).show();
			btnSearch.setProgress(0);
		}
	}

	private void startIntentToMain(Summoner[] summonerArr, String patchVersion, String searchedSummoner) {
		Log.e("StartIntentToMain", patchVersion + " " + searchedSummoner);
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable("summoners", summonerArr);
		extras.putString("version", patchVersion);
		extras.putString("searched_summoner", searchedSummoner);
		intent.putExtras(extras);
		startActivity(intent);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			if (w != null) {
				int scrCoords[] = new int[2];
				w.getLocationOnScreen(scrCoords);
				float x = event.getRawX() + w.getLeft() - scrCoords[0];
				float y = event.getRawY() + w.getTop() - scrCoords[1];

				if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(w.getWindowToken(), 0);
				}
			}
		}

		return ret;
	}

	private class GetCurrentGame extends AsyncTask<String, Integer, CurrentGame> {
		private String summonerName;
		private VersionUtil versionUtil;

		@Override
		protected CurrentGame doInBackground(String... params) {
			summonerName = params[0];
			String region = params[1];

            Context context = getApplicationContext();
            ResourceUtil resourceUtil = new ResourceUtil(context);
            JSONParser parser = new JSONParser(resourceUtil);
            JSONRequester requester = new JSONRequester(parser);
            versionUtil = new VersionUtil(context, parser, requester, resourceUtil, region);

			if (versionUtil.isFirstTime() || !versionUtil.isLatestVersion()) {
				publishProgress(2);
				versionUtil.updateVersion();

				publishProgress(100);

				publishProgress(3);
			} else {
				try {
					parser.updateResources();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			CurrentGame game;
			try {
				game = new CurrentGame(summonerName, region, parser, requester);

				publishProgress(100);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return game;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			switch(values[0]) {
				case 2:
					Toast.makeText(getApplication(), getString(R.string.patch_data_notcurrent), Toast.LENGTH_LONG).show();
					break;
				case 3:
					Toast.makeText(getApplication(), getString(R.string.patch_data_complete_proceeding), Toast.LENGTH_LONG).show();
					break;
			}

			btnSearch.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(CurrentGame result) {
			super.onPostExecute(result);
			if (result != null) {
				Summoner[] summoners = result.getSummoners();
				btnSearch.setProgress(0);
				startIntentToMain(summoners, versionUtil.getVersion(), summonerName);
			} else {
				btnSearch.setProgress(-1);
				Toast.makeText(getApplication(), String.format(getString(R.string.not_in_game), summonerName), Toast.LENGTH_LONG).show();

				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						btnSearch.setProgress(0);
					}
				}, 1000);
			}
		}
	}
}