package com.grupp32.freeelo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Variables.
    private Controller controller = new Controller();
    private TextView summonerIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        summonerIdText = (TextView) findViewById(R.id.summonerID);
        new TextViewUpdater().execute();
    }

    private class TextViewUpdater extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String summonerId = "";
            try {
                summonerId += controller.getSummonerId("cloudbells", "euw");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return summonerId;
        }

        @Override
        protected void onPostExecute(Object o) {
            summonerIdText.setText((String) o);
        }
    }
}
