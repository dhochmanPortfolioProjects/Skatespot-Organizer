package com.dhochmanrquick.skatespotorganizer;

import android.content.Context;
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

    // Inner class: ViewHolder (this could also be declared in the SpotMasterFragment)
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mSpotTitle_TextView;
        public final TextView mSpotDescription_TextView;
        public Spot mItem;

        // Constructor: Get the Views for this list item. The view passed in has already been inflated
        // so no need to inflate it here.
        public ViewHolder(View view) {
            super(view);
            mView = view; // fragment_spotmaster_item inflated
            mSpotTitle_TextView = view.findViewById(R.id.spot_title_tv);
            mSpotDescription_TextView = view.findViewById(R.id.spot_description_tv);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mSpotDescription_TextView.getText() + "'";
//        }
    }

    private List<Spot> mSpots; // Note that this variable caches the the master list of Dummy spots
    private final OnListFragmentInteractionListener mListener;
    private final LayoutInflater mInflater;

    // Constructor (called from SpotMasterFragment); sets member variables
//    public MySpotMasterRecyclerViewAdapter(List<Spot> spots, OnListFragmentInteractionListener listener) {
    MySpotMasterRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    void setWords(List<Spot> spots){
        mSpots = spots;
        notifyDataSetChanged();
    }

//        mSpots = spots;
//        mListener = listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the list item view to be held by a ViewHolder and then create the ViewHolder with that View (this could also be done
        // in the ViewHolder's constructor
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spotmaster_item, parent, false);
        return new ViewHolder(itemView);
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

    // Account gracefully for the possibility that the data is not yet ready and mWords is still null;
    // getItemCount() is called many times, and when it is first called,
    // mSpots has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mSpots != null)
            return mSpots.size();
        else return 0;
    }
}
