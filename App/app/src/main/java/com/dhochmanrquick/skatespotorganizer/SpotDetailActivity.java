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

/**
 * The UI controller for the Spot Detail view. This class is responsible for displaying the
 * detailed spot information to the user. The Spot Detail view consists of:
 * <p>
 * 1). A photo carousel (to display the photos of the Spot), including a dot slider to show the
 * user their location within the carousel.
 * 2). The Spot title
 * 3). The Spot description
 * 4). A map showing the Spot's location
 * 5). An edit Spot FAB
 */
public class SpotDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    // The key used in onSaveInstanceState to write the active dot to the bundle
    private static final String KEY_ACTIVE_DOT = "active_dot";

    private SpotViewModel mSpotViewModel;
    private Spot mSpot;
    private GoogleMap mMap;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private LinearLayout mDotSlider_LinearLayout;
    private int mActiveDot; // The current active dot in the dot slider

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        // Get the previous active dot (if this Activity is being recreated)
        if (savedInstanceState != null) {
            mActiveDot = savedInstanceState.getInt(KEY_ACTIVE_DOT, 0);
        }

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

        mDotSlider_LinearLayout = findViewById(R.id.SliderDots);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        // Get the Spot to display from the ViewModel and set an Observer on the LiveData ( which
        // wraps the current Spot)
        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final? This is the Spot
                if (spot != null) {
                    mSpot = spot; // Cache the Spot locally to access it outside of this Observer
                    mDotSlider_LinearLayout.removeAllViews(); // Clear any previous dots from the slider
                    ViewPager spotImage_ViewPager = findViewById(R.id.spot_image_viewpager);
                    final SpotPhotoViewPagerAdapter spotPhotoViewPagerAdapter; // Declared final for use in OnPageChangeListener
                    // Build an appropriate SpotPhotoViewPagerAdapter according to whether
                    // the Spot has 0, 1, or 1+ photos; the SpotPhotoViewPagerAdapter knows how to
                    // build appropriate views in all three cases.
                    if (spot.getPhotoCount() == 0) {
                        spotPhotoViewPagerAdapter = new SpotPhotoViewPagerAdapter(
                                SpotDetailActivity.this, true);
                    } else { // Spot has at least one photo
                        spotPhotoViewPagerAdapter = new SpotPhotoViewPagerAdapter(
                                SpotDetailActivity.this, spot, mSpotViewModel);
                        if (spot.getPhotoCount() > 1) { // Only set up dot slider for 1+ photos
                            final int dotsCount = spotPhotoViewPagerAdapter.getCount();
                            final ImageView[] dotImages_Array = new ImageView[dotsCount]; // Declared final for use in OnPageChangeListener
                            // Dynamically create "non active" dot ImageViews for each item
                            // (spot photo) in the adapter and add to mDotSlider_LinearLayout
                            for (int i = 0; i < dotsCount; i++) {
                                dotImages_Array[i] = new ImageView(SpotDetailActivity.this); // Create new dot and add to array
                                // Set dot to non active
                                dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                                // Set layout params
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(8, 0, 8, 0);
                                // Although each dot View is added to the mDotSlider_LinearLayout,
                                // its visual appearance is still manipulated by taking from
                                // dotImages_Array[] and calling methods on it.
                                mDotSlider_LinearLayout.addView(dotImages_Array[i], params);
                            }
                            // Set the active dot; mActiveDot is initially 0 but may be another value
                            // if the Activity is being recreated.
                            dotImages_Array[mActiveDot].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                            spotImage_ViewPager.removeOnPageChangeListener(mOnPageChangeListener);
                            mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
                                // The following methods must be overridden
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                }

                                @Override
                                public void onPageSelected(int position) {
                                    // Deactivate previously active dot
                                    dotImages_Array[mActiveDot].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                                    mActiveDot = position; // Update mActiveDot to the new dot
                                    // Instead of iterating through all the dots and deactivating them,
                                    // more efficient to just deactivate the previous dot and then
                                    // activate the new dot
//                                    for (int i = 0; i < spotPhotoViewPagerAdapter.getCount(); i++) {
//                                        dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
//                                    }
                                    // Set active dot
                                    dotImages_Array[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                }
                            };
                        }
                    }
                    spotImage_ViewPager.setAdapter(spotPhotoViewPagerAdapter);
                    spotImage_ViewPager.addOnPageChangeListener(mOnPageChangeListener);

                    // Populate UI Views with this Spot's information
                    ((TextView) findViewById(R.id.spot_detail_title_tv)).setText(spot.getName());
                    ((TextView) findViewById(R.id.spot_detail_description_tv)).setText(spot.getDescription());

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

        // Setup FAB to open EditSpotActivity
        FloatingActionButton fab = findViewById(R.id.spot_edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpotDetailActivity.this, EditSpotActivity.class);
                intent.putExtra("EDIT_SPOT", mSpot.getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent spotStreetView_Intent = new Intent(this, SpotStreetViewActivity.class);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVE_DOT, mActiveDot);
    }
}
