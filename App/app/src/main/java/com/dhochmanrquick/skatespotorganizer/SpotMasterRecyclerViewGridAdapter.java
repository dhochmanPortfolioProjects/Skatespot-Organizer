package com.dhochmanrquick.skatespotorganizer;//package com.dhochmanrquick.skatespotorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhochmanrquick.skatespotorganizer.SpotMasterFragment.OnListFragmentInteractionListener;
import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
//import com.dhochmanrquick.skatespotorganizer.dummy.DummyContent.DummyItem;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SpotMasterRecyclerViewGridAdapter extends RecyclerView.Adapter<SpotMasterRecyclerViewGridAdapter.ViewHolder> {

    // Inner class: ViewHolder (this could also be declared in the SpotMasterFragment)
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Spot mItem;
        public final ImageView mSpotImage_ImageView;
        public final TextView mSpotTitle_TextView;
//        public final TextView mSpotDescription_TextView;

        // Constructor: Get the Views for this list item. The view passed in has already been inflated
        // so no need to inflate it here.
        public ViewHolder(View view) {
            super(view);
            mView = view; // fragment_spotmaster_list_item inflated
            mSpotImage_ImageView = view.findViewById(R.id.fragment_spotmaster_list_smallcard_item_spot_iv);
            mSpotTitle_TextView = view.findViewById(R.id.fragment_spotmaster_list_smallcard_item_spot_title_tv);
//            mSpotDescription_TextView = view.findViewById(R.id.spot_detail_description_title_tv);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mSpotDescription_TextView.getText() + "'";
//        }
    } // END ViewHolder class declaration

    private List<Spot> mSpots; // Note that this variable caches the the master list of Dummy spots
    private final OnListFragmentInteractionListener mListener;
    private final LayoutInflater mInflater;
    Context mContext;

    // Constructor (called from SpotMasterFragment); sets member variables
//    public SpotMasterRecyclerViewAdapter(List<Spot> spots, OnListFragmentInteractionListener listener) {
    SpotMasterRecyclerViewGridAdapter(Context context, OnListFragmentInteractionListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    void setWords(List<Spot> spots) {
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
                .inflate(R.layout.fragment_spotmaster_list_smallcard_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Spot spot = mSpots.get(position); // Get the Spot from the spot list at the position requested by the RecyclerView
        holder.mItem = spot;
        holder.mSpotTitle_TextView.setText(spot.getName());
//        holder.mSpotDescription_TextView.setText(spot.getDescription());
//        holder.mSpotImage_ImageView.setImageResource(R.drawable.paju_spot_landscape);
//        holder.mSpotImage_ImageView.setImageResource(spot.getImageID());

//        ViewModel mSpotViewModel = ViewModelProviders.of(mContext.getApplicationInfo().getActivity()).get(SpotViewModel.class);
//        ViewModel mSpotViewModel = ViewModelProviders.


//        PictureUtils.getPhotoFile(mContext.getApplicationContext(), spot);
//        holder.mSpotImage_ImageView.setImageBitmap(bitmap);

        // Set Spot's ImageView as a Bitmap
        if (spot.getPhotoCount() > 0) {
            File filesDir = mContext.getFilesDir(); // Get handle to directory for private application files
//            File photoFile = new File(filesDir, spot.getPhotoFilepath(1)); // Create new File in the directory
//            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 1000, 1000);
            Bitmap bitmap = PictureUtils.getScaledBitmap(spot.getPhotoFilepath(1), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
            holder.mSpotImage_ImageView.setImageBitmap(bitmap);
        } else {
            // Read your drawable from somewhere
            Drawable dr = mContext.getResources().getDrawable(R.drawable.ic_no_image);
//            dr.setBounds(5, 5, 5, 5);
//            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//            // Scale it to 50 x 50
//            Drawable d = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            // Set your new, scaled drawable "d"
//            holder.mSpotImage_ImageView.setImageResource(R.drawable.ic_no_image);
            holder.mSpotImage_ImageView.setImageDrawable(dr);
        }

//        spot.getImageID() == 0 ? holder.mSpotImage_ImageView.setImageBitmap(bitmap) : holder == null;


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


