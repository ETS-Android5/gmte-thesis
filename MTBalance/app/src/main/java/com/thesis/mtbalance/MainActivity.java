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
        TabLayout tabLayout = findViewById(R.id.tablayout_main);
        ViewPager viewPager = findViewById(R.id.viewpager_main);
        ViewPagerAdapter VPAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add the desired fragments, without title to show icon
        VPAdapter.addFragment(new RidesFragment(), "");
        VPAdapter.addFragment(new InfoFragment(), "");

        // Setup the adapter and viewpager
        viewPager.setAdapter(VPAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Add the icons and remove elevation from the actionbar
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_chart);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_help);
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
        // Disable button when a card is clicked
        if (RecyclerViewAdapter.ITEM_CLICKED)
            return false;

        // Start settings activity
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        // Start testing activity
        if (item.getItemId() == R.id.testing) {
            Intent intent = new Intent(this, TestingActivity.class);
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
        // Disable button when a card is clicked
        if (RecyclerViewAdapter.ITEM_CLICKED)
            return;

        Intent intent = new Intent(this, MeasuringActivity.class);
        startActivity(intent);
    }
}