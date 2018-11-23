package com.dhochmanrquick.skatespotorganizer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class SearchFilterDialogFragment extends DialogFragment {

    // Constants
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    // Widgets
    private TextView mRadius_TextView;

    // Vars
    private int mRadius = 0;

    public interface OnSearchFilterResult{
        void sendSearchFilterResult(int radius);
    }

    public OnSearchFilterResult mOnSearchFilterResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_search_filter, container, false);

//        mAutoCompleteTextView = view.findViewById(R.id.advanced_search_AutoCompleteTextView);
        mRadius_TextView = view.findViewById(R.id.advanced_search_radius_tv);

        view.findViewById(R.id.dialog_search_filter_apply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSearchFilterResult.sendSearchFilterResult(mRadius);
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
            mOnSearchFilterResult = (OnSearchFilterResult) getActivity();
        } catch (ClassCastException e){

        }
    }
}
