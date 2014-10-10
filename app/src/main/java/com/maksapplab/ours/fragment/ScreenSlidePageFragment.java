package com.maksapplab.ours.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.utilities.PhotoUtil;

import static com.maksapplab.ours.constants.PhotoConstant.URI;

public class ScreenSlidePageFragment extends Fragment {

    // Reference to our image view we will use
    private ImageView mSelectedImage;
    private String mImageUri;

    public ScreenSlidePageFragment() {super();}

    public static ScreenSlidePageFragment newInstance(String imageUri) {

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putString(URI, imageUri);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        mSelectedImage = (ImageView) view.findViewById(R.id.image_display);
        mSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * If the screen is touched, then go back to parent activity
                 */
                getActivity().onBackPressed();
            }
        });

        //Show the image
        mSelectedImage.setImageBitmap(
                PhotoUtil.getBitmapFromCameraData(Uri.parse(mImageUri)));

        return view;
    }
}
