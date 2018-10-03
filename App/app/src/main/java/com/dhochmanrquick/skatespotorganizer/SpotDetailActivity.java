package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;

import java.io.File;
import java.util.List;

public class SpotDetailActivity extends AppCompatActivity {

    private SpotViewModel mSpotViewModel;
//    private LiveData<Spot> spot; // Since the Spot is only accessed and manipulated in onChanged,
    // do we need this member variable?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        // Retrieve intent extra
        int id = getIntent().getIntExtra("com.dhochmanrquick.skatespotorganizer", 0);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        // Set Observer on the LiveData wrapping the current Spot
        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final?
                ((TextView) findViewById(R.id.spot_detail_title_tv)).setText("Name: " + spot.getName()
                + "\nId: " + spot.getId()
                + "\nDescription: " + spot.getDescription());
                File filesDir = getFilesDir(); // Get handle to directory for private application files
                File photoFile = new File(filesDir, spot.getPhotoFilename()); // Create new File in the directory
                Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                ((ImageView) findViewById(R.id.spot_detail_image_iv)).setImageBitmap(bitmap);
                }
        });

//        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
//            @Override
//            public void onChanged(@Nullable final List<Spot> words) {
//                // Update the cached copy of the words in the adapter.
//                adapter.setWords(words);
//            }
//        });
//
//        Toast.makeText(this, "Spot ID number " + spot.getName() + " has been selected", Toast.LENGTH_SHORT)
//                .show();

//        ((TextView) findViewById(R.id.spot_detail_title_tv)).setText("Spot ID number " + id);
    }
}
