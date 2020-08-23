package com.thesis.mtbalance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.chart.common.dataentry.DataEntry;

import java.util.ArrayList;

public class XDirFragment extends Fragment {

    /* Variables */
    // The data points of the plot
    private ArrayList<DataEntry> mPlotData;

    private View mView;

    public XDirFragment() {
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
        mView = inflater.inflate(R.layout.fragment_xdir, container, false);
        return mView;
    }

    /**
     * Called after the view is created.
     * The plot is populated here since views in fragments can only be accessed after creation.
     *
     * @param view               - the current view of the fragment.
     * @param savedInstanceState - the saved instance of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * Called on the creation of the fragment (before onCreateView).
     * Used for initializing data.
     *
     * @param savedInstanceState - the saved instance of the fragment.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the plot data from the PlotActivity bundle
        mPlotData = (ArrayList<DataEntry>) requireArguments()
                .getSerializable(PlotsActivity.BUNDLE_KEY_XDIR);
    }
}
