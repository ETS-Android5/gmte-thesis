package com.thesis.mtbalance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class PlotsActivity extends AppCompatActivity {

    // Public key used by fragments to get access to the plots data
    public static final String BUNDLE_KEY =
            "com.thesis.mtbalance.plotsactivity.bundle.KEY";

    /* Variables */
    private ArrayList<String> mPlotData;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mVPAdapter;

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

    private void getPlotData() {
        // Get the intent data from the card view click
        Intent intent = getIntent();
        String fileDir = intent.getStringExtra(RecyclerViewAdapter.EXTRA_FILEDIR);

        // Retrieve the plot data from the file directory
        mPlotData = new FileHelper().loadArrayData(fileDir, this);

        // todo: make mPlotdata arraylist of floats instead and do processing beforehand
    }

    /**
     * Sets the tablayout in the main activity.
     */
    private void setTabLayout() {
        // Find the elements needed for setup
        mTabLayout = findViewById(R.id.tablayout_plots);
        mViewPager = findViewById(R.id.viewpager_plots);
        mVPAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Create a bundle holding the plot data
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BUNDLE_KEY, mPlotData);

        // Create the fragments and add the bundle data
        BothDirFragment bothDirFragment = new BothDirFragment();
        YDirFragment yDirFragment = new YDirFragment();
        XDirFragment xDirFragment = new XDirFragment();

        bothDirFragment.setArguments(bundle);
        yDirFragment.setArguments(bundle);
        xDirFragment.setArguments(bundle);

        // Add the desired fragments, without title to show icon
        mVPAdapter.addFragment(bothDirFragment, "");
        mVPAdapter.addFragment(yDirFragment, "");
        mVPAdapter.addFragment(xDirFragment, "");

        // Setup the adapter and viewpager
        mViewPager.setAdapter(mVPAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Add the icons and remove elevation from the actionbar
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.ic_directions);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.ic_vertical);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.ic_horizontal);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setElevation(0);
    }
}