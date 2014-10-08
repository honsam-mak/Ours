package com.maksapplab.ours.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maksapplab.ours.R;
import com.maksapplab.ours.constants.DatePickerConstant;
import com.maksapplab.ours.fragment.DatePickerFragment;
import com.maksapplab.ours.fragment.PhotoBrowserFragment;

import static com.maksapplab.ours.constants.PhotoConstant.PATH;

public class PhotoBrowserActivity extends Activity {

    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browser);

        Intent intent = getIntent();
        mImagePath = intent.getStringExtra(PATH);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, PhotoBrowserFragment.newInstance(Uri.parse(mImagePath)))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
        case R.id.action_settings_note:
             Intent intent = new Intent(this, PhotoNoteActivity.class);
             intent.putExtra(PATH, mImagePath);
             startActivity(intent);
             return true;
        case R.id.action_settings_display_date:
            DatePickerFragment newFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("ImageName", Uri.parse(mImagePath).getLastPathSegment());
            args.putInt(DatePickerConstant.TYPE, DatePickerConstant.DISPLAY_DATE);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "datePicker");
             break;
        }

        return super.onOptionsItemSelected(item);
    }
}
