package com.grupp32.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.json.JSONException;

import java.util.HashSet;
import java.util.Iterator;

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
	private AutoCompleteTextView twSummonerName;
	private Spinner spRegion;
	private CircularProgressButton btnSearch;

    private String[] autoCompleteArray;
    private HashSet autoCompleteSet;
    private SharedPreferences prefs;

	private Summoner[] previouslySearchedSummonerArr;
	private String previouslySearchedPatchVersion;
	private String previouslySearchedSummoner;
    private String previouslySearchedRegion;

	private static final String ARG_SUMMONERS = "ARG_SUMMONERS";
	private static final String ARG_SEARCHED_SUMMONER = "ARG_SEARCHED_SUMMONERS";
	private static final String ARG_VERSION = "ARG_VERSION";
    private static final String ARG_REGION = "ARG_REGION";

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
					btnSearch.setEnabled(false);
					btnSearch.setProgress(1);
					checkCurrentGame();
				}
			}
		});

        setupSharedPrefs();
        setupSearchBar();

		spRegion = (Spinner) findViewById(R.id.region);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, R.layout.textview_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRegion.setAdapter(adapter);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			previouslySearchedSummonerArr = (Summoner[]) bundle.getSerializable(ARG_SUMMONERS);
			previouslySearchedPatchVersion = bundle.getString(ARG_VERSION);
			previouslySearchedSummoner = bundle.getString(ARG_SEARCHED_SUMMONER);
            previouslySearchedRegion = bundle.getString(ARG_REGION);
			twSummonerName.setText(previouslySearchedSummoner);
            spRegion.setSelection(((ArrayAdapter)spRegion.getAdapter()).getPosition(previouslySearchedRegion));
		}
	}

    private void setupSharedPrefs() {
        prefs = getSharedPreferences("autocomplete", MODE_PRIVATE);
        HashSet hashSet = (HashSet) prefs.getStringSet("searchedNames", new HashSet<String>());
        autoCompleteSet = (HashSet) hashSet.clone();
        autoCompleteArray = new String[hashSet.size()];
        Iterator it = hashSet.iterator();
        int index = 0;
        while (it.hasNext()) {
            autoCompleteArray[index++] = (String) it.next();
        }
    }

    private void setupSearchBar() {
        twSummonerName = (AutoCompleteTextView) findViewById(R.id.summoner_name);
        twSummonerName.requestFocus();
        twSummonerName.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(!checkPreviouslySearched()) {
						btnSearch.setEnabled(false);
                        btnSearch.setProgress(1);
                        checkCurrentGame();
                    }
                }
                return false;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoCompleteArray);
        twSummonerName.setAdapter(adapter);
    }

	private boolean checkPreviouslySearched() {
		if (previouslySearchedPatchVersion != null && previouslySearchedSummonerArr != null && previouslySearchedSummoner != null &&
				previouslySearchedSummoner.equals(twSummonerName.getText().toString().trim()) && previouslySearchedRegion != null) {
			startIntentToMain(previouslySearchedSummonerArr, previouslySearchedPatchVersion, previouslySearchedSummoner, previouslySearchedRegion);
			return true;
		}

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

		btnSearch.setEnabled(true);
	}

	private void startIntentToMain(Summoner[] summonerArr, String patchVersion, String searchedSummoner, String searchedRegion) {
        autoCompleteSet.add(searchedSummoner);
        prefs.edit().putStringSet("searchedNames", autoCompleteSet).apply();
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable(ARG_SUMMONERS, summonerArr);
		extras.putString(ARG_VERSION, patchVersion);
		extras.putString(ARG_SEARCHED_SUMMONER, searchedSummoner);
        extras.putString(ARG_REGION, searchedRegion);
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
				startIntentToMain(summoners, versionUtil.getVersion(), summonerName, (String) spRegion.getSelectedItem());
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