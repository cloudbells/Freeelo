package com.grupp32.freeelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TabFragment extends Fragment {
    private ImageView background;

    public static TabFragment newInstance() {
        return new TabFragment();
    }

    public static TabFragment newInstance(int num, Summoner summoner, Champion champion) {
        TabFragment fragment = new TabFragment();

        Bundle args = new Bundle();
        args.putSerializable("summoner", summoner);
        args.putSerializable("champions", champion);
        args.putInt("num", num);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        background = (ImageView) view.findViewById(R.id.background);
        final TextView summonerName = (TextView) view.findViewById(R.id.summonerName);

        Log.e("PRE BUNDLE", "Fragment instantiated");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Summoner summoner = (Summoner) bundle.getSerializable("summoner");
            Champion champ = (Champion) bundle.getSerializable("champions");
            if(summoner != null && champ != null) {
                Log.e("HELPME", champ.toString());
                summonerName.setText(summoner.getName());

                try {
                    new BackgroundSwitcher().execute(new URL("http://ddragon.leagueoflegends.com/cdn/img/champions/loading/" + champ.getKey() + "_0.jpg"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e("BUNDLE", "BUNDLE EMPTY");
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
