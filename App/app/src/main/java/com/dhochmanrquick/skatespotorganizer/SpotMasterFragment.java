package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SpotMasterFragment extends Fragment {

    // Member variables
    Context mContext;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
//    private int mColumnCount; // Todo: What is this?
    private OnListFragmentInteractionListener mListener;
    private SpotViewModel mSpotViewModel;
    private List<Spot> mSpots;
    private RecyclerView mRecyclerView;
    private SpotMasterRecyclerViewAdapter mListAdapter;
    private SpotMasterRecyclerViewGridAdapter mGridAdapter;
    private boolean mIsInListView = false;
    private boolean mIsInGridView = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpotMasterFragment() {
    }

    // TODO: Customize parameter initialization

    /**
     * A static factory method for creating a new SpotMasterFragment. This method creates a new
     * instance of SpotMasterFragment, creates and sets a Bundle containing the columnCount on
     * the fragment, and returns the fragment.
     * <p>
     * (I don't entirely understand why we're using the Factory design pattern for instantiation
     * but I think I've seen that it's the convention with Fragment creation).
     *
     * @return The new SpotMasterFragment instance
     */
    @SuppressWarnings("unused")
    public static SpotMasterFragment newInstance(/*int columnCount*/) {
        SpotMasterFragment fragment = new SpotMasterFragment();
        // Todo: Why the Bundle with the columnCount?
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Todo: What is this? Do we need it?
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }

//        mContext = getContext();
//        mRecyclerView = getActivity().findViewById(R.id.fragment_spotmaster_list_rv);

        // Cannot just use findViewById because this is a Fragment
        // java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.View.setOnClickListener(android.view.View$OnClickListener)' on a null object reference
//        getActivity().findViewById(R.id.fragment_spotmaster_list_listview_ic_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//                mListAdapter = new SpotMasterRecyclerViewAdapter(getContext(), mListener);
//                mRecyclerView.setAdapter(mListAdapter);
//            }
//        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Find and inflate this Fragment's layout
        View fragment_spotmaster_parent_View = inflater.inflate(R.layout.fragment_spotmaster, container, false);

        // Set the mListAdapter
        mContext = fragment_spotmaster_parent_View.getContext(); // Get the Context from the parent View
        // Get the RecyclerView nested inside the parent View
        mRecyclerView = fragment_spotmaster_parent_View.findViewById(R.id.fragment_spotmaster_list_rv);

        // Create both Adapters
        mListAdapter = new SpotMasterRecyclerViewAdapter(getContext(), mListener);
        mGridAdapter = new SpotMasterRecyclerViewGridAdapter(getContext(), mListener);

//        if (mColumnCount <= 1) {
            mIsInListView = true;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(mListAdapter);
//        } else {
//            mIsInGridView = true;
//            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mColumnCount));
//            mRecyclerView.setAdapter(mGridAdapter);
//        }

        fragment_spotmaster_parent_View.findViewById(R.id.fragment_spotmaster_list_listview_ic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsInListView = true;
                mIsInGridView = false;
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mListAdapter.setSpots(mSpots);
                mRecyclerView.setAdapter(mListAdapter);
            }
        });

        fragment_spotmaster_parent_View.findViewById(R.id.fragment_spotmaster_list_gridview_ic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsInListView = false;
                mIsInGridView = true;
                mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
                mGridAdapter.setSpots(mSpots);
                mRecyclerView.setAdapter(mGridAdapter);
            }
        });

        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
        //
        // This creates the ViewModel and stores it in the local variable mSpotViewModel.
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
//
//        // An observer for the LiveData returned by getAllWords().
//        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
//        // Whenever the data changes, the onChanged() callback is invoked, which calls the mListAdapter's setWord()
//        // method to update the mListAdapter's cached data and refresh the displayed list.
        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable final List<Spot> spots) {

                mSpots = spots;

                // Update the cached copy of the words in the mListAdapter.
//                mListAdapter.setSpots(words);
                if (mListAdapter != null && mIsInListView) {
                    mListAdapter.setSpots(spots);
                } else if (mGridAdapter != null && mIsInGridView) {
                    mGridAdapter.setSpots(spots);
                }
            }
        });

        return fragment_spotmaster_parent_View;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Spot item);
    }

    /**
     * A method to respond to the action of a user submitting a search via the app bar. MainActivity
     * receives the ACTION_SEARCH Intent in onNewIntent() and calls this method if the SpotMasterFragment
     * is the currently loaded fragment, passing in the search String that the user queried for.
     *
     * @param      The search String that the user queried for
     */
//    public void handleSearchQuery(String query) {
//
//    }

//    public void updateUI(List<Spot> spots) {
//        // Update the cached copy of the words in the mListAdapter.
//        mListAdapter.setSpots(spots);
//    }

    public void displayAdvancedSearchResult(LatLng searchResult_LatLng, int radius) {
        List<Spot> spots = new ArrayList<>();
        for (Spot spot: mSpots) {
            // "static double	computeDistanceBetween(LatLng from, LatLng to)"
            // Returns the distance between two LatLngs, in meters.
            if(SphericalUtil.computeDistanceBetween(searchResult_LatLng, spot.getLatLng()) <= radius) {
                spots.add(spot);
            }
        }

        if (mListAdapter != null && mIsInListView) {
            mListAdapter.setSpots(spots);
        } else if (mGridAdapter != null && mIsInGridView) {
            mGridAdapter.setSpots(spots);
        }
    }
}
