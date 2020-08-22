package com.thesis.mtbalance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.annotations.Ellipse;
import com.anychart.core.annotations.PlotController;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.MarkerType;
import com.anychart.graphics.vector.text.HAlign;

import java.util.ArrayList;

public class XDirFragment extends Fragment {

    /* Variables */
    // The data points of the plot.
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

        // Todo: change to line plot and allow for zooming in

        // Set the color for the background and markers
        String backgroundColor = "#2B2B2B";
        String markerColor = "#52B7F8";

        // Find the plotView element in the fragment
        AnyChartView plotView = view.findViewById(R.id.plot_xdir);

        // Set the plotView background during loading + loading bar
        plotView.setBackgroundColor(backgroundColor);
        ProgressBar progressBar = view.findViewById(R.id.progress_xdir);
        plotView.setProgressBar(progressBar);

        // Create a scatter plot and set the background color
        Scatter scatter = AnyChart.scatter();
        scatter.background().fill(backgroundColor);

        // Set the min + max values for the scales of the plot
        scatter.xScale()
                .minimum(-100f)
                .maximum(100f);
        scatter.yScale()
                .minimum(0f);

        // Enable gridlines on both axes
        scatter.xGrid(0).enabled(true);
        scatter.yGrid(0).enabled(true);
        scatter.xMinorGrid(0).enabled(true);
        scatter.yMinorGrid(0).enabled(true);

        // Set clearer line for the x axis
        scatter.lineMarker(0)
                .axis(scatter.xAxis(0))
                .value(0)
                .stroke("2 white");

        // Change the tooltip name
        scatter.tooltip().title("Balance");

        // Set the marker/tooltip and plot data to it
        Marker marker = scatter.marker(mPlotData);
        marker.type(MarkerType.CIRCLE)
                .size(4f)
                .color(markerColor);
        marker.tooltip()
                .hAlign(HAlign.CENTER)
                .format("time: {%Value} sec\\nleft/right: {%X} cm");

        // Show the plot
        plotView.setChart(scatter);
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
