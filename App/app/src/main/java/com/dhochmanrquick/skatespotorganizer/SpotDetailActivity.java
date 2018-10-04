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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.List;

public class SpotDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private SpotViewModel mSpotViewModel;
//    private LiveData<Spot> spot; // Since the Spot is only accessed and manipulated in onChanged,
    // do we need this member variable?
    private Spot mSpot;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        // Within your UI, a map will be represented by either a MapFragment or MapView object.
        MapView mapView = findViewById(R.id.spotmap);
        // Use getMapAsync() to set the callback on the fragment.
        // Note: getMapAsync() must be called from the main thread, and the callback will be executed
        // in the main thread. If Google Play services is not installed on the user's device, the
        // callback will not be triggered until the user installs Play services.
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        // Retrieve intent extra
        int id = getIntent().getIntExtra("com.dhochmanrquick.skatespotorganizer", 0);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        // Set Observer on the LiveData wrapping the current Spot
        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final? This is the Spot
                // pulled from the database.
                mSpot = spot;
                ((TextView) findViewById(R.id.spot_detail_title_tv)).setText(spot.getName());
                ((TextView) findViewById(R.id.spot_detail_description_tv)).setText(spot.getDescription());
                File filesDir = getFilesDir(); // Get handle to directory for private application files
                File photoFile = new File(filesDir, spot.getPhotoFilename()); // Create new File in the directory
                Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
                ((ImageView) findViewById(R.id.spot_detail_image_iv)).setImageBitmap(bitmap);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mSpot.getLatLng().latitude, mSpot.getLatLng().longitude))
                        .title(mSpot.getName())
                        .snippet(mSpot.getDescription()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mSpot.getLatLng()));
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

    /**
     * Use the onMapReady(GoogleMap) callback method to get a handle to the GoogleMap object.
     * The callback is triggered when the map is ready to be used. It provides a non-null instance
     * of GoogleMap. You can use the GoogleMap object to set the view options for the map or add a
     * marker, for example. The key class when working with a map object is the GoogleMap class.
     * GoogleMap models the map object within your application. Within your UI, a map will be
     * represented by either a MapFragment or MapView object.
     *
     * @param googleMap a non-null instance of the GoogleMap object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Triggers a null reference as is; mSpot hasn't been properly assigned yet.
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(mSpot.getLatLng().latitude, mSpot.getLatLng().longitude))
//                .title(mSpot.getName())
//                .snippet(mSpot.getDescription()));
    }
}
