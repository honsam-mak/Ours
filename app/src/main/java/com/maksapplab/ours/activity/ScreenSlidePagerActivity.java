package com.maksapplab.ours.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.maksapplab.ours.R;
import com.maksapplab.ours.adapters.items.PhotoUri;
import com.maksapplab.ours.fragment.ScreenSlidePageFragment;
import com.maksapplab.ours.view.ZoomOutPageTransformer;

import static com.maksapplab.ours.constants.PhotoConstant.LISTVIEW_TO_SLIDEPAGE;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenSlidePagerActivity extends FragmentActivity {

    private final int DELAY_MS = 500;
    private final int PERIOD_MS = 3000;

    private int NUM_PAGES;
    private Timer swipeTimer;
    private int currentPage;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private ArrayList<PhotoUri> mPhotoUris;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        //Get the Bundle Object
        Bundle bundleObject = getIntent().getExtras();
        //Get ArrayList Bundle
        mPhotoUris = (ArrayList<PhotoUri>) bundleObject.getSerializable(LISTVIEW_TO_SLIDEPAGE);

        NUM_PAGES = mPhotoUris.size();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        autoShow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.screen_slide_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setScreenAlwaysOn(false);
        super.onBackPressed();
    }

    private void autoShow() {

        setScreenAlwaysOn(true);

        final Handler handler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void setScreenAlwaysOn(boolean on) {
        if(on) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * A simple pager adapter that represents ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.newInstance(mPhotoUris.get(position).getImagePath());
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
