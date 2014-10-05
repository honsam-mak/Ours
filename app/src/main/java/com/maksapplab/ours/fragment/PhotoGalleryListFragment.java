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
import android.widget.ListView;
import android.widget.TextView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.activity.PhotoBrowserActivity;
import com.maksapplab.ours.adapters.PhotoAdapter;
import com.maksapplab.ours.adapters.PhotoListViewAdapter;
import com.maksapplab.ours.adapters.items.ListViewItem;
import com.maksapplab.ours.adapters.items.PhotoItem;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;
import com.maksapplab.ours.utilities.PhotoGalleryAsyncLoader;
import com.maksapplab.ours.utilities.PhotoItemMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PhotoGalleryListFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<List<PhotoItem>>  {

    protected OnFragmentInteractionListener mListener;
//    protected AbsListView mListView;
//    protected PhotoAdapter mAdapter;
//    protected ArrayList<PhotoItem> mPhotoListItem;
    protected TextView mEmptyTextView;
    protected ProgressDialog mLoadingProgressDialog;
//    protected TextView mTitleTextView;

    private ListView mListView;
    private PhotoListViewAdapter mListViewAdapter;
    private ArrayList<ListViewItem> mListViewItem;

    private ImageView mImageView;

    /**
     * Required empty constructor
     */
    public PhotoGalleryListFragment() {
        super();
    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static PhotoGalleryListFragment newInstance(int sectionNumber) {
        PhotoGalleryListFragment fragment = new PhotoGalleryListFragment();
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
//        mPhotoListItem = new ArrayList<PhotoItem>() ;
//        mAdapter = new PhotoAdapter(context,
//                R.layout.photo_item,
//                mPhotoListItem, false);
        mListViewItem = new ArrayList<ListViewItem>();
        mListViewAdapter = new PhotoListViewAdapter(context,
                mListViewItem);

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
//        mListView = (AbsListView) view.findViewById(android.R.id.list);
//        mListView.setAdapter(mAdapter);
        mEmptyTextView = (TextView)view.findViewById(R.id.empty);
//        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        mListView = (ListView) view.findViewById(R.id.gallery_list);
        mListView.setAdapter(mListViewAdapter);

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

        return view;
    }

    /**
     * Used to show a generic empty text warning. Override in inheriting classes.
     */
    protected void resolveEmptyText(){
//        if(mAdapter.isEmpty()){
//            mEmptyTextView.setVisibility(View.VISIBLE);
//            setEmptyText();
//        } else {
//            mEmptyTextView.setVisibility(View.INVISIBLE);
//        }
        if(mListViewItem == null || mListViewItem.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            setEmptyText();
        } else {
            mEmptyTextView.setVisibility(View.INVISIBLE);
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
//        mPhotoListItem.clear();
        if(mListViewAdapter !=null) {
//            mListViewAdapter.getItem(0).getPhotoItems().clear();

            Log.d("Gallery", "photo item has " + data.size());

            PhotoItemMap pim = new PhotoItemMap(
                    (ArrayList<PhotoItem>) data,
                    DateUtil.parse(PropertyManager.getInstance().getPregnantDate()),
                    Calendar.WEEK_OF_YEAR
            );

            for (Long d : pim.getResultMap().descendingKeySet()) {
                ListViewItem lvItem = new ListViewItem();
                ArrayList<PhotoItem> items = pim.getResultMap().get(d);
                lvItem.setTitle(d.toString());
                lvItem.setPhotoItems(items);
                mListViewItem.add(lvItem);

            }
            mListViewAdapter.notifyDataSetChanged();

            resolveEmptyText();
        }
        cancelProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<List<PhotoItem>> loader) {
        // Clear the data in the mAdapter.
//        mPhotoListItem.clear();
        mListViewAdapter.notifyDataSetChanged();

        resolveEmptyText();
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