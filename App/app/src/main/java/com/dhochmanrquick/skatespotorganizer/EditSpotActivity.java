package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;

import java.io.File;
import java.util.List;

public class EditSpotActivity extends AppCompatActivity {

    // The key used in onSaveInstanceState to write the active dot to the bundle
    private static final String KEY_ACTIVE_DOT = "active_dot";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;

    private SpotViewModel mSpotViewModel;
    private Spot mEditSpot;
    private File mPhotoFile;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mPhotoIndexToDisplay = 0; // Keeps track of which photo and dot is currently active and should be displayed
    private LinearLayout mDotSlider_LinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_spot);

        // Get the previous active dot (if this Activity is being recreated)
        if (savedInstanceState != null) {
//            mActiveDot = savedInstanceState.getInt(KEY_ACTIVE_DOT, 0);
            mPhotoIndexToDisplay = savedInstanceState.getInt(KEY_ACTIVE_DOT, 0);
        }

        // Get the ViewModel to access the underlying database
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        mDotSlider_LinearLayout = findViewById(R.id.SliderDots);

        // The Activity that launches this one sends the spotID in an extra. Get the spotID for the
        // spot that we are editing.
        int spotID = getIntent().getIntExtra("EDIT_SPOT", 0);
        // Get Spot from database
        mSpotViewModel.getSpot(spotID).observe(this, new android.arch.lifecycle.Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) {
                if (spot != null) {
                    mEditSpot = spot; // Cache spot locally
                    // Populate all the views in the layout
                    ((EditText) findViewById(R.id.edit_spot_name)).setText(spot.getName());
                    ((EditText) findViewById(R.id.edit_spot_latitude)).setText("" + spot.getLatitude());
                    ((EditText) findViewById(R.id.edit_spot_longtitude)).setText("" + spot.getLongitude());
                    ((EditText) findViewById(R.id.edit_spot_description)).setText(spot.getDescription());
                    // Now, instead of having to open a File based on the short pathname (generated
                    // dynamically by each Spot) just to call getPath() on it so that
                    // PictureUtils.getScaledBitmap() can open it and convert it to a Bitmap, since
                    // each Spot now holds the full path for each of its photos, so we can use that
                    // directly when we call PictureUtils.getScaledBitmap().

//                    File photoFile = new File(getFilesDir(), spot.getPhotoFilepath(1)); // Create new File in the directory
//                    Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                    Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                    mDotSlider_LinearLayout.removeAllViews(); // Clear any previous dots from the slider
                    ViewPager viewPager = findViewById(R.id.edit_spot_image_viewpager);
//                    if (spot.getPhotoCount() > 0) {
                    viewPager.setBackgroundResource(0); // Clear any previously set background resource
                    // Load spot_images ArrayList with Spot's photo file paths
//                        ArrayList<String> spotImages_List = new ArrayList<>();
//                        int photoCount = spot.getPhotoCount();
//                        for (int i = 1; i <= photoCount; i++) {
//                            spotImages_List.add(spot.getPhotoFilepath(i));
//                        }
                    final SpotPhotoViewPagerAdapter viewPagerAdapter;
                    // Instantiate new SpotPhotoViewPagerAdapter (which knows how to build the View for each
                    // photo associated with this Spot) with spotImages_List.
//                        SpotPhotoViewPagerAdapter viewPagerAdapter = new SpotPhotoViewPagerAdapter(EditSpotActivity.this, spotImages_List);
                    if (mEditSpot.getPhotoCount() == 0) {
//                        ImageView item_ImageView = new ImageView(EditSpotActivity.this);
//                        item_ImageView.setImageResource(R.drawable.ic_no_image);
                        viewPagerAdapter = new SpotPhotoViewPagerAdapter(
                                EditSpotActivity.this, true);
                    } else { // Spot has at least one photo
                        viewPagerAdapter = new SpotPhotoViewPagerAdapter(
                                EditSpotActivity.this, mEditSpot, mSpotViewModel);
                        if (mEditSpot.getPhotoCount() > 1) { // Set up dot slider
                            final int dotsCount = viewPagerAdapter.getCount();
                            final ImageView[] dotImages_Array = new ImageView[dotsCount];

                            // Dynamically create dot ImageViews for each item (spot photo) in the
                            // adapter and add to SliderDotsPanel View
                            for (int i = 0; i < dotsCount; i++) {
                                dotImages_Array[i] = new ImageView(EditSpotActivity.this);
                                dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(8, 0, 8, 0);
                                mDotSlider_LinearLayout.addView(dotImages_Array[i], params);
                            }

                            dotImages_Array[mPhotoIndexToDisplay].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

//                            if (mPhotoIndexToDisplay == -1) {
//                                dotImages_Array[mActiveDot].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
//                            } else {
//                                dotImages_Array[mPhotoIndexToDisplay].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
//                            }

                            viewPager.removeOnPageChangeListener(mOnPageChangeListener);
                            mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                }

                                @Override
                                public void onPageSelected(int position) {
                                    // Deactivate previously active dot
                                    dotImages_Array[mPhotoIndexToDisplay].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                                    mPhotoIndexToDisplay = position;
//                                    mActiveDot = position; // Update mActiveDot to the new dot
                                    // Instead of iterating through all the dots and deactivating them,
                                    // more efficient to just deactivate the previous dot and then
                                    // activate the new dot
//                                    for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
//                                        dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
//                                    }
                                    dotImages_Array[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                }
                            };
                        }
                    }
                    viewPager.setAdapter(viewPagerAdapter);
                    viewPager.setCurrentItem(mPhotoIndexToDisplay, true);

//                    if (mPhotoIndexToDisplay != -1) {
//                        viewPager.setCurrentItem(mPhotoIndexToDisplay, true);
//                        mPhotoIndexToDisplay = -1;
//                    }
                    viewPager.addOnPageChangeListener(mOnPageChangeListener);
                }
            }
        });

        Button saveChanges_Button = findViewById(R.id.edit_spot_create_btn);
        saveChanges_Button.setText("Save changes");
        saveChanges_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                spotName = ((EditText) findViewById(R.id.new_spot_name)).getText().toString();
//                latitude = Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString());
//                longitude = Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString());
//                description = ((EditText) findViewById(R.id.new_spot_description)).getText().toString();
//
//                LatLng latLng = new LatLng(Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString()),
//                        Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString()));


//                Spot mNewSpot = new Spot(((EditText) findViewById(R.id.new_spot_name)).getText().toString(),
//                        new LatLng(Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString()),
//                                Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString())),
//                        ((EditText) findViewById(R.id.new_spot_description)).getText().toString(),
//                        /*R.drawable.dangsan_spot_landscape*/ 0);
//
//                mSpotViewModel.insert(mNewSpot);

//                File mPhotoFile = PictureUtils.getPhotoFile(getApplication(), mNewSpot);
//
//                // Translate the local filepath stored in mPhotoFile into a Uri the camera app can see
//                Uri uri = FileProvider.getUriForFile(getApplicationContext(),
//                        "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);
//
//                captureImage_Intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//                // Query Package Manager for every activity cameraImage_Intent can resolve to
//                List<ResolveInfo> cameraActivity = getPackageManager().queryIntentActivities(captureImage_Intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//                // Grant write permission for this Uri to each activity
//                for (ResolveInfo activity : cameraActivity) {
//                    grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }
//
//                startActivityForResult(captureImage_Intent, REQUEST_PHOTO);
//
//                    mSpotViewModel.insert(mNewSpot);
                // Todo: Implement the update to database
                // Populate all the views in the layout
                mEditSpot.setName(((EditText) findViewById(R.id.edit_spot_name)).getText().toString());
                mEditSpot.setLongitude(Float.parseFloat(((EditText) findViewById(R.id.edit_spot_latitude)).getText().toString()));
                mEditSpot.setLongitude(Float.parseFloat(((EditText) findViewById(R.id.edit_spot_longtitude)).getText().toString()));
                mEditSpot.setDescription(((EditText) findViewById(R.id.edit_spot_description)).getText().toString());

                if (mPhotoFile != null) {
                    mEditSpot.setPhotoFilepath(mPhotoFile.getPath(), mEditSpot.getPhotoCount());
                }
                // Now, instead of having to open a File based on the short pathname (generated
                // dynamically by each Spot) just to call getPath() on it so that
                // PictureUtils.getScaledBitmap() can open it and convert it to a Bitmap, since
                // each Spot now holds the full path for each of its photos, so we can use that
                // directly when we call PictureUtils.getScaledBitmap().

//                    File photoFile = new File(getFilesDir(), spot.getPhotoFilepath(1)); // Create new File in the directory
//                    Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                    Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//                    Bitmap bitmap = PictureUtils.getScaledBitmap(spot.getPhotoFilepath(1), 1000, 1000);
//                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
                mSpotViewModel.updateSpots(mEditSpot);
                Toast.makeText(getBaseContext(), "Updating Spot information...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Spot")
                .setMessage("Are you sure you want to permanently delete this spot?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User has chosen to delete this Spot
                        // First, need to delete the photo files
                        while (mEditSpot.getPhotoCount() > 0) {
                            // Open the file

                            // Create a path where we will place our picture in the user's
                            // public download directory and delete the file.  If external
                            // storage is not currently mounted this will fail.
//                        File path = Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_DOWNLOADS);
//                        File file = new File(path, "DemoPicture.jpg");
//                        file.delete();
//                        File filesDir = getFilesDir();
//                        filesDir.
//                        deleteFile(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
                            File fileToDelete = new File(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
//                            File fileToDelete = new File(getFilesDir(), mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()));
//                        if (deleteFile(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()))) {
//                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
//                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
                            if (fileToDelete.delete()) {
//                            Toast.makeText(NewSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has been deleted.", Toast.LENGTH_LONG).show();
//                                Toast.makeText(EditSpotActivity.this, mEditSpot.getName() + " has been deleted.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EditSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has not been deleted.", Toast.LENGTH_LONG).show();
                            }
//                        Files.delete(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
                            mEditSpot.decrementPhotoCount();
                        }
                        if (mEditSpot.getPhotoCount() == 0) {
                            mSpotViewModel.deleteSpots(mEditSpot);
                            Toast.makeText(EditSpotActivity.this,
                                    mEditSpot.getName() + " has been deleted.",
                                    Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            Toast.makeText(EditSpotActivity.this,
                                    "An error has occurred while trying to delete Spot " + mEditSpot.getName() + ".",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

//        // Create Button Dynamically
//        Button deleteSpot_Button = new Button(this);
//        deleteSpot_Button.setText("Delete spot"/*R.string.show_text*/);
//        deleteSpot_Button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
////            deleteSpot_Button.setGravity(Gravity.CENTER); This didn't work
////            deleteSpot_Button.layout_mar(0, 20, 0, 0);
//        deleteSpot_Button.setBackgroundColor(Color.RED);
//        deleteSpot_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//            }
//        });
//        LinearLayout root_container = findViewById(R.id.edit_spot_root_container);
//        // Add delete Button to LinearLayout
//        if (root_container != null) {
//            root_container.addView(deleteSpot_Button);
//        }

        final String[] items = new String[]{"From camera", "From gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder imageSelection_builder = new AlertDialog.Builder(this);
        imageSelection_builder.setTitle("Add image");
        imageSelection_builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // Pick from camera
                mPhotoFile = new File(getFilesDir(), mEditSpot.generateNextPhotoFilename());
                if (item == 0) {

                    // Create camera/image capture implicit intent
                    final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Determine whether there is a camera app available
                    boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;

//                        mPhotoFile = new File(getFilesDir(), mEditSpot.generateNextPhotoFilename());

//                    File mPhotoFile = PictureUtils.getPhotoFile(getApplication(), mNewSpot);
//                File mPhotoFile = mTempFile;
                    // Translate the local filepath stored in mPhotoFile into a Uri the camera app can see
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                            "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);

                    // If you pass the extra parameter MediaStore.EXTRA_OUTPUT with the camera intent
                    // then camera activity will write the captured image to that path and it will not
                    // return the bitmap in the onActivityResult method.
                    captureImage_Intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // Query Package Manager for every activity cameraImage_Intent can resolve to
                    List<ResolveInfo> cameraActivity =
                            getPackageManager().queryIntentActivities(captureImage_Intent, PackageManager.MATCH_DEFAULT_ONLY);

                    // Grant write permission for this Uri to each activity
                    for (ResolveInfo activity : cameraActivity) {
                        grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(captureImage_Intent, PICK_FROM_CAMERA);
                } else { // Pick from file
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*"); // Set the primary MIME type
                    //intent.addCategory(Intent.CATEGORY_OPENABLE); // May want to add this

                    // Method signature: static Intent	createChooser(Intent target, CharSequence title, IntentSender sender)
                    // Convenience function for creating a ACTION_CHOOSER Intent.
                    // CharSequence title: You can specify the title that will appear in the activity chooser.
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                    }
                }
            }
        });
        final AlertDialog imageSelection_dialog = imageSelection_builder.create();

        findViewById(R.id.edit_spot_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditSpot.getPhotoCount() >= 5) {
                    Toast.makeText(EditSpotActivity.this,
                            "You may only save 5 photos for this spot",
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    imageSelection_dialog.show();
                }
//                if (mEditSpot.getPhotoCount() >= 5) {
//                    Toast.makeText(EditSpotActivity.this,
//                            "You may only save 5 photos for this spot",
//                            Toast.LENGTH_LONG)
//                            .show();
//                } else {
//                    // Create camera/image capture implicit intent
//                    final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    // Determine whether there is a camera app available
//                    boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;
//
//                    mPhotoFile = new File(getFilesDir(), mEditSpot.generateNextPhotoFilename());
//
////                    File mPhotoFile = PictureUtils.getPhotoFile(getApplication(), mNewSpot);
////                File mPhotoFile = mTempFile;
//                    // Translate the local filepath stored in mPhotoFile into a Uri the camera app can see
//                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),
//                            "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);
//
//                    // If you pass the extra parameter MediaStore.EXTRA_OUTPUT with the camera intent
//                    // then camera activity will write the captured image to that path and it will not
//                    // return the bitmap in the onActivityResult method.
//                    captureImage_Intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//                    // Query Package Manager for every activity cameraImage_Intent can resolve to
//                    List<ResolveInfo> cameraActivity =
//                            getPackageManager().queryIntentActivities(captureImage_Intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//                    // Grant write permission for this Uri to each activity
//                    for (ResolveInfo activity : cameraActivity) {
//                        grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    }
//                    startActivityForResult(captureImage_Intent, PICK_FROM_CAMERA);
//                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
        // if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        if (resultCode != RESULT_OK) return;
        File filesDir = getFilesDir(); // Get handle to directory for private application files
        File photoFile;
        Bitmap bitmap;

        switch (requestCode) {
//            case REQUEST_PHOTO:
            case PICK_FROM_CAMERA:
                mPhotoIndexToDisplay = mEditSpot.getPhotoCount();
                mEditSpot.incrementPhotoCount();
                mEditSpot.setPhotoFilepath(mPhotoFile.getPath(), mEditSpot.getPhotoCount());
                mSpotViewModel.updateSpots(mEditSpot);
                Toast.makeText(EditSpotActivity.this, "Your photo has been added to the spot.", Toast.LENGTH_LONG).show();
//                doCrop();
//                photoFile = new File(filesDir, mNewSpot.getPhotoFilename()); // Create new File in the directory
//                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                bitmap = PictureUtils.getScaledBitmap(mTempFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//                ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
                break;
            case PICK_FROM_FILE:
                Uri selectedImage_Uri = intent.getData(); // Get return contentURI
//                try {
//                    // Create Bitmap from the return contentURI
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage_Uri);
//                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                File selectedPhoto_File = new File(getFilesDir(), mImageCaptureUri.getPath()); // Create new File in the directory
                if (PictureUtils.copyUriContentToFile(getApplication(), selectedImage_Uri, mPhotoFile)) {
                    mPhotoIndexToDisplay = mEditSpot.getPhotoCount();
                    mEditSpot.incrementPhotoCount();
                    mEditSpot.setPhotoFilepath(mPhotoFile.getPath(), mEditSpot.getPhotoCount());
                    mSpotViewModel.updateSpots(mEditSpot);
                    Toast.makeText(EditSpotActivity.this, "The selected photo has been added to the spot.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditSpotActivity.this,
                            "An error has occurred while saving the selected photo.",
                            Toast.LENGTH_LONG).show();
                }
//                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
//                doCrop();
                break;
            case CROP_FROM_CAMERA:
//                Bundle extras = intent.getExtras();
//                if (extras != null) {
//                    Bitmap photo = extras.getParcelable("data");
////                    mImageView.setImageBitmap(photo);
//                }
//                File f = new File(mImageCaptureUri.getPath());
//                if (f.exists()) f.delete();
                break;
        }
//        switch (requestCode) {
//            case REQUEST_PHOTO:
//                if (resultCode == RESULT_OK && intent.hasExtra("data")) {
//                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
//                    if (bitmap != null) {
//                        ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
////                        ivThumbnailPhoto.setImageBitmap(bitmap);
//                    }
//                }
//        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK && intent.hasExtra("data")) {
////            Bundle extras = intent.getExtras();
//            Bitmap imageBitmap = (Bitmap) intent.getExtras().get("data");
//            ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(imageBitmap);
////                        ivThumbnailPhoto.setImageBitmap(bitmap);
////                    mImageView.setImageBitmap(imageBitmap);
////                break;
//        }


//                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(imageBitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(KEY_ACTIVE_DOT, mActiveDot);
        outState.putInt(KEY_ACTIVE_DOT, mPhotoIndexToDisplay);
    }
}

