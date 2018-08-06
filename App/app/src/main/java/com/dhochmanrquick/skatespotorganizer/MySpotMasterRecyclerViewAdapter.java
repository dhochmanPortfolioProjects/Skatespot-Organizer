package com.dhochmanrquick.skatespotorganizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhochmanrquick.skatespotorganizer.SpotMasterFragment.OnListFragmentInteractionListener;
import com.dhochmanrquick.skatespotorganizer.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySpotMasterRecyclerViewAdapter extends RecyclerView.Adapter<MySpotMasterRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySpotMasterRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spotmaster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mSpotNameTextView.setText(mValues.get(position).spotName);
        holder.mSpotDescriptionTextView.setText(mValues.get(position).spotDescription);
        holder.mSpotImageImageView.setImageResource(mValues.get(position).spotImage);

        holder.mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mSupportingTextTextView.getVisibility()== View.VISIBLE){
                    holder.mExpandButton.setImageResource(R.drawable.ic_expand_more_black_36dp);
                    holder.mSupportingTextTextView.setVisibility(View.GONE);
                } else {
                    holder.mSupportingTextTextView.setVisibility(View.VISIBLE);
                    holder.mExpandButton.setImageResource(R.drawable.ic_expand_less_black_36dp);
                }
            }
        });

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mSpotNameTextView;
        public final TextView mSpotDescriptionTextView;
        public final ImageView mSpotImageImageView;
        public final ImageButton mExpandButton;
        public final TextView mSupportingTextTextView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSpotNameTextView = (TextView) view.findViewById(R.id.spot_title_tv);
            mSpotDescriptionTextView = (TextView) view.findViewById(R.id.spot_description_tv);
            mSpotImageImageView = (ImageView) view.findViewById(R.id.spot_image_iv);
            mExpandButton = (ImageButton) view.findViewById(R.id.expand_button);
            mSupportingTextTextView = (TextView) view.findViewById(R.id.supporting_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSpotDescriptionTextView.getText() + "'";
        }
    }
}
