package com.dhochmanrquick.skatespotorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

public class SpotStreetViewActivity extends AppCompatActivity implements
        OnStreetViewPanoramaReadyCallback {

    private LatLng mSpotLatLng;
    private StreetViewPanoramaView mStreetViewPanoramaView;
    private static final String STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_street_view);

        mSpotLatLng = getIntent().getParcelableExtra("Spot");

//        StreetViewPanoramaFragment streetViewPanoramaFragment =
//                (StreetViewPanoramaFragment) getFragmentManager()
//                        .findFragmentById(R.id.streetviewpanorama);
//        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

//        StreetViewPanoramaView streetViewPanoramaView = findViewById(R.id.streetviewpanorama);
//        streetViewPanoramaView.getStreetViewPanoramaAsync(this);

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        options.position(mSpotLatLng, 100);
//        options.position(new LatLng(37.707876, 126.746319));
//        if (savedInstanceState == null) {
//            if (Spot_id == 0) { // Bulgwang
//                options.position(new LatLng(37.615945, 126.924795));
//            } else { // Paju spot
//                options.position(new LatLng(37.707876, 126.746319));
////
////                StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
////                .bearing(options.getStreetViewPanoramaCamera().bearing + 30).build();
//
//            }
//        }
        mStreetViewPanoramaView = new StreetViewPanoramaView(this, options);

//        int PAN_BY = 30;
//        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
//                .zoom(mStreetViewPanoramaView.get.getPanoramaCamera().zoom)
//                .tilt(mStreetViewPanorama.getPanoramaCamera().tilt)
//                .bearing(mStreetViewPanorama.getPanoramaCamera().bearing - PAN_BY)
//                .build();

        addContentView(mStreetViewPanoramaView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // *** IMPORTANT ***
        // StreetViewPanoramaView requires that the Bundle you pass contain _ONLY_
        // StreetViewPanoramaView SDK objects or sub-Bundles.
        Bundle mStreetViewBundle = null;
        if (savedInstanceState != null) {
            mStreetViewBundle = savedInstanceState.getBundle(STREETVIEW_BUNDLE_KEY);
        }
        mStreetViewPanoramaView.onCreate(mStreetViewBundle);
    }

    /**
     * Use the onStreetViewPanoramaReady(StreetViewPanorama) callback method to to retrieve a
     * non-null instance of (handle to the) StreetViewPanorama, ready to be used.
     *
     * @param streetViewPanorama
     */
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
//        streetViewPanorama.setPosition(new LatLng(-33.87365, 151.20689));
//        streetViewPanorama.setPosition(mSpotLatLng);
        if (streetViewPanorama != null /*&& streetViewPanorama. != null*/) {
            // location is present
            streetViewPanorama.setPosition(mSpotLatLng);

        } else {
            // location not available
            Toast.makeText(this, "Street View not available for this spot", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        mStreetViewPanoramaView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mStreetViewPanoramaView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mStreetViewPanoramaView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mStreetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY);
        if (mStreetViewBundle == null) {
            mStreetViewBundle = new Bundle();
            outState.putBundle(STREETVIEW_BUNDLE_KEY, mStreetViewBundle);
        }

        mStreetViewPanoramaView.onSaveInstanceState(mStreetViewBundle);
    }
}
