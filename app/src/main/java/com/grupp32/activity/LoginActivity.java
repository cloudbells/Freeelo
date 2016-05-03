package com.grupp32.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import collection.CurrentGame;
import collection.Summoner;

/**
 * @author Alexander Johansson, Sigvard Nilsson
 */
public class LoginActivity extends AppCompatActivity {
	private TextView twSummonerName;
	private Spinner spRegion;
	private CircularProgressButton btnSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btnSearch = (CircularProgressButton) findViewById(R.id.search_button);
		btnSearch.setIndeterminateProgressMode(true);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnSearch.setProgress(1);
				checkCurrentGame();
			}
		});

		twSummonerName = (TextView) findViewById(R.id.summoner_name);
		twSummonerName.requestFocus();
		twSummonerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					btnSearch.setProgress(1);
					checkCurrentGame();
				}

				return false;
			}
		});

		spRegion = (Spinner) findViewById(R.id.region);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, R.layout.textview_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRegion.setAdapter(adapter);
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

	private void startIntentToMain(Summoner[] summonerArr) {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable("summoners", summonerArr);
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

		@Override
		protected CurrentGame doInBackground(String... params) {
			summonerName = params[0];
			String region = params[1];

			CurrentGame game;
			try {
				game = new CurrentGame(getApplicationContext(), summonerName, region);

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
			btnSearch.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(CurrentGame result) {
			super.onPostExecute(result);
			if (result != null) {
				Summoner[] summoners = result.getSummoners();
				btnSearch.setProgress(0);
				startIntentToMain(summoners);
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