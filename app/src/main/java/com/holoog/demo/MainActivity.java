package com.holoog.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_INIT_TAB = "extraInitTab";
    ViewPager viewPager;
    MainActivity.PagerAdapter pagerAdapter;
    int init = 0;
    boolean initLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init = getIntent().getIntExtra(EXTRA_INIT_TAB, 0);
        assignViews();
        setUpViewPager();
    }

    /**
     * Assign adapter to viewPager
     */
    private void setUpViewPager() {
        if(pagerAdapter==null){
            pagerAdapter=new MainActivity.PagerAdapter(getSupportFragmentManager());
        }
        viewPager.setAdapter(pagerAdapter);
        if (!initLoaded) {
            viewPager.setCurrentItem(init);
            initLoaded = true;
        }
    }

    /**
     * assign views to object from layout
     */
    private void assignViews() {
        viewPager=(ViewPager)findViewById(R.id.viewPager);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new IntroductionFragment();
/*                case 1:
                    return new DefaultRegionFragment();
                case 2:
                    return new RegionPreferenceFragment();
                case 3:
                    return new CustomMasterFragment();
                case 4:
                    return new SetRegionFragment();
                case 5:
                    return new GetRegionFragment();
                case 6:
                    return new FullNumberFragment();*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
