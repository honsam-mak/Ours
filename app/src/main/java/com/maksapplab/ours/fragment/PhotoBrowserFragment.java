package com.maksapplab.ours.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maksapplab.ours.R;

import static com.maksapplab.ours.constants.PhotoConstant.URI;

/**
 * Example of loading an image into an image view using the image picker.
 *
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
public class PhotoBrowserFragment extends BaseFragment {

    // Reference to our image view we will use
    private ImageView mSelectedImage;
    private String mImageUri;

    /**
     * Default empty constructor.
     */
    public PhotoBrowserFragment(){
        super();
    }

    public static PhotoBrowserFragment newInstance(Uri imageUri) {
        PhotoBrowserFragment fragment = new PhotoBrowserFragment();
        Bundle args = new Bundle();
        args.putString(URI, imageUri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUri = getArguments().getString(URI);
        }
    }

    /**
     * OnCreateView fragment override
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_photo_browser, container, false);

        // Set the image view
        mSelectedImage = (ImageView)view.findViewById(R.id.imageViewFullSized);

        //Show the image
        Uri imageUri = Uri.parse(mImageUri);
        Bitmap bitmap = getBitmapFromCameraData(imageUri, getActivity());
        mSelectedImage.setImageBitmap(bitmap);

        return view;
    }

    private Bitmap getBitmapFromCameraData(Uri selectedImage, Context context){
        return BitmapFactory.decodeFile(selectedImage.toString());
    }
}
