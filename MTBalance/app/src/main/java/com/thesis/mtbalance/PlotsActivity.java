package com.thesis.mtbalance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class PlotsActivity extends AppCompatActivity {

    /* Variables */
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

        // Set the tablayout
        setTabLayout();

        // Get the intent data from the card view click
        Intent intent = getIntent();
        String fileDir = intent.getStringExtra(RecyclerViewAdapter.EXTRA_FILEDIR);
    }

    /**
     * Sets the tablayout in the main activity.
     */
    private void setTabLayout() {
        // Find the elements needed for setup
        mTabLayout = findViewById(R.id.tablayout_plots);
        mViewPager = findViewById(R.id.viewpager_plots);
        mVPAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add the desired fragments, without title to show icon
        mVPAdapter.addFragment(new BothDirFragment(), "");
        mVPAdapter.addFragment(new YDirFragment(), "");
        mVPAdapter.addFragment(new XDirFragment(), "");

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