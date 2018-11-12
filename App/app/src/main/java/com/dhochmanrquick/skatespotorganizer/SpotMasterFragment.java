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

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SpotMasterFragment extends Fragment {

    // Member variables
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount; // Todo: What is this?
    private OnListFragmentInteractionListener mListener;
    private SpotViewModel mSpotViewModel;
    private SpotMasterRecyclerViewAdapter mSpotMasterRecyclerViewAdapter;
    private SpotMasterRecyclerViewGridAdapter mSpotMasterRecyclerViewGridAdapter;

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
     *
     * (I don't entirely understand why we're using the Factory design pattern for instantiation
     * but I think I've seen that it's the convention with Fragment creation).
     *
     * @param columnCount
     * @return  The new SpotMasterFragment instance
     */
    @SuppressWarnings("unused")
    public static SpotMasterFragment newInstance(int columnCount) {
        SpotMasterFragment fragment = new SpotMasterFragment();
        // Todo: Why the Bundle with the columnCount?
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate fragment_spotmaster_list.xml, which contains only the RecyclerView widget
        View view = inflater.inflate(R.layout.fragment_spotmaster_list, container, false);

        // Set the mSpotMasterRecyclerViewAdapter
//        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                mSpotMasterRecyclerViewAdapter = new SpotMasterRecyclerViewAdapter(getContext(), mListener);
                recyclerView.setAdapter(mSpotMasterRecyclerViewAdapter);
            } else {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mColumnCount);
//                gridLayoutManager.scrollToPosition(0); // This didn't work
//                gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL); // This didn't work
//                gridLayoutManager.setStackFromEnd(true); // java.lang.UnsupportedOperationException: GridLayoutManager does not support stack from end. Consider using reverse layout
//                gridLayoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(gridLayoutManager);
                mSpotMasterRecyclerViewGridAdapter = new SpotMasterRecyclerViewGridAdapter(getContext(), mListener);
                recyclerView.setAdapter(mSpotMasterRecyclerViewGridAdapter);
            }
            // Create the mSpotMasterRecyclerViewAdapter and pass it the list of dummy Spots
//            recyclerView.setAdapter(new SpotMasterRecyclerViewAdapter(DummyContent.get(getActivity()).getSpots(), mListener));
//        }

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
//        // Whenever the data changes, the onChanged() callback is invoked, which calls the mSpotMasterRecyclerViewAdapter's setWord()
//        // method to update the mSpotMasterRecyclerViewAdapter's cached data and refresh the displayed list.
        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable final List<Spot> words) {
                // Update the cached copy of the words in the mSpotMasterRecyclerViewAdapter.
//                mSpotMasterRecyclerViewAdapter.setWords(words);
                if (mSpotMasterRecyclerViewAdapter != null) {
                    mSpotMasterRecyclerViewAdapter.setWords(words);
                } else {
                    mSpotMasterRecyclerViewGridAdapter.setWords(words);
                }
            }
        });

        return view;
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
     * @param query     The search String that the user queried for
     */
//    public void handleSearchQuery(String query) {
//
//    }

//    public void updateUI(List<Spot> spots) {
//        // Update the cached copy of the words in the mSpotMasterRecyclerViewAdapter.
//        mSpotMasterRecyclerViewAdapter.setWords(spots);
//    }
}
