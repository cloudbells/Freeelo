package com.grupp32.freeelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TabFragment extends Fragment {
    private ImageView background;
    private String summonerName;
    private String region;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        background = (ImageView) view.findViewById(R.id.background);

        summonerName = getActivity().getIntent().getExtras().getString("summoner");
        region = getActivity().getIntent().getExtras().getString("region");
        Toast.makeText(getActivity(), summonerName + " - Region: " + region, Toast.LENGTH_SHORT).show();

        try {
            new BackgroundSwitcher().execute(new URL("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Aatrox_0.jpg"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class BackgroundSwitcher extends AsyncTask<URL, Bitmap, Bitmap> {
        protected Bitmap doInBackground(URL... urls) {
            Bitmap bitmap = null;
            URL url = urls[0];
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            background.setImageBitmap(Bitmap.createScaledBitmap(bitmap, background.getWidth(), background.getHeight(), false));
        }
    }
}
