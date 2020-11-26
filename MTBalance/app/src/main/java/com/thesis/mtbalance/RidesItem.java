package com.thesis.mtbalance;

/**
 * Item class for rides data.
 */
public class RidesItem {

    /* Finals */
    // Not used in the view, but for the intent to the plot activity
    private final String START_TIME;

    private final String COMPLETION_TIME;
    private final String DATE_TIME;

    private final String FEEDBACK_METHOD;
    private final String BALANCE_PERFORMANCE;
    private final String BALANCE_DEVIATION;
    private final String RESPONSE_TIME;

    /**
     * Constructor to set all data.
     *
     * @param startTime          - the start time of the ride.
     * @param completionTime     - the time it took to complete the ride (min).
     * @param dateTime           - the date + time of the ride.
     * @param feedbackMethod     - the feedback method used during the ride.
     * @param balancePerformance - the balance performance of the ride.
     * @param balanceDeviation   - the average balance deviation during the ride (cm)
     * @param responseTime       - the average response time during the ride (ms).
     */
    public RidesItem(String startTime, String completionTime, String dateTime, String feedbackMethod,
                     String balancePerformance, String balanceDeviation, String responseTime) {
        START_TIME = startTime;
        COMPLETION_TIME = completionTime;
        DATE_TIME = dateTime;
        FEEDBACK_METHOD = feedbackMethod;
        BALANCE_PERFORMANCE = balancePerformance;
        BALANCE_DEVIATION = balanceDeviation;
        RESPONSE_TIME = responseTime;
    }

    // region Getters
    public String getStartTime() {
        return START_TIME;
    }

    public String getCompletionTime() {
        return COMPLETION_TIME;
    }

    public String getDateTime() {
        return DATE_TIME;
    }

    public String getFeedbackMethod() {
        return FEEDBACK_METHOD;
    }

    public String getBalancePerformance() {
        return BALANCE_PERFORMANCE;
    }

    public String getBalanceDeviation() {
        return BALANCE_DEVIATION;
    }

    public String getResponseTime() {
        return RESPONSE_TIME;
    }
    // endregion
}
