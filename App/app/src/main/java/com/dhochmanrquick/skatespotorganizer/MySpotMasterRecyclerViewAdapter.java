package com.dhochmanrquick.skatespotorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhochmanrquick.skatespotorganizer.SpotMasterFragment.OnListFragmentInteractionListener;
//import com.dhochmanrquick.skatespotorganizer.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySpotMasterRecyclerViewAdapter extends RecyclerView.Adapter<MySpotMasterRecyclerViewAdapter.ViewHolder> {

    private final List<Spot> mSpots; // Wraps the master list of Dummy spots
    private final OnListFragmentInteractionListener mListener;

    // Constructor (called from SpotMasterFragment); sets member variables
    public MySpotMasterRecyclerViewAdapter(List<Spot> spots, OnListFragmentInteractionListener listener) {
        mSpots = spots;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the ViewHolder's View and construct it with that View (this could also be done
        // in the ViewHolder's constructor
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spotmaster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Spot spot = mSpots.get(position); // Get the Spot at the position requested by the RecyclerView
        holder.mItem = spot;
        holder.mSpotTitle_TextView.setText(spot.getName());
        holder.mSpotDescription_TextView.setText(spot.getDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSpots.size();
    }

    // Inner class: ViewHolder (this could also be declared in the SpotMasterFragment)
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mSpotTitle_TextView;
        public final TextView mSpotDescription_TextView;
        public Spot mItem;

        // Constructor: Get the Views for this list item. The view passed in has already been inflated
        // so no need to inflate here
        public ViewHolder(View view) {
            super(view);
            mView = view; // fragment_spotmaster_item inflated
            mSpotTitle_TextView = view.findViewById(R.id.spot_title_tv);
            mSpotDescription_TextView = view.findViewById(R.id.spot_description_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSpotDescription_TextView.getText() + "'";
        }
    }
}
