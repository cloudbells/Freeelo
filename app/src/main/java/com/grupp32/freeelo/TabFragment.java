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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TabFragment extends Fragment {
    private ImageView background;
    //private TabLayout tabLayout;
    //private int tabId;

    //private String summonerName;
    //private String region;
    static TabFragment newInstance() {
        TabFragment fragment = new TabFragment();
        return fragment;
    }

    static TabFragment newInstance(int num, Champion champion) {
        TabFragment fragment = new TabFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("champion", champion);
        args.putInt("num", num);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        background = (ImageView) view.findViewById(R.id.background);
        //tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);

        //summonerName = getActivity().getIntent().getExtras().getString("summoner");
        //region = getActivity().getIntent().getExtras().getString("region");
        //Toast.makeText(getActivity(), summonerName + " - Region: " + region, Toast.LENGTH_SHORT).show();

        Log.e("PRE BUNDLE", "Fragment instantiated");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Champion champ = (Champion) bundle.getSerializable("champion");
            //tabId = bundle.getInt("tabId");
            Log.e("HELPME", champ.toString());

            try {
                new BackgroundSwitcher().execute(new URL("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + champ.getKey() + "_0.jpg"));
                //, new URL("http://ddragon.leagueoflegends.com/cdn/6.6.1/img/champion/" + champ.getSquareImageFull()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("BUNDLE", "BUNDLE EMPTY");
        }

        return view;
    }

    private class BackgroundSwitcher extends AsyncTask<URL, Bitmap, Bitmap> {
        protected Bitmap doInBackground(URL... urls) {
            //Bitmap[] bitmap = new Bitmap[2];
            Bitmap bitmap = null;
            URL url = urls[0];
            //URL url2 = urls[1];
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                //bitmap[0] = BitmapFactory.decodeStream((InputStream) url.getContent());
                //bitmap[1] = BitmapFactory.decodeStream((InputStream) url2.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            background.setImageBitmap(Bitmap.createScaledBitmap(bitmap, background.getWidth(), background.getHeight(), false));
            //tabLayout.getTabAt(tabId).setIcon(new BitmapDrawable(getResources(), bitmap[1]));
        }
    }
}
