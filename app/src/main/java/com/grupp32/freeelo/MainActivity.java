package com.grupp32.freeelo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Top").setIcon(R.drawable.ic_top));
        tabLayout.addTab(tabLayout.newTab().setText("Jungle").setIcon(R.drawable.ic_jgl));
        tabLayout.addTab(tabLayout.newTab().setText("Mid").setIcon(R.drawable.ic_mid));
        tabLayout.addTab(tabLayout.newTab().setText("Bot").setIcon(R.drawable.ic_bot));
        tabLayout.addTab(tabLayout.newTab().setText("Supp").setIcon(R.drawable.ic_sup));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        Bundle extras = intent.getExtras();
        if(extras != null) {
            Toast.makeText(this, extras.get("summoner") + " - Region: " + extras.get("region"), Toast.LENGTH_LONG).show();
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabFragment tab1 = new TabFragment();
                    return tab1;
                case 1:
                    TabFragment tab2 = new TabFragment();
                    return tab2;
                case 2:
                    TabFragment tab3 = new TabFragment();
                    return tab3;
                case 3:
                    TabFragment tab4 = new TabFragment();
                    return tab4;
                case 4:
                    TabFragment tab5 = new TabFragment();
                    return tab5;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}