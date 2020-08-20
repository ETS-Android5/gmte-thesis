package com.thesis.mtbalance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RidesFragment extends Fragment {

    /* Variables */
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Setup the adapter and link it to the recyclerview
        mRVAdapter = new RecyclerViewAdapter(getContext(), mRidesData);
        mRecyclerView.setAdapter(mRVAdapter);

        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Todo: fill mRidesData with correct data here!
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
        mRidesData.add(new RidesItem
                ("11:56 min", "19 August 2020 - 16:20",
                        "Directional", "68.15 %",
                        "69.11 cm", "11.34 ms"));
    }
}
