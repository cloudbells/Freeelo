package com.grupp32.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Alexander Johansson, Sigvard Nilsson
 */
public class LoginActivity extends AppCompatActivity {
	private TextView twSummonerName;
	private Spinner spRegion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		twSummonerName = (TextView) findViewById(R.id.summonerName);
		assert twSummonerName != null;
		twSummonerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					startIntentToMain();
				}

				return false;
			}
		});

		spRegion = (Spinner) findViewById(R.id.region);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, R.layout.textview_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRegion.setAdapter(adapter);

		final Button btnOK = (Button) findViewById(R.id.okButton);
		assert btnOK != null;
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startIntentToMain();
			}
		});
	}

	private void startIntentToMain() {
		String summoner = twSummonerName.getText().toString().trim();
		if (!summoner.isEmpty()) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			String region = spRegion.getSelectedItem().toString();
			intent.putExtra("summoner", summoner);
			intent.putExtra("region", region);
			startActivity(intent);
		} else {
			Toast.makeText(getApplication(), getString(R.string.enter_name), Toast.LENGTH_LONG).show();
		}
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
}