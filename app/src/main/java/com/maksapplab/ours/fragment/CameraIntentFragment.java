package com.maksapplab.ours.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.maksapplab.ours.R;
import com.maksapplab.ours.activity.MainActivity;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.ThumbnailUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.maksapplab.ours.constants.PhotoConstant.PREFIX_PHOTO;

public class CameraIntentFragment extends BaseFragment implements Button.OnClickListener {

    // Activity result key for camera
    protected static final int REQUEST_TAKE_PHOTO = 11111;
    protected String APP_NAME;

    // Image view for showing our image.
    private ImageView mImageView;

    private MainActivity activity;

    /**
     * Default empty constructor.
     */
    public CameraIntentFragment(){
        super();
    }

    private String getAppName() {
        return getResources().getString(R.string.app_name);
    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static CameraIntentFragment newInstance(int sectionNumber) {
        CameraIntentFragment fragment = new CameraIntentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APP_NAME = getAppName();
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
        View view = inflater.inflate(R.layout.fragment_camera_intent, container, false);

        // Set the image view
        mImageView = (ImageView)view.findViewById(R.id.imageViewFullSized);
        Button takePictureButton = (Button)view.findViewById(R.id.button);

        // Set OnItemClickListener so we can be notified on button clicks
        takePictureButton.setOnClickListener(this);

        activity = (MainActivity)getActivity();

        /*
         * Go straight to Camera mode, but it is disabled for now
         */
//        dispatchTakePictureIntent();

        return view;
    }

    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent() {

        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(
                        activity,
                        "There was a problem saving the photo...",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();

            // Show the full sized image.
            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mImageView);

            removeImage(getLastImageId(activity));
        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();

            cleanupAfterAbort();
        }
    }

    /**
     * Creates the image file to which the image must be saved.
     * @return
     * @throws java.io.IOException
     */
    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = PREFIX_PHOTO + timeStamp + ".jpg";
        File storageDir =
          new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES) + "/" + APP_NAME);

        if(!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = new File(storageDir, imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());

        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(activity.getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);

        ThumbnailUtil.createThumbnails(contentUri);
        PropertyManager.getInstance().saveDisplayDateByCreatedTime(f.getName());

//        SimplePhotoGalleryListFragment.newInstance(2).resetFragmentState();
    }

    /**
     * Deal with button clicks.
     * @param v
     */
    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * Gets the last image id from the media store
     * @return
     */
    private int getLastImageId(Context context) {
        final String[] imageColumns = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA};

        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = context.getContentResolver().query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                imageColumns,
                                null,
                                null,
                                imageOrderBy);

        while (imageCursor.moveToNext()) {
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath =
                    imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d("Camera", "getLastImageId::id " + id);
            Log.d("Camera", "getLastImageId::path " + fullPath);

            if(!fullPath.contains(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + APP_NAME)){
                imageCursor.close();
                return id;
            }
        }
        return 0;
    }

    private void removeImage(int id) {
        ContentResolver cr = getActivity().getContentResolver();
        cr.delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media._ID + "=?",
                new String[]{ Long.toString(id) } );
    }

    private void cleanupAfterAbort(){

        File file = new File(activity.getCurrentPhotoPath());
        if (file.exists())
            file.delete();
    }
}
