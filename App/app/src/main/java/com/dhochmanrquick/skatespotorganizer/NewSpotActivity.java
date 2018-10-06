package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.List;

public class NewSpotActivity extends AppCompatActivity {

//    String spotName;
//    double latitude;
//    double longitude;
//    String description;

    private Spot mNewSpot;
    private SpotViewModel mSpotViewModel;
    private static final int REQUEST_PHOTO = 2;
    private static final String EXTRA_CURRENT_LOCATION = "com.dhochmanrquick.skatespotorganizer.current_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_spot);

        // Get the ViewModel to access the underlying database
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        Location location = getIntent().getParcelableExtra(EXTRA_CURRENT_LOCATION);

        if (location != null) {
            ((EditText) findViewById(R.id.new_spot_latitude)).setText("" + location.getLatitude());
            ((EditText) findViewById(R.id.new_spot_longtitude)).setText("" + location.getLongitude());
        }

        // Create camera/image capture implicit intent
        final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Determine whether there is a camera app available
        boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;

        ImageButton takePhoto = findViewById(R.id.new_spot_camera);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                startActivityForResult(captureImage_Intent, REQUEST_PHOTO);

//                mSpotViewModel.insert(mNewSpot);

//                finish(); //
            }
        });

        ((Button) findViewById(R.id.new_spot_create_btn)).setOnClickListener(new View.OnClickListener() {
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
                mSpotViewModel.insert(mNewSpot);
//
                finish();
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
        File filesDir = getFilesDir(); // Get handle to directory for private application files
        File photoFile = new File(filesDir, mNewSpot.getPhotoFilename()); // Create new File in the directory
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
        ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);


//                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(imageBitmap);

    }
}
