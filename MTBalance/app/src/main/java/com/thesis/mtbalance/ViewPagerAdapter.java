package com.thesis.mtbalance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    /* Variables */
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mTitlesList = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param fm - the fragment manager for the adapter.
     */
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    /**
     * Gets the fragment given an index.
     *
     * @param position - the index for the fragment.
     * @return the retrieved fragment.
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * Gets the tab count.
     *
     * @return the tab count.
     */
    @Override
    public int getCount() {
        return mTitlesList.size();
    }

    /**
     * Gets the page title given a position.
     *
     * @param position - the index to retrieve the title from.
     * @return the title at the position.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitlesList.get(position);
    }

    /**
     * Adds a fragment to the adapter.
     *
     * @param fragment - the fragment to add.
     * @param title    - the title of the fragment.
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitlesList.add(title);
    }
}
