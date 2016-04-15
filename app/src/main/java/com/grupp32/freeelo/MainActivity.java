package com.grupp32.freeelo;

import android.app.Application;
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
    private ArrayList<Summoner> summoners;
    private ArrayList<Champion> champions;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private long unixTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        summoners = new ArrayList<>();
        champions = new ArrayList<>();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for(int i = 0; i < 5; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_fill));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(0);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
            if(!summoners.isEmpty() && !champions.isEmpty()) {
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
            summonerName = params[0];
            String region = params[1];

            unixTime = System.currentTimeMillis() / 1000L;

            try {
                CurrentGame game = new CurrentGame(getApplicationContext(), summonerName, region);

                Summoner[] summonerArr = game.getSummoners();
                int progress = 0;
                for(Summoner summoner : summonerArr) {
                    Champion champ = summoner.getChampion();
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
            if(bar != null) {
                bar.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
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

                long time = (System.currentTimeMillis() / 1000L) - unixTime;
                Toast.makeText(getApplication(), "Responstid: " + time, Toast.LENGTH_LONG).show();

                for(Summoner summoner : summoners) {
                    Champion champ = summoner.getChampion();
                    Log.e("HELLO", champ.toString());
                    Spell spell1 = summoner.getSpell1();
                    Spell spell2 = summoner.getSpell2();
                    RuneCollection runes = summoner.getRunes();
                    Log.e("SPELL1", spell1.toString());
                    Log.e("SPELL2", spell2.toString());
                    Log.e("MASTERIES", summoner.getMasteries());
                    Log.e("RUNES", runes.toString());
                    Log.e("SUMMONER", summoner.toString());
                }
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
            TabLayout.Tab tab = tabLayout.getTabAt(tabId);
            if(tab != null) {
                tab.setIcon(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }
}