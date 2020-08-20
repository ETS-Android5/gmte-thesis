package com.thesis.mtbalance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RidesFragment extends Fragment {

    /* Variables */
    private FileHelper fileHelper;
    private View mView;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRVAdapter;

    private ArrayList<RidesItem> mRidesData = new ArrayList<>();

    public RidesFragment() {
    }

    /**
     * Creates the layout of the fragment in the container.
     *
     * @param inflater           - helper to inflate the layout.
     * @param container          - the container to inflate to fragment in.
     * @param savedInstanceState - state holding data on the onPause callback.
     * @return a view element with the inflated fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Create a view from the layout
        mView = inflater.inflate(R.layout.fragment_rides, container, false);

        // Setup the recyclerview
        mRecyclerView = mView.findViewById(R.id.recyclerview_rides);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Setup the adapter and link it to the recyclerview
        mRVAdapter = new RecyclerViewAdapter(getContext(), mRidesData);
        mRecyclerView.setAdapter(mRVAdapter);

        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the data from the file as an arrayList of strings
        fileHelper = new FileHelper();
        ArrayList<String> rideStringData = fileHelper.loadFromFile
                ("rides", requireContext());

        // Check if there is data in the first place
        if (rideStringData.size() == 0) {
            // Todo: set a textview in the activity to show there are no items
            return;
        }

        // Create a shared preferences object and get the current participant number
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences
                (requireContext());
        String participantNumber = sharedPref.getString
                (SettingsActivity.KEY_PARTICIPANT_NUMBER, "0");

        // Loop over all the rides in the string data and split the values
        for (String ride : rideStringData) {
            String[] rideArray = ride.split(",");

            // Discard the ride data if the participant does not match the settings participant.
            if (!rideArray[1].equals(participantNumber))
                continue;

            // Add the rideArray as a rides item
            addRidesItem(rideArray);
        }
    }

    /**
     * Adds the dataArray as a rides item to the list.
     *
     * @param dataArray - the data to parse as a rides item.
     */
    private void addRidesItem(String[] dataArray) {
        // Get the name for the feedback method out of the string array
        String feedbackMethod = getResources().getStringArray
                (R.array.feedback_method_entries)[Integer.parseInt(dataArray[2])];

        // Datetime is already correct
        String dateTime = dataArray[3];

        // Get a string array from the DVs with their respective unit denotations
        String[] rideDVS = fileHelper.stringArrayToMeasurements
                (new String[]{dataArray[4], dataArray[5], dataArray[6], dataArray[7]},
                        new String[]{"%", "cm", "ms", "sec"});

        // Add the data to the list of ride items
        mRidesData.add(new RidesItem(rideDVS[3], dateTime, feedbackMethod,
                rideDVS[0], rideDVS[1], rideDVS[2]));
    }
}