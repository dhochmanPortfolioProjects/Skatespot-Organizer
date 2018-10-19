package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;

import java.io.File;

public class EditSpotActivity extends AppCompatActivity {

    private SpotViewModel mSpotViewModel;
    private Spot mEditSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_spot); // For now, use same layout as NewSpot

        // Get the ViewModel to access the underlying database
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // Edit mode:
//        if (getIntent().hasExtra("EDIT_SPOT")) {
//            inEditMode = true;

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
                    ((EditText) findViewById(R.id.new_spot_name)).setText(spot.getName());
                    ((EditText) findViewById(R.id.new_spot_latitude)).setText("" + spot.getLatitude());
                    ((EditText) findViewById(R.id.new_spot_longtitude)).setText("" + spot.getLongitude());
                    ((EditText) findViewById(R.id.new_spot_description)).setText(spot.getDescription());
                    // Now, instead of having to open a File based on the short pathname (generated
                    // dynamically by each Spot) just to call getPath() on it so that
                    // PictureUtils.getScaledBitmap() can open it and convert it to a Bitmap, since
                    // each Spot now holds the full path for each of its photos, so we can use that
                    // directly when we call PictureUtils.getScaledBitmap().

//                    File photoFile = new File(getFilesDir(), spot.getPhotoFilepath(1)); // Create new File in the directory
//                    Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//                    Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                    Bitmap bitmap = PictureUtils.getScaledBitmap(spot.getPhotoFilepath(1), 1000, 1000);
                    ((ImageView) findViewById(R.id.new_spot_photo_iv)).setImageBitmap(bitmap);
                }
            }
        });
        Button saveChanges_Button = findViewById(R.id.new_spot_create_btn);
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
                mEditSpot.setName(((EditText) findViewById(R.id.new_spot_name)).getText().toString());
                mEditSpot.setLongitude(Float.parseFloat(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString()));
                mEditSpot.setLongitude(Float.parseFloat(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString()));
                mEditSpot.setDescription(((EditText) findViewById(R.id.new_spot_description)).getText().toString());
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

        // Create Button Dynamically
        Button deleteSpot_Button = new Button(this);
        deleteSpot_Button.setText("Delete spot"/*R.string.show_text*/);
        deleteSpot_Button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            deleteSpot_Button.setGravity(Gravity.CENTER); This didn't work
//            deleteSpot_Button.layout_mar(0, 20, 0, 0);
        deleteSpot_Button.setBackgroundColor(Color.RED);
        deleteSpot_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    File fileToDelete = new File(getFilesDir(), mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()));
//                        if (deleteFile(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()))) {
//                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
//                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
                    if (fileToDelete.delete()) {
//                            Toast.makeText(NewSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has been deleted.", Toast.LENGTH_LONG).show();
                        Toast.makeText(EditSpotActivity.this, mEditSpot.getName() + " has been deleted.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has not been deleted.", Toast.LENGTH_LONG).show();
                    }
//                        Files.delete(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
                    mEditSpot.decrementPhotoCount();
                }
                mSpotViewModel.deleteSpots(mEditSpot);
                finish();
            }
        });

        LinearLayout root_container = findViewById(R.id.new_spot_root_container);
        // Add Button to LinearLayout
        if (root_container != null) {
            root_container.addView(deleteSpot_Button);
        }
    }
}
