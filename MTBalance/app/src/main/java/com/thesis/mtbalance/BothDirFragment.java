package com.thesis.mtbalance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.annotations.Ellipse;
import com.anychart.core.annotations.PlotController;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.SelectionMode;
import com.anychart.graphics.vector.SolidFill;

import java.util.ArrayList;

public class BothDirFragment extends Fragment {

    /* Variables */
    // The set threshold leniency
    private String mThreshold;

    // The data points of the plot
    private ArrayList<DataEntry> mPlotData;

    public BothDirFragment() {
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
        return inflater.inflate(R.layout.fragment_bothdir, container, false);
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

        // Set the color for the background and markers
        String backgroundColor = "#2B2B2B";
        String markerColor = "#52B7F8";
        String textColor = "#FFFFFF";

        // Find the plotView element in the fragment
        AnyChartView plotView = view.findViewById(R.id.plot_bothdir);

        // Set the plotView background during loading + loading icon
        plotView.setBackgroundColor(backgroundColor);
        ProgressBar progressBar = view.findViewById(R.id.progress_bothdir);
        plotView.setProgressBar(progressBar);

        // Create a scatter plot and set the background color
        Scatter scatter = AnyChart.scatter();
        scatter.background().fill(backgroundColor);

        // Set the axis titles for the plot
        scatter.xAxis(0).title("Balance deviation leaning left/right (cm)");
        scatter.xAxis(0).title().fontColor(textColor);
        scatter.yAxis(0).title("Balance deviation leaning back/front (cm)");
        scatter.yAxis(0).title().fontColor(textColor);

        // Set the min + max values for the scales of the plot (1 meter deviation)
        scatter.xScale()
                .minimum(-100f)
                .maximum(100f);
        scatter.yScale()
                .minimum(-100f)
                .maximum(100f);

        // Enable gridlines on both axes
        scatter.xGrid(0).enabled(true);
        scatter.yGrid(0).enabled(true);
        scatter.xMinorGrid(0).enabled(true);
        scatter.yMinorGrid(0).enabled(true);

        // Set clearer lines for the cartesian coordinate system
        scatter.lineMarker(0)
                .axis(scatter.xAxis(0))
                .value(0)
                .stroke("3 " + textColor);
        scatter.lineMarker(1)
                .axis(scatter.yAxis(0))
                .value(0)
                .stroke("3 " + textColor);

        // Set the interactivity mode of the plot
        scatter.interactivity()
                .hoverMode(HoverMode.BY_SPOT)
                .spotRadius(10f);
        scatter.interactivity()
                .selectionMode(SelectionMode.SINGLE_SELECT);

        // Change the tooltip name
        scatter.tooltip().title("Balance");

        // Set the marker/tooltip and plot data to it
        Marker marker = scatter.marker(mPlotData);
        marker.type(MarkerType.CIRCLE)
                .size(4f)
                .color(markerColor);
        marker.hovered()
                .size(8f)
                .fill(new SolidFill("gold", 0.5f))
                .stroke("gold 0.5f");
        marker.selected()
                .size(8f)
                .fill(new SolidFill("gold", 1f))
                .stroke("gold");
        marker.tooltip()
                .format("left/right: {%X} cm\\nback/front: {%Value} cm");

        // Create an annotation showing the optimal balance zone
        PlotController plotController = scatter.annotations();
        Ellipse balanceThreshold = plotController.ellipse("");
        balanceThreshold
                .xAnchor("-" + mThreshold)
                .secondXAnchor(mThreshold)
                .valueAnchor("-" + mThreshold)
                .secondValueAnchor(mThreshold)
                .fill("green", 0.5f)
                .stroke("2 green 0.5f");

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

        // Create a shared preferences object and get the threshold leniency
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences
                (requireContext());
        mThreshold = sharedPref.getString(SettingsActivity.KEY_THRESHOLD_LENIENCY, "0");

        // Retrieve the plot data from the PlotActivity bundle
        mPlotData = (ArrayList<DataEntry>) requireArguments()
                .getSerializable(PlotsActivity.BUNDLE_KEY_BOTHDIR);
    }
}
