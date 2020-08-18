package com.thesis.mtbalance;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCb;
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCb;
import com.xsens.dot.android.sdk.models.XsPayload;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.utils.XsensDotScanner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MeasuringActivity extends AppCompatActivity
        implements XsensDotDeviceCb, XsensDotScannerCb {

    // region Variables
    // Finals
    private final int ALL_DOTS = 3;

    // Floats
    private float mAnkleLength, mKneeLength;

    // Booleans
    private boolean mMeasuring = false;

    // Views
    private LinearLayout mMeasuringLayout;
    private Chronometer mChronometer;

    // Instants
    Instant mStartTime;

    // Helpers
    private VecHelper mVecHelper;

    // Xsens
    private XsensDotScanner mDotScanner;
    private ArrayList<XsensDotDevice> mDotList = new ArrayList<>();

    // Hashmaps
    private HashMap<String, String> mAddressTagMap = new HashMap<>();
    private HashMap<String, float[]> mTagQuatMap = new HashMap<>();
    // endregion

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState - state holding saved info from onPause callback.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Initialize helper
        mVecHelper = new VecHelper(this);

        // Get the shared preferences
        retrieveSharedPreferences();

        // Initialize the SDK
        initXsensSdk();
    }

    /**
     * Retrieves the used shared preferences.
     */
    private void retrieveSharedPreferences() {
        // Create a shared preferences object and get the leg lengths
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mAnkleLength = Float.parseFloat(Objects.requireNonNull(sharedPref.getString
                (SettingsActivity.KEY_LOWER_LEG_DIMENSION, "0")));
        mKneeLength = Float.parseFloat(Objects.requireNonNull(sharedPref.getString
                (SettingsActivity.KEY_UPPER_LEG_DIMENSION, "0")));
    }

    /**
     * Initializes the DOT SDK and starts a scan.
     */
    private void initXsensSdk() {
        // Set global SDK options
        XsensDotSdk.setDebugEnabled(true);  // Todo: remove debugger when application is finished.
        XsensDotSdk.setReconnectEnabled(true);

        // Initialize views
        mMeasuringLayout = findViewById(R.id.measuring_layout);
        mChronometer = findViewById(R.id.chronometer);

        // Initialize scanner object and start scan
        mDotScanner = new XsensDotScanner(getApplicationContext(), this);
        mDotScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        mDotScanner.startScan();
    }

    /**
     * Callback function which triggers when a DOT is scanned.
     *
     * @param bluetoothDevice - the currently scanned DOT.
     */
    @Override
    public void onXsensDotScanned(BluetoothDevice bluetoothDevice) {
        // Get the address, return if the device is already initialized
        String address = bluetoothDevice.getAddress();
        if (mAddressTagMap.containsKey(address))
            return;

        // Add the current address to the address-tag hashmap, depending on MAC-address
        switch (address) {
            case "D4:CA:6E:F1:7D:D9":   // Bike
                mAddressTagMap.put(address, "Bike DOT");
                break;
            case "D4:CA:6E:F1:66:AA":   // Ankle
                mAddressTagMap.put(address, "Ankle DOT");
                break;
            case "D4:CA:6E:F1:63:D0":   // Knee
                mAddressTagMap.put(address, "Knee DOT");
                break;
            default:
                return;
        }

        // Initialize address with a base quaternion
        mTagQuatMap.put(mAddressTagMap.get(address), new float[]{1f, 0f, 0f, 0f});

        // Create a new DOT object and add it to the list of DOT objects
        // Todo: check if 'this' is fine instead of MeasuringActivity.this
        XsensDotDevice dot = new XsensDotDevice(getApplicationContext(),
                bluetoothDevice, this);
        dot.connect();
        mDotList.add(dot);

        // Stop the scan if all the DOTs are initialized
        if (mDotList.size() == ALL_DOTS) {
            mDotScanner.stopScan();

            // Notify user
            Snackbar.make(mMeasuringLayout, "Connected all DOTs.",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Toggles the measuring state of the DOTs.
     *
     * @param view - the button calling the function.
     */
    public void toggleMeasuring(View view) {
        // Return if not all the DOTs are initialized.
        if (mDotList.size() != ALL_DOTS)
            return;

        // If not measuring, start measuring
        if (!mMeasuring) {
            // Set measuring to true and change the icon of the view
            mMeasuring = true;
            ((ImageButton) view).setImageResource(R.drawable.ic_stop);

            // Initialize every dot with the quaternion measurement mode
            for (XsensDotDevice dot : mDotList) {
                dot.setMeasurementMode(XsPayload.PAYLOAD_TYPE_ORIENTATION_QUATERNION);
                dot.startMeasuring();
            }

            // Start the clock and the chronometer
            mStartTime = Instant.now();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();

            // Notify user
            Snackbar.make(mMeasuringLayout, "Started measuring.",
                    Snackbar.LENGTH_LONG).show();
        }

        // If measuring, stop measuring
        else {
            // Set measuring to false and change the icon of the view
            mMeasuring = false;
            ((ImageButton) view).setImageResource(R.drawable.ic_play);

            // Stop the measuring for every DOT
            for (XsensDotDevice dot : mDotList)
                dot.stopMeasuring();

            // Stop the clock and the chronometer
            Instant endTime = Instant.now();
            mChronometer.stop();

            // Todo: get completionTime, save DVs to rides.txt and measurement data to startTime.txt
            // Todo: notify user
        }
    }

    /**
     * Starts the calibration of the DOTs.
     *
     * @param view - the button calling the function.
     */
    public void startCalibration(View view) {
        // Return if not all the DOTs are initialized or not currently measuring.
        if (mDotList.size() != ALL_DOTS || !mMeasuring)
            return;

        // Calibrate all DOTs
        for (XsensDotDevice dot : mDotList)
            dot.resetHeading();

        // Notify user
        Snackbar.make(mMeasuringLayout, "Calibrated DOTs.",
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * Callback function which is triggered when values in a DOT change.
     *
     * @param address      - the MAC address of the DOT.
     * @param xsensDotData - the data object tied to the DOT.
     */
    @Override
    public void onXsensDotDataChanged(String address, XsensDotData xsensDotData) {
        // Get the current tag and map the current quaternion to it
        String currTag = mAddressTagMap.get(address);
        mTagQuatMap.put(currTag, xsensDotData.getQuat());

        // If the current tag is the Bike DOT, calculate the balance
        if (currTag != null && currTag.equals("Bike DOT"))
            calculateBalance();
    }

    /**
     * Calculates the balance given the current quaternion values.
     */
    private void calculateBalance() {
        // Calculate the bike vector and mirror it to get the optimal balance direction
        float[] bikeVector = mVecHelper.quatRotation
                (Objects.requireNonNull(mTagQuatMap.get("Bike DOT")), 100f);
        bikeVector = mVecHelper.mirrorVector(bikeVector, false, 0f);

        // Calculate the ankle vector and knee vector
        float[] ankleVector = mVecHelper.quatRotation
                (Objects.requireNonNull(mTagQuatMap.get("Ankle DOT")), mAnkleLength);
        float[] kneeVector = mVecHelper.quatRotation
                (Objects.requireNonNull(mTagQuatMap.get("Knee DOT")), mKneeLength);

        // Calculate the position of the current balance (end effector)
        float[] endEffector = mVecHelper.getEndEffector(ankleVector, kneeVector);
    }

    // region Unused
    @Override
    public void onXsensDotConnectionChanged(String s, int i) {

    }

    @Override
    public void onXsensDotServicesDiscovered(String s, int i) {

    }

    @Override
    public void onXsensDotFirmwareVersionRead(String s, String s1) {

    }

    @Override
    public void onXsensDotTagChanged(String s, String s1) {

    }

    @Override
    public void onXsensDotBatteryChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotCalibrationResult(String s, int i, int i1, int i2) {

    }

    @Override
    public void onXsensDotOtaChecked(String s, boolean b, String s1, String s2) {

    }

    @Override
    public void onXsensDotOtaRollback(String s, boolean b, String s1, String s2) {

    }

    @Override
    public void onXsensDotOtaFileMismatch(String s) {

    }

    @Override
    public void onXsensDotOtaDownloaded(String s, int i) {

    }

    @Override
    public void onXsensDotOtaUpdated(String s, int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public void onXsensDotNewFirmwareVersion(String s, boolean b, String s1, String s2) {

    }

    @Override
    public void onXsensDotOtaDischarge(String s) {

    }
    // endregion
}