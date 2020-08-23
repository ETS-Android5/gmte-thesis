package com.thesis.mtbalance;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        // Set the default preferences on first startup
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Set the tablayout
        setTabLayout();
    }

    /**
     * Sets the tablayout in the main activity.
     */
    private void setTabLayout() {
        // Find the elements needed for setup
        mTabLayout = findViewById(R.id.tablayout_main);
        mViewPager = findViewById(R.id.viewpager_main);
        mVPAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add the desired fragments, without title to show icon
        mVPAdapter.addFragment(new RidesFragment(), "");
        mVPAdapter.addFragment(new InfoFragment(), "");

        // Setup the adapter and viewpager
        mViewPager.setAdapter(mVPAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Add the icons and remove elevation from the actionbar
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.ic_chart);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.ic_help);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setElevation(0);
    }

    /**
     * Called on options menu creation.
     *
     * @param menu - the to be inflated menu object.
     * @return boolean indicating success.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Called on selection of item in options menu.
     *
     * @param item - the selected item.
     * @return boolean indicating if selection should be swallowed.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Start settings activity
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the measurement activity.
     *
     * @param view - the clicked fab.
     */
    public void startRide(View view) {
        Intent intent = new Intent(this, MeasuringActivity.class);
        startActivity(intent);
    }
}