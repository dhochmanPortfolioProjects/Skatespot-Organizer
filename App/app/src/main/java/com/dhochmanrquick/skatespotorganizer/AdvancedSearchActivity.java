package com.dhochmanrquick.skatespotorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class AdvancedSearchActivity extends AppCompatActivity {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    //    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private AutoCompleteTextView mAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_search);

        mAutoCompleteTextView = findViewById(R.id.advanced_search_AutoCompleteTextView);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);
        mAutoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
    }
}
