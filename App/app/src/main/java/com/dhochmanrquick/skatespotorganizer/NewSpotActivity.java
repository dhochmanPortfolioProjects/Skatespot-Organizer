package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observer;

public class NewSpotActivity extends AppCompatActivity {

    private Spot mNewSpot;
    private SpotViewModel mSpotViewModel;
    //    private static final int REQUEST_PHOTO = 2;
    private static final String EXTRA_CURRENT_LOCATION = "com.dhochmanrquick.skatespotorganizer.current_location";
    private boolean inEditMode = false;
    private boolean inCurrentLocationMode = false;

    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_spot);

        // Configure image source selection AlertDialog
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // Pick from camera
                if (item == 0) {
                    // Create camera/image capture implicit intent
                    final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Determine whether there is a camera app available
                    boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;
                    // Create new Spot and populate its fields with text from the EditTexts
                    // Todo: Add data validation
                    mNewSpot = new Spot(((EditText) findViewById(R.id.new_spot_name)).getText().toString(),
                            new LatLng(Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString()),
                                    Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString())),
                            ((EditText) findViewById(R.id.new_spot_description)).getText().toString(),
                            /*R.drawable.dangsan_spot_landscape*/ 0);

                    File mPhotoFile = PictureUtils.getPhotoFile(getApplication(), mNewSpot);

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
//                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
//                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
//
//                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//
//                    try {
//                        intent.putExtra("return-data", true);
//
//                        startActivityForResult(intent, PICK_FROM_CAMERA);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
                } else { // Pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });
        final AlertDialog dialog = builder.create();


        // Get the ViewModel to access the underlying database
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // This Activity should have 3 modes: New and Edit
        // Edit mode
        if (getIntent().hasExtra("EDIT_SPOT")) {
            inEditMode = true;
            int spotID = getIntent().getIntExtra("EDIT_SPOT", 0);
            mSpotViewModel.getSpot(spotID).observe(this, new android.arch.lifecycle.Observer<Spot>() {
                @Override
                public void onChanged(@Nullable Spot spot) {
                    // Populate all the views in the layout
                    ((EditText) findViewById(R.id.new_spot_name)).setText(spot.getName());
                    ((EditText) findViewById(R.id.new_spot_latitude)).setText("" + spot.getLatitude());
                    ((EditText) findViewById(R.id.new_spot_longtitude)).setText("" + spot.getLongitude());
                    ((EditText) findViewById(R.id.new_spot_description)).setText(spot.getDescription());
                    File filesDir = getFilesDir(); // Get handle to directory for private application files
                    File photoFile = new File(filesDir, spot.getPhotoFilename()); // Create new File in the directory
                    Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
                }
            });

            Button saveChanges_Button = ((Button) findViewById(R.id.new_spot_create_btn));
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
                    Toast.makeText(getBaseContext(), "Implement database update...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        // Current Location Mode
//        else if (getIntent().hasExtra("EXTRA_CURRENT_LOCATION")) { For some reason, this returns null even if there is an extra
        else {
            inCurrentLocationMode = true;
            Location location = getIntent().getParcelableExtra(EXTRA_CURRENT_LOCATION);
            if (location != null) {
                ((EditText) findViewById(R.id.new_spot_latitude)).setText("" + location.getLatitude());
                ((EditText) findViewById(R.id.new_spot_longtitude)).setText("" + location.getLongitude());
            }
            Button saveChanges_Button = findViewById(R.id.new_spot_create_btn);
            saveChanges_Button.setText("Create new spot");
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
                    mSpotViewModel.insert(mNewSpot);
                    finish();
                }
            });
        }

        findViewById(R.id.new_spot_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                // Create camera/image capture implicit intent
//                final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


//                Intent intent = new Intent();
//                // Show only images, no videos or anything else
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                // Always show the chooser (if there are multiple options available)
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

//                final String [] items			= new String [] {"Take from camera", "Select from gallery"};
//                ArrayAdapter<String> adapter	= new ArrayAdapter<String> (getBaseContext()/*this*/, android.R.layout.select_dialog_item, items);
//                AlertDialog.Builder builder		= new AlertDialog.Builder(getBaseContext()/*this*/);

//                builder.setTitle("Select Image").setMessage("Select image from:").setPositiveButton("Camera")
//                builder.setMessage();

//                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) { //pick from camera
//                        if (item == 0) {
//                            Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
//                                    "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
//
//                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//
//                            try {
//                                intent.putExtra("return-data", true);
//
//                                startActivityForResult(intent, PICK_FROM_CAMERA);
//                            } catch (ActivityNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        } else { //pick from file
//                            Intent intent = new Intent();
//
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
//                        }
//                    }
//                } );


//
//                // Determine whether there is a camera app available
//                boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;
//                // Create new Spot and populate its fields with text from the EditTexts
//                // Todo: Add data validation
//                mNewSpot = new Spot(((EditText) findViewById(R.id.new_spot_name)).getText().toString(),
//                        new LatLng(Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString()),
//                                Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString())),
//                        ((EditText) findViewById(R.id.new_spot_description)).getText().toString(),
//                        /*R.drawable.dangsan_spot_landscape*/ 0);
//
//                File mPhotoFile = PictureUtils.getPhotoFile(getApplication(), mNewSpot);
//
//                // Translate the local filepath stored in mPhotoFile into a Uri the camera app can see
//                Uri uri = FileProvider.getUriForFile(getApplicationContext(),
//                        "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);
//
//                // If you pass the extra parameter MediaStore.EXTRA_OUTPUT with the camera intent
//                // then camera activity will write the captured image to that path and it will not
//                // return the bitmap in the onActivityResult method.
//                captureImage_Intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//                // Query Package Manager for every activity cameraImage_Intent can resolve to
//                List<ResolveInfo> cameraActivity =
//                        getPackageManager().queryIntentActivities(captureImage_Intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//                // Grant write permission for this Uri to each activity
//                for (ResolveInfo activity : cameraActivity) {
//                    grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }
//
//                startActivityForResult(captureImage_Intent, REQUEST_PHOTO);


//                mSpotViewModel.insert(mNewSpot);

//                finish(); //
            }
        });
    }

    /**
     * The Android Camera application encodes the photo in the return Intent delivered to
     * onActivityResult() as a small Bitmap in the extras, under the key "data".
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
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
//                doCrop();
                photoFile = new File(filesDir, mNewSpot.getPhotoFilename()); // Create new File in the directory
                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
                break;
            case PICK_FROM_FILE:
                mImageCaptureUri = intent.getData();
//                mImageCaptureUri.get
//                MediaStore.Images.Media.
                try {
                    // Create Bitmap from the return contentURI
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);

                    ImageView imageView = findViewById(R.id.new_spot_photo_iv);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                filesDir = getFilesDir(); // Get handle to directory for private application files
//                photoFile = new File(filesDir, mImageCaptureUri.getPath()); // Create new File in the directory
//                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
//                doCrop();
                break;

            case CROP_FROM_CAMERA:
                Bundle extras = intent.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

//                    mImageView.setImageBitmap(photo);
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

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
}
