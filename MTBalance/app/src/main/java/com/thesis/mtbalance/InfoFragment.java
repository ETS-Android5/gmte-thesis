package com.thesis.mtbalance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {

    public InfoFragment() {
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }
}
