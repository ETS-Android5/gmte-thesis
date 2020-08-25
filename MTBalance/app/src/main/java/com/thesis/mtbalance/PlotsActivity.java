package com.thesis.mtbalance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class PlotsActivity extends AppCompatActivity {

    // Public keys used by fragments to get access to the plots data
    public static final String BUNDLE_KEY_BOTHDIR =
            "com.thesis.mtbalance.plotsactivity.bundle.KEY.BOTHDIR";
    public static final String BUNDLE_KEY_XDIR =
            "com.thesis.mtbalance.plotsactivity.bundle.KEY.XDIR";
    public static final String BUNDLE_KEY_YDIR =
            "com.thesis.mtbalance.plotsactivity.bundle.KEY.YDIR";

    /* Variables */
    // ArrayLists of data entry objects for the plots
    private ArrayList<DataEntry> mBothDirData = new ArrayList<>();
    private ArrayList<DataEntry> mXDirData = new ArrayList<>();
    private ArrayList<DataEntry> mYDirData = new ArrayList<>();

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState - state holding saved info from onPause callback.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots);

        // Get the plot data from the passed intent
        getPlotData();

        // Set the tablayout
        setTabLayout();
    }

    /**
     * Gets the data for the plots.
     */
    private void getPlotData() {
        // Create a shared preferences object and get the plot resolution
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int plotResolution = Integer.parseInt(Objects.requireNonNull
                (sharedPref.getString(SettingsActivity.KEY_PLOT_RESOLUTION, "60")));
        if (plotResolution <= 0)
            plotResolution = 1;

        // Get the intent data from the card view click
        Intent intent = getIntent();
        String fileDir = intent.getStringExtra(RecyclerViewAdapter.EXTRA_FILEDIR);

        // Create a new fileHelper object
        FileHelper fileHelper = new FileHelper();

        // Retrieve the plot data from the file directory
        ArrayList<String> rideData = fileHelper.loadArrayData(fileDir, this);

        // Loop over all the strings in the rideData and turn them into data entry elements
        int i = -1;
        for (String ride : rideData) {
            // Add data depending on the resolution set in the preferences
            i++;
            if (i % plotResolution != 0)
                continue;

            // Parse the data and add it to the data entries
            float[] data = fileHelper.stringToFloatArray(ride);
            mBothDirData.add(new ValueDataEntry(data[1], data[2]));     // X and Y
            mXDirData.add(new ValueDataEntry(data[1], data[0]));        // X and Time
            mYDirData.add(new ValueDataEntry(data[0], data[2]));        // Time and Y
        }
    }

    /**
     * Sets the tablayout in the main activity.
     */
    private void setTabLayout() {
        // Find the elements needed for setup
        TabLayout tabLayout = findViewById(R.id.tablayout_plots);
        ViewPager viewPager = findViewById(R.id.viewpager_plots);
        ViewPagerAdapter VPAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Create a bundle holding the plot data
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_BOTHDIR, mBothDirData);
        bundle.putSerializable(BUNDLE_KEY_XDIR, mXDirData);
        bundle.putSerializable(BUNDLE_KEY_YDIR, mYDirData);

        // Create the fragments and add the bundle data
        BothDirFragment bothDirFragment = new BothDirFragment();
        XDirFragment xDirFragment = new XDirFragment();
        YDirFragment yDirFragment = new YDirFragment();

        bothDirFragment.setArguments(bundle);
        xDirFragment.setArguments(bundle);
        yDirFragment.setArguments(bundle);

        // Add the desired fragments, without title to show icon
        VPAdapter.addFragment(bothDirFragment, "");
        VPAdapter.addFragment(xDirFragment, "");
        VPAdapter.addFragment(yDirFragment, "");

        // Setup the adapter and viewpager
        viewPager.setAdapter(VPAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Add the icons and remove elevation from the actionbar
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_directions);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_horizontal);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_vertical);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setElevation(0);
    }

    /**
     * Called when a menu button is pressed.
     *
     * @param item - the item that is pressed.
     * @return the state of the method execution.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Finish the activity to prevent data leakage
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}