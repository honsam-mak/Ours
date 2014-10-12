package com.maksapplab.ours.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.maksapplab.ours.R;
import com.maksapplab.ours.constants.DatePickerConstant;
import com.maksapplab.ours.fragment.BaseFragment;
import com.maksapplab.ours.fragment.CameraIntentFragment;
import com.maksapplab.ours.fragment.DatePickerFragment;
import com.maksapplab.ours.fragment.PhotoGalleryListFragment;
import com.maksapplab.ours.fragment.PlaceholderFragment;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.PhotoUtil;
import com.maksapplab.ours.utilities.ThumbnailUtil;

import java.util.Locale;

public class MainActivity extends CameraActivity implements
        ActionBar.TabListener, BaseFragment.OnFragmentInteractionListener  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    boolean mRefreshGallery;

    public static final int SIMPLE_CAMERA_INTENT_FRAGMENT = 0;
    public static final int SIMPLE_PHOTO_GALLERY_FRAGMENT = 1;
    public static final int SIMPLE_IMAGEVIEW_FRAGMENT     = 2;

    private static final int IMAGE_PICKER_SELECT = 999;

    public SectionsPagerAdapter getmSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PropertyManager.getInstance().setContext(this);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings_target_date:
                DatePickerFragment newFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt(DatePickerConstant.TYPE, DatePickerConstant.PREGNANT_DATE);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "datePicker");
                return true;
            case R.id.action_settings_import:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "onActivityResult");
        if (requestCode == IMAGE_PICKER_SELECT  && resultCode == Activity.RESULT_OK) {
            Uri targetUri = PhotoUtil.copyPhoto(data, this);

            if(targetUri != null) {
                ThumbnailUtil.createThumbnails(targetUri);
                PropertyManager.getInstance().saveDisplayDateByCreatedTime(targetUri.getLastPathSegment());

                //Refresh viewpager
                mSectionsPagerAdapter.notifyDataSetChanged();
                if(mViewPager.getCurrentItem() == SIMPLE_CAMERA_INTENT_FRAGMENT) {
                    mViewPager.setCurrentItem(SIMPLE_PHOTO_GALLERY_FRAGMENT, false);
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        if(tab.getPosition() == SIMPLE_PHOTO_GALLERY_FRAGMENT &&
                mRefreshGallery) {
            // Add here in order to refresh only on tab change to photo gallery
            mSectionsPagerAdapter.notifyDataSetChanged();
            mRefreshGallery = false;
        }

        mViewPager.setCurrentItem(tab.getPosition(), false);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * Handle Incoming messages from contained fragments.
     */

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    public void onFragmentInteraction(String id) {}

    @Override
    public void onFragmentInteraction(int actionId) {

        switch(actionId) {
            case SIMPLE_IMAGEVIEW_FRAGMENT:
            break;
        }
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
     public class SectionsPagerAdapter extends FragmentStatePagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case SIMPLE_CAMERA_INTENT_FRAGMENT:
                    return CameraIntentFragment.newInstance(position + 1);
                case SIMPLE_PHOTO_GALLERY_FRAGMENT:
                    return PhotoGalleryListFragment.newInstance(position + 1);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        /*
         * Must add the getItemPosition. It addressed the issue of calling notifyDataSetChange not working.
         * After adding it, the photo gallery list fragment will be refreshed when take photo.
         */
        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }

    public void setFreshGallery(boolean fresh) {
        mRefreshGallery = fresh;
    }

    public boolean isNeedFreshGallery() {
        return mRefreshGallery;
    }
}
