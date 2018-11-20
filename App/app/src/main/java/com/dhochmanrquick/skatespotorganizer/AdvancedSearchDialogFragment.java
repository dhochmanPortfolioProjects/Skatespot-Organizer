package com.dhochmanrquick.skatespotorganizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class AdvancedSearchDialogFragment extends DialogFragment {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    //    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private AutoCompleteTextView mAutoCompleteTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogfragment_advanced_search, container, false);

        mAutoCompleteTextView = view.findViewById(R.id.advanced_search_AutoCompleteTextView);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext(), null); // TODO: I never know if I should use getContext(), getApplication(), etc.
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, LAT_LNG_BOUNDS, null);
        mAutoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);

        return view;
    }
}
