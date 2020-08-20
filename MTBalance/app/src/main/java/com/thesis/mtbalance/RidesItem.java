package com.thesis.mtbalance;

/**
 * Item class for rides data.
 */
public class RidesItem {

    /* Variables */
    private String mCompletionTime;
    private String mDateTime;

    private String mFeedbackMethod;
    private String mBalancePerformance;
    private String mBalanceDeviation;
    private String mResponseTime;

    /**
     * Constructor to set all data.
     *
     * @param completionTime     - the time it took to complete the ride (min).
     * @param dateTime           - the date + time of the ride.
     * @param feedbackMethod     - the feedback method used during the ride.
     * @param balancePerformance - the balance performance of the ride.
     * @param balanceDeviation   - the average balance deviation during the ride (cm)
     * @param responseTime       - the average response time during the ride (ms).
     */
    public RidesItem(String completionTime, String dateTime, String feedbackMethod,
                     String balancePerformance, String balanceDeviation, String responseTime) {
        mCompletionTime = completionTime;
        mDateTime = dateTime;
        mFeedbackMethod = feedbackMethod;
        mBalancePerformance = balancePerformance;
        mBalanceDeviation = balanceDeviation;
        mResponseTime = responseTime;
    }

    // region Getters
    public String getCompletionTime() {
        return mCompletionTime;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public String getFeedbackMethod() {
        return mFeedbackMethod;
    }

    public String getBalancePerformance() {
        return mBalancePerformance;
    }

    public String getBalanceDeviation() {
        return mBalanceDeviation;
    }

    public String getResponseTime() {
        return mResponseTime;
    }
    // endregion
}
