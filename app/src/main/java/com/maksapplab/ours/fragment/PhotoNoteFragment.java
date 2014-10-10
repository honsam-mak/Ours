package com.maksapplab.ours.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.manager.PropertyManager;

import static com.maksapplab.ours.constants.PhotoConstant.SUFFIX_NOTE;
import static com.maksapplab.ours.constants.PhotoConstant.URI;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoNoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PhotoNoteFragment extends BaseFragment {

    private final String SAVE = "Save";

    private String DEFAULT_TEXT;
    private String mImageUri;
    private String mImageName;

    private ImageView mImageView;
    private EditText mEditText;
    private Button mButton;

//    private OnFragmentInteractionListener mListener;

    public static PhotoNoteFragment newInstance(Uri uri) {
        PhotoNoteFragment fragment = new PhotoNoteFragment();
        Bundle args = new Bundle();
        args.putString(URI, uri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoNoteFragment() {
        // Required empty public constructor
        super();
    }

    private String getDefaultText() {
        return getResources().getString(R.string.default_note_text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DEFAULT_TEXT = getDefaultText();

        if (getArguments() != null) {
            mImageUri = getArguments().getString(URI);
            mImageName = Uri.parse(mImageUri).getLastPathSegment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_note, container, false);

        mImageView = (ImageView) view.findViewById(R.id.note_imageView);
        mButton = (Button) view.findViewById(R.id.note_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditText.isEnabled()) {
                    //EditText is disable, so turn it on
                    mEditText.setEnabled(true);

                    if(DEFAULT_TEXT.equals(mEditText.getText().toString()))
                        mEditText.setText("");

                    mButton.setText(SAVE);
                }else {
                    saveNote(mImageName, mEditText.getText().toString());
                }
            }
        });

        //Show the image
        Uri imageUri = Uri.parse(mImageUri);
        Bitmap bitmap = getBitmapFromCameraData(imageUri, getActivity());
        mImageView.setImageBitmap(bitmap);

        mEditText = (EditText) view.findViewById(R.id.note_editText);
        String note = getNote(mImageName);
        mEditText.setText(note);
        mEditText.setEnabled(false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    private Bitmap getBitmapFromCameraData(Uri selectedImage, Context context){
        return BitmapFactory.decodeFile(selectedImage.toString());
    }

    private String getKey(String imagePath) {
        return imagePath + SUFFIX_NOTE;
    }

    private void saveNote(String imageName, String note) {
        PropertyManager.getInstance().setProperty(
                getKey(imageName), note);
    }

    private String getNote(String imageName) {
        return PropertyManager.getInstance().getProperty(getKey(imageName), DEFAULT_TEXT);
    }
}
