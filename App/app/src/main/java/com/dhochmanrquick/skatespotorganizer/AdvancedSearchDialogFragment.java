package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class AdvancedSearchDialogFragment extends DialogFragment {

    // Constants
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    // Widgets
    private AutoCompleteTextView mAutoCompleteTextView;
    private TextView mRadius_TextView;

    // Vars
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    //    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private Place mPlace;
    private LatLng mLatLng;
    private int mRadius = 0;
    private SpotViewModel mSpotViewModel;
    private List<Spot> mSpots;

    public interface OnAdvancedSearchResult{
        void sendAdvancedSearchResult(LatLng searchResult, int radius);
    }

    public OnAdvancedSearchResult mOnAdvancedSearchResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogfragment_advanced_search, container, false);

        mAutoCompleteTextView = view.findViewById(R.id.advanced_search_AutoCompleteTextView);
        mRadius_TextView = view.findViewById(R.id.advanced_search_radius_tv);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext(), null); // TODO: I never know if I should use getContext(), getApplication(), etc.
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSpotViewModel = ViewModelProviders.of(getActivity()).get(SpotViewModel.class);
        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable List<Spot> spots) {
                mPlaceAutocompleteAdapter.setSpots(spots);
            }
        });

        mAutoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);

        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the clicked item from the adapter
                final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);

                if (item instanceof Spot) {
                    mLatLng = ((Spot) item).getLatLng();
                } else {
                    final String placeId = item.getPlaceId();

                    // Submit request
                    mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful()) {
                                PlaceBufferResponse places = task.getResult();
//                            Place myPlace = places.get(0);
                                mPlace = places.get(0);
                                mLatLng = mPlace.getLatLng();
                                places.release();
                            } else {
//                            Log.e(TAG, "Place not found.");
                            }
                        }
                    });
                }
//                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                        .getPlaceById(mGeoDataClient, placeId);
//                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                    @Override
//                    public void onResult(@NonNull PlaceBuffer places) {
//
//                    }
//                });
//                Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.dialogfragment_advanced_search_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAdvancedSearchResult.sendAdvancedSearchResult(mLatLng, mRadius);
                getDialog().dismiss();
            }
        });

        ((SeekBar) view.findViewById(R.id.advanced_search_radius_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius_TextView.setText(String.valueOf(progress));
                mRadius = progress * 1000; // Convert to kilometers
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnAdvancedSearchResult = (OnAdvancedSearchResult) getActivity();
        } catch (ClassCastException e){

        }
    }
}
