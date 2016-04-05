package com.grupp32.freeelo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CurrentGame game;
    private ArrayList<Summoner> summoners;
    private ArrayList<Champion> champions;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        summoners = new ArrayList<Summoner>();
        champions = new ArrayList<Champion>();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        /*tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_top));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_jgl));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_mid));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_bot));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_sup));*/
        for(int i = 0; i < 5; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_fill));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(0);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        /*tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TABPOS", "pos = " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/

        Intent intent = getIntent();
        GetCurrentGame game = new GetCurrentGame();
        game.setProgressBar(progressBar);
        game.execute(intent.getExtras().getString("summoner"), intent.getExtras().getString("region"));
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int numOfTabs;

        public PagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            /*Log.e("POSITION1", "pos: " + position);
            Log.e("ArrayLIST", Arrays.toString(tabs.toArray()));
            if(tabs.isEmpty()) {
                return new TabFragment();
            } else {
                Log.e("POSITION2", "pos: " + position);
                Log.e("ArrayLIST", Arrays.toString(tabs.toArray()));
                return tabs.get(position);
            }*/
            if(!champions.isEmpty()) {
                return TabFragment.newInstance(position, summoners.get(position), champions.get(position));
            } else {
                return TabFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

    private class GetCurrentGame extends AsyncTask<String, Integer, String> {
        private ProgressBar bar;
        private String summonerName;

        public void setProgressBar(ProgressBar bar) {
            this.bar = bar;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("TEST", "doInBackground");
            summonerName = params[0];
            String region = params[1];

            try {
                game = new CurrentGame(summonerName, region);

                Summoner[] summonerArr = game.getSummoners();
                int progress = 0;
                for(Summoner summoner : summonerArr) {
                    Champion champ = summoner.getChampion();
                    Log.e("HELLO", champ.toString());

                    summoners.add(summoner);
                    champions.add(champ);

                    publishProgress(progress += 20);
                }

                return "" + summonerArr.length;
            } catch(Exception e) {
                e.printStackTrace();
                finish();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.v("TEST", "onProgressUpdate");
            if(bar != null) {
                bar.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("TEST", "onPostExecute" + result);
            if(result != null) {
                bar.setProgress(100);
                bar.setVisibility(View.INVISIBLE);

                int tabId = 0;
                for(Champion champ : champions) {
                    new IconSwitcher().execute("http://ddragon.leagueoflegends.com/cdn/6.6.1/img/champion/" + champ.getSquareImageFull(), Integer.toString(tabId));
                    tabId++;
                }

                viewPager.setCurrentItem(1);
                viewPager.setCurrentItem(2);
            } else {
                Toast.makeText(getApplication(), String.format(getString(R.string.not_in_game), summonerName), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class IconSwitcher extends AsyncTask<String, Bitmap, Bitmap> {
        private int tabId;

        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            tabId = Integer.parseInt(strings[1]);
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(strings[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            tabLayout.getTabAt(tabId).setIcon(new BitmapDrawable(getResources(), bitmap));
        }
    }
}