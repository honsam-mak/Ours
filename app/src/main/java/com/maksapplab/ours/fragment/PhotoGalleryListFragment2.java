package com.maksapplab.ours.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.activity.PhotoBrowserActivity;
import com.maksapplab.ours.adapters.PhotoAdapter;
import com.maksapplab.ours.adapters.items.PhotoItem;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;
import com.maksapplab.ours.utilities.PhotoGalleryAsyncLoader;
import com.maksapplab.ours.utilities.PhotoItemMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PhotoGalleryListFragment2 extends BaseFragment implements AbsListView.OnItemClickListener,
        AbsListView.OnItemLongClickListener,
        LoaderManager.LoaderCallbacks<List<PhotoItem>>  {

    protected OnFragmentInteractionListener mListener;
    protected AbsListView mListView;
    protected PhotoAdapter mAdapter;
    protected ArrayList<PhotoItem> mPhotoListItem;
    protected TextView mEmptyTextView;
    protected ProgressDialog mLoadingProgressDialog;
    protected TextView mTitleTextView;

    private boolean isLongClicked = false;
    private ImageView mImageView;

    /**
     * Required empty constructor
     */
    public PhotoGalleryListFragment2() {
        super();
    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static PhotoGalleryListFragment2 newInstance(int sectionNumber) {
        PhotoGalleryListFragment2 fragment = new PhotoGalleryListFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Gallery", "onCreate");

        // Create an empty loader and pre-initialize the photo list items as an empty list.
        Context context = getActivity().getBaseContext();

        // Set up empty mAdapter
        mPhotoListItem = new ArrayList<PhotoItem>() ;
        mAdapter = new PhotoAdapter(context,
                R.layout.photo_item,
                mPhotoListItem, false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        // Set the mAdapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mEmptyTextView = (TextView)view.findViewById(R.id.empty);
        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);

        // Set the cover image
        mImageView = (ImageView) view.findViewById(R.id.cover);

        String imgPath = PropertyManager.getInstance().getCoverImage();
        if(imgPath == null) {
            mImageView.setImageResource(R.drawable.placeholder);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            mImageView.setImageBitmap(bitmap);
        }

        // Show the empty text / message.
        resolveEmptyText();

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        // Set OnItemLongClickListener
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    /**
     * Used to show a generic empty text warning. Override in inheriting classes.
     */
    protected void resolveEmptyText(){
        if(mAdapter.isEmpty()){
            mEmptyTextView.setVisibility(View.VISIBLE);
            setEmptyText();
        } else {
            mEmptyTextView.setVisibility(View.INVISIBLE);
        }
    }

//    protected void resolveTitleText() {
//        if(!mAdapter.isEmpty()) {
//            PhotoItem pi = mAdapter.getItem(0);
//            mTitleTextView.setText("Week " + DateUtil.getPregnantWeek(pi.getDateTaken()));
//        }
//    }

    protected void resolveTitleText(Long diff) {
        if(!mAdapter.isEmpty()) {
            mTitleTextView.setText("Week " + diff);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Gallery", "onResume");

        resetFragmentState();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Gallery", "onAttach");
        try {
            mListener = (OnFragmentInteractionListener) activity;
            // Show a progress dialog.
            mLoadingProgressDialog = new ProgressDialog(getActivity());
            mLoadingProgressDialog.setMessage("Loading Photos...");
            mLoadingProgressDialog.setCancelable(true);
            mLoadingProgressDialog.show();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        cancelProgressDialog();
    }

    @Override
    public void onPause(){
        super.onPause();
        cancelProgressDialog();
    }

    @Override
    public void onStop(){
        super.onStop();
        cancelProgressDialog();
    }

    /**
     * This is only triggered when the user selects a single photo.
     * @param parent
     * @param view
     * @param position
     * @param id
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!isLongClicked) {
            Intent intent = new Intent(getActivity(), PhotoBrowserActivity.class);
            PhotoItem photoListItem = this.mAdapter.getItem(position);
            String imagePath = photoListItem.getFullImageUri().getPath();
            intent.putExtra("IMAGE_PATH", imagePath);
            startActivity(intent);
        } else {
            isLongClicked = false;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

        PhotoItem photoListItem = this.mAdapter.getItem(position);
        String imagePath = photoListItem.getThumbnailUri().getPath();

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        mImageView.setImageBitmap(bitmap);

        PropertyManager.getInstance().setCoverImage(imagePath);
        isLongClicked = true;

        return false;
    }

    /**
     * Used when hitting the back button to reset the mFragment UI state
     */
    public void resetFragmentState(){
        // Clear view state
        getActivity().invalidateOptionsMenu();
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText() {
        mEmptyTextView.setText("No Photos!");
    }

    /**
     * Loader Handlers for loading the photos in the background.
     */
    @Override
    public Loader<List<PhotoItem>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
        return new PhotoGalleryAsyncLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<PhotoItem>> loader, List<PhotoItem> data) {
        // Set the new data in the mAdapter.
        mPhotoListItem.clear();

        Log.d("Gallery", "photo item has " + data.size());

        PhotoItemMap pim = new PhotoItemMap(
                (ArrayList<PhotoItem>) data,
                DateUtil.parse(PropertyManager.getInstance().getPregnantDate()),
                Calendar.WEEK_OF_YEAR
        );

        for(Long d : pim.getResultMap().keySet()) {
//            if(d != 1) {
                ArrayList<PhotoItem> items = pim.getResultMap().get(d);
                for (int i = 0; i < items.size(); i++) {
                    PhotoItem item = items.get(i);
                    mPhotoListItem.add(item);
                }
                resolveTitleText(d);
                break;
//            }
        }

//        for(int i = 0; i < data.size();i++){
//            PhotoItem item = data.get(i);
//            mPhotoListItem.add(item);
//        }

        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
//        resolveTitleText();
        cancelProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<List<PhotoItem>> loader) {
        // Clear the data in the mAdapter.
        mPhotoListItem.clear();
        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
//        resolveTitleText();
        cancelProgressDialog();
    }

    /**
     * Save cancel for the progress loader
     */
    private void cancelProgressDialog(){
        if(mLoadingProgressDialog != null){
            if(mLoadingProgressDialog.isShowing()){
                mLoadingProgressDialog.cancel();
            }
        }
    }

}