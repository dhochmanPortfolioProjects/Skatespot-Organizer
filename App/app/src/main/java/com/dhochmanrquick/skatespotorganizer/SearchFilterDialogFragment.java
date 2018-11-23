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
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
//            new LatLng(-40, -168), new LatLng(71, 136));

    // Widgets
    private TextView mRadius_TextView;
    private SeekBar mRadius_SeekBar;

    // Vars
    private int mRadius = 0;

    public interface OnSearchFilterResult{
        void sendSearchFilterResult(int radius);
    }

    /**
     * A member variable to hold the implementation of the OnSearchFilterResult interface
     */
    public OnSearchFilterResult mOnSearchFilterResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogfragment_search_filter, container, false);

        mRadius_TextView = view.findViewById(R.id.dialogfragment_search_filter_radius_tv);

        view.findViewById(R.id.dialogfragment_search_filter_apply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSearchFilterResult.sendSearchFilterResult(mRadius);
                getDialog().dismiss();
            }
        });

        mRadius_SeekBar = view.findViewById(R.id.dialogfragment_search_filter_radius_seekbar);
        mRadius_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        view.findViewById(R.id.dialogfragment_search_filter_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set all filter values to default values
                mRadius_SeekBar.setProgress(0);
                mRadius = 0;
                mOnSearchFilterResult.sendSearchFilterResult(mRadius);
                getDialog().dismiss();
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
