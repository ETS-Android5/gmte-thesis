package com.thesis.mtbalance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.RidesViewHolder> {

    // Used to pass the startTime as an intent to the PlotsActivity
    public static final String EXTRA_FILEDIR =
            "com.thesis.mtbalance.recyclerviewadapter.extra.FILEDIR";

    public static boolean ITEM_CLICKED;

    /* Finals */
    private final Context CONTEXT;
    private final ArrayList<RidesItem> RIDES_DATA;

    /**
     * Constructor for the view adapter, used by the recyclerview to link data.
     *
     * @param context   - the application context.
     * @param ridesData - the data to connect to the recyclerview.
     */
    public RecyclerViewAdapter(Context context, ArrayList<RidesItem> ridesData) {
        CONTEXT = context;
        RIDES_DATA = ridesData;
    }

    /**
     * Creates the viewholder for an item element in the recyclerview.
     *
     * @param parent   - the parent of the viewholder (recyclerview).
     * @param viewType - what type of view we are dealing with.
     * @return returns the viewholder.
     */
    @NonNull
    @Override
    public RidesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new RidesViewHolder object
        final RidesViewHolder rvh = new RidesViewHolder(LayoutInflater.from(CONTEXT)
                .inflate(R.layout.item_rides, parent, false));

        // Set the initial click state to false
        ITEM_CLICKED = false;

        // Sets an onClickListener for each element in the recycler view
        rvh.CARD_VIEW.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Make sure an item is only clicked once
                if (ITEM_CLICKED)
                    return;
                ITEM_CLICKED = true;

                // Start the PlotsActivity on a click, passing the current startTime as data
                String startTime = RIDES_DATA.get(rvh.getAdapterPosition()).getStartTime();
                Intent intent = new Intent(CONTEXT, PlotsActivity.class);
                intent.putExtra(EXTRA_FILEDIR, startTime);
                CONTEXT.startActivity(intent);
            }
        });

        return rvh;
    }

    /**
     * Binds the data to the viewholder elements.
     *
     * @param holder   - the holder to connect the data to.
     * @param position - the position of the card to get the correct element.
     */
    @Override
    public void onBindViewHolder(@NonNull RidesViewHolder holder, int position) {

        holder.COMP_TIME_TV.setText(RIDES_DATA.get(position).getCompletionTime());
        holder.DATE_TIME_TV.setText(RIDES_DATA.get(position).getDateTime());

        holder.FEEDBACK_TV.setText(RIDES_DATA.get(position).getFeedbackMethod());
        holder.BAL_PERF_TV.setText(RIDES_DATA.get(position).getBalancePerformance());
        holder.BAL_DEV_TV.setText(RIDES_DATA.get(position).getBalanceDeviation());
        holder.RESP_TIME_TV.setText(RIDES_DATA.get(position).getResponseTime());
    }

    /**
     * Gets the amount of items in the recyclerview.
     *
     * @return An integer representing the amount of items.
     */
    @Override
    public int getItemCount() {
        return RIDES_DATA.size();
    }

    /**
     * Custom viewholder for the rides data.
     */
    public static class RidesViewHolder extends RecyclerView.ViewHolder {

        /* Finals */
        private final CardView CARD_VIEW;

        private final TextView COMP_TIME_TV;
        private final TextView DATE_TIME_TV;

        private final TextView FEEDBACK_TV;
        private final TextView BAL_PERF_TV;
        private final TextView BAL_DEV_TV;
        private final TextView RESP_TIME_TV;

        /**
         * Setup the textview elements for the viewholder.
         *
         * @param itemView - the current item we are feeding data.
         */
        public RidesViewHolder(@NonNull View itemView) {
            super(itemView);

            CARD_VIEW = itemView.findViewById(R.id.rides_cardview);

            COMP_TIME_TV = itemView.findViewById(R.id.comptime_textview);
            DATE_TIME_TV = itemView.findViewById(R.id.datetime_textview);

            FEEDBACK_TV = itemView.findViewById(R.id.feedback_textview);
            BAL_PERF_TV = itemView.findViewById(R.id.balperf_textview);
            BAL_DEV_TV = itemView.findViewById(R.id.baldev_textview);
            RESP_TIME_TV = itemView.findViewById(R.id.resptime_textview);
        }
    }
}