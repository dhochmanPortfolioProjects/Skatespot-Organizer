package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SpotDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private SpotViewModel mSpotViewModel;
    //    private LiveData<Spot> spot; // Since the Spot is only accessed and manipulated in onChanged,
    // do we need this member variable?
    private Spot mSpot;
    private GoogleMap mMap;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

//        ViewPager viewPager = findViewById(R.id.spot_image_viewpager);
//        ViewPagerAdapter adapter = new ViewPagerAdapter(this, )

        // Within your UI, a map will be represented by either a MapFragment or MapView object.
        MapView mapView = findViewById(R.id.spotmap);
        // When using the API in fully interactive mode, users of the MapView class must forward the
        // following activity lifecycle methods to the corresponding methods in the MapView class:
        // onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onSaveInstanceState(),
        // and onLowMemory().
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        // Use getMapAsync() to set the callback on the fragment.
        // Note: getMapAsync() must be called from the main thread, and the callback will be executed
        // in the main thread. If Google Play services is not installed on the user's device, the
        // callback will not be triggered until the user installs Play services.
        mapView.getMapAsync(this);

        // Retrieve intent extra
        int id = getIntent().getIntExtra("com.dhochmanrquick.skatespotorganizer", 0);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        // Set Observer on the LiveData wrapping the current Spot
        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final? This is the Spot
                if (spot != null) {
                    mSpot = spot;

                    ViewPager viewPager = findViewById(R.id.spot_image_viewpager);

                    // Load spot_images ArrayList with Spot's photo file paths
                    ArrayList<String> spotImages_List = new ArrayList<>();
                    int photoCount = spot.getPhotoCount();
                    for (int i = 1; i <= photoCount; i++) {
                        spotImages_List.add(spot.getPhotoFilepath(i));
                    }

                    // Instantiate new ViewPagerAdapter (which knows how to build the View for each
                    // photo associated with this Spot) with spotImages_List.
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SpotDetailActivity.this, spotImages_List);

                    LinearLayout sliderDotsPanel = findViewById(R.id.SliderDots);
                    final int dotsCount = viewPagerAdapter.getCount();
                    final ImageView[] dotImages_Array = new ImageView[dotsCount];

                    viewPager.setAdapter(viewPagerAdapter);

                    // Create dot ImageViews and add to SliderDotsPanel View
                    sliderDotsPanel.removeAllViews();
                    for (int i = 0; i < dotsCount; i++) {
                        dotImages_Array[i] = new ImageView(SpotDetailActivity.this);
                        dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0, 8, 0);
                        sliderDotsPanel.addView(dotImages_Array[i], params);
                    }

                    dotImages_Array[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                    viewPager.removeOnPageChangeListener(mOnPageChangeListener);
                    mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {
//                            int dotsCount_local = dotsCount;
//                            ImageView[] dotImages_Array_local = new ImageView[dotsCount_local];
                            for (int i = 0; i < dotsCount; i++) {
                                dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                            }
                            dotImages_Array[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    };
                    viewPager.addOnPageChangeListener(mOnPageChangeListener);

                    // Populate UI Views with this Spot's information
                    ((TextView) findViewById(R.id.spot_detail_title_tv)).setText(spot.getName());
                    ((TextView) findViewById(R.id.spot_detail_description_tv)).setText(spot.getDescription());
//                File filesDir = getFilesDir(); // Get handle to directory for private application files
//                File photoFile = new File(filesDir, spot.getPhotoFilepath(1)); // Create new File in the directory


//                    final Bitmap bitmap = PictureUtils.getScaledBitmap(spot.getPhotoFilepath(1), 1000, 1000);
////            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//                    final ImageView spot_ImageView = (ImageView) findViewById(R.id.spot_detail_image_iv);
//                    spot_ImageView.setImageBitmap(bitmap);
//
//
//                    spot_ImageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Dialog spotImage_Dialog = new Dialog(SpotDetailActivity.this);
////                            spotImage_Dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                            ImageView imageView = new ImageView(SpotDetailActivity.this);
//                            imageView.setImageBitmap(bitmap);
//                            spotImage_Dialog.getWindow().setContentView(imageView);
////                            settingsDialog.getWindow().setContentView(spot_ImageView); // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
////                            settingsDialog.getWindow().setBackgroundDrawableResource(R.drawable.paju_spot_landscape);
////                            settingsDialog.setContentView(getLayoutInflater().inflate(spot_ImageView/*R.layout.image_layout*/, null));
//                            spotImage_Dialog.setCancelable(true);
//                            spotImage_Dialog.show();
//                        }
//                    });


                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mSpot.getLatLng().latitude, mSpot.getLatLng().longitude)));
//                        .title(mSpot.getName())
//                        .snippet(mSpot.getDescription()));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mSpot.getLatLng()));
                } else {
                    finish();
                }
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

        // Setup FAB to open EditSpotActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.spot_edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpotDetailActivity.this, EditSpotActivity.class);
                intent.putExtra("EDIT_SPOT", mSpot.getId());
                startActivity(intent);
//                mCurrentPlayer.createCryptogram(PlayableGamesActivity.this);
//                Intent intent = new Intent(PlayableGamesActivity.this, CreateGameActivity.class);
//                startActivity(intent);
            }
        });
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        Intent spotStreetView_Intent = new Intent(this, SpotStreetViewActivity.class);
//        Intent spot_Intent = new Intent(getContext(), NewSpotActivity.class);
//        spot_Intent.setData(marker.getTag());

        spotStreetView_Intent.putExtra("Spot", mSpot.getLatLng());

        startActivity(spotStreetView_Intent);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Triggers a null reference as is; mSpot hasn't been properly assigned yet.
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(mSpot.getLatLng().latitude, mSpot.getLatLng().longitude))
//                .title(mSpot.getName())
//                .snippet(mSpot.getDescription()));
    }
}
