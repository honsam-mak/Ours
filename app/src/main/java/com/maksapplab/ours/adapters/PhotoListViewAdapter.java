package com.maksapplab.ours.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.activity.PhotoBrowserActivity;
import com.maksapplab.ours.activity.ScreenSlidePagerActivity;
import com.maksapplab.ours.adapters.items.ListViewItem;
import com.maksapplab.ours.adapters.items.PhotoItem;
import com.maksapplab.ours.adapters.items.PhotoUri;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.view.GridViewInList;

import static com.maksapplab.ours.constants.PhotoConstant.PATH;
import static com.maksapplab.ours.constants.PhotoConstant.LISTVIEW_TO_SLIDEPAGE;

import java.util.ArrayList;

/**
 * Created by honsam on 9/30/14.
 */
public class PhotoListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ListViewItem> mList;
    private boolean isLongClicked;

    public PhotoListViewAdapter(Context context,
            ArrayList<ListViewItem> mList) {
        super();
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        } else {
            return this.mList.size();
        }
    }

    @Override
    public ListViewItem getItem(int position) {
        if(mList == null || mList.isEmpty()) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.listview_photo_item, null);
            holder.gridView = (GridViewInList) convertView.findViewById(R.id.gridlist);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.emptyTextView = (TextView) convertView.findViewById(R.id.empty);

            holder.titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Prepare the list of photoItems
                    ArrayList<PhotoItem> photoItemsList = mList.get(position).getPhotoItems();
                    ArrayList<PhotoUri> photoUris = new ArrayList<PhotoUri>();

                    for(PhotoItem item : photoItemsList) {
                        PhotoUri uri = new PhotoUri(item.getFullImageUri().getPath());
                        photoUris.add(uri);
                    }

                    // Create a Bundle and Put Bundle in on it
                    Bundle bundleObject = new Bundle();
                    bundleObject.putSerializable(LISTVIEW_TO_SLIDEPAGE, photoUris);

                    Intent intent = new Intent(mContext, ScreenSlidePagerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(bundleObject);
                    mContext.startActivity(intent);
                }
            });

            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    if(!isLongClicked) {

                        Intent intent = new Intent(mContext, PhotoBrowserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PhotoItem photoListItem = (PhotoItem) adapterView.getItemAtPosition(position);
                        String imagePath = photoListItem.getFullImageUri().getPath();
                        intent.putExtra(PATH, imagePath);
                        mContext.startActivity(intent);
                    } else {
                        isLongClicked = false;
                    }
                }
            });

            holder.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                    PhotoItem photoListItem = (PhotoItem) adapterView.getItemAtPosition(position);

                    String imagePath = photoListItem.getThumbnailUri().getPath();

                    PropertyManager.getInstance().setCoverImage(imagePath);
                    isLongClicked = true;

                    return false;
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(this.mList != null) {
            if(holder.gridView != null) {
                ListViewItem mListViewItem = this.mList.get(position);
                PhotoAdapter gridViewAdapter = new PhotoAdapter(mContext,
                        R.layout.photo_item,
                        mListViewItem.getPhotoItems(),
                        false);
                holder.gridView.setAdapter(gridViewAdapter);
                holder.titleTextView.setText(
                        mListViewItem.getTitle() +
                        mContext.getResources().getString(R.string.group_unit_text));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        GridViewInList gridView;
        TextView titleTextView;
        TextView emptyTextView;
    }
}
