package com.thesis.mtbalance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.models.XsensDotPayload;
import com.xsens.dot.android.sdk.utils.XsensDotScanner;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MeasuringActivity extends AppCompatActivity
        implements XsensDotDeviceCallback, XsensDotScannerCallback {

    // region Variables
    // Finals
    private final int ALL_DOTS = 4;

    private final UUID SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private final UUID CHAR_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");

    // Numerical / Strings
    private String mParticipantNumber;
    private String mFeedbackMethod;
    private String mFeedbackString;

    private int mIteration = 0;
    private float mThresholdLeniency;
    private float mAnkleLength, mKneeLength;
    private float[] mSensorOffset, mHipOffset;

    // DVs
    private float mBalancePerformance = 0f;
    private float mBalanceDeviation = 0f;
    private float mResponseTime = 0f;

    // Booleans
    private boolean mAllConnected = false;
    private boolean mMeasuring = false;
    private boolean mBalanced = true;

    // Views
    private LinearLayout mMeasuringLayout;
    private Chronometer mChronometer;

    // Instants
    private Instant mStartTime, mEndTime;
    private Instant mStartBalanceTime, mEndBalanceTime;

    // Helpers
    private VecHelper mVecHelper;
    private FileHelper mFileHelper;

    // Containers
    private HashMap<String, String> mAddressTagMap = new HashMap<>();
    private HashMap<String, float[]> mTagQuatMap = new HashMap<>();
    private ArrayList<String> mBalanceData = new ArrayList<>();

    // Xsens
    private XsensDotScanner mDotScanner;
    private ArrayList<XsensDotDevice> mDotList = new ArrayList<>();

    // Feedback
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattCharacteristic mCharacteristic = null;

    /**
     * Callback variable for BLE connections.
     * Handles various callbacks during the connection phase.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // Handles initial connection to remote BLE device
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            // Discover the services if the initial connection was successful
            if (status == BluetoothGatt.GATT_SUCCESS &&
                    newState == BluetoothProfile.STATE_CONNECTED)
                gatt.discoverServices();
        }

        // Finishes up the BLE connection by locating the remote service and characteristic
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            // Get the service and characteristic to communicate with the remote BLE device
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatt = gatt;
                BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
                mCharacteristic = service.getCharacteristic(CHAR_UUID);
                mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            }
        }
    };
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

        // Initialize the views and helpers
        initViewHelpers();

        // Get the shared preferences
        retrieveSharedPreferences();

        // Initialize the feedback
        initFeedback();

        // Initialize the SDK
        initXsensSdk();
    }

    /**
     * Retrieves the used shared preferences.
     */
    private void retrieveSharedPreferences() {
        // Create a shared preferences object
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the participant number, preferred feedback method and initialize feedback string
        mParticipantNumber = sharedPref.getString(SettingsActivity.KEY_PARTICIPANT_NUMBER, "0");
        mFeedbackMethod = sharedPref.getString(SettingsActivity.KEY_PREFERRED_FEEDBACK, "0");
        mFeedbackString = "";

        // Show the testing label if the application is in testing mode
        if (mParticipantNumber.equals("0"))
            findViewById(R.id.testing_textview).setVisibility(View.VISIBLE);

        // Get the preferred threshold leniency
        mThresholdLeniency = Float.parseFloat(Objects.requireNonNull(sharedPref.getString
                (SettingsActivity.KEY_THRESHOLD_LENIENCY, "0")));

        // Get the leg lengths
        mAnkleLength = Float.parseFloat(Objects.requireNonNull(sharedPref.getString
                (SettingsActivity.KEY_LOWER_LEG_LENGTH, "0")));
        mKneeLength = Float.parseFloat(Objects.requireNonNull(sharedPref.getString
                (SettingsActivity.KEY_UPPER_LEG_LENGTH, "0")));

        // Get the offsets
        mSensorOffset = mFileHelper.stringToFloatArray(Objects.requireNonNull
                (sharedPref.getString(SettingsActivity.KEY_OFFSET_DIMENSION, "0,0,0")));
        mHipOffset = mFileHelper.stringToFloatArray(Objects.requireNonNull
                (sharedPref.getString(SettingsActivity.KEY_HIP_DIMENSION, "0,0,0")));
    }

    /**
     * Initializes the view and the helper classes.
     */
    private void initViewHelpers() {
        // Initialize helpers
        mVecHelper = new VecHelper();
        mFileHelper = new FileHelper();

        // Initialize views
        mMeasuringLayout = findViewById(R.id.measuring_layout);
        mChronometer = findViewById(R.id.chronometer);
    }

    /**
     * Initializes the chosen feedback device.
     * Connects to the BLE device, and sets up a writeable characteristic for communication.
     */
    private void initFeedback() {
        // If the feedback is set to application, return
        if (mFeedbackMethod.equals("0"))
            return;

        // Set the BLE address depending on the chosen feedback method
        final String beltAddr = "B0:7E:11:F6:50:9C";
        final String helmAddr = "90:E2:02:1C:4E:8B";
        String bleAddr = mFeedbackMethod.equals("1") ? beltAddr : helmAddr;

        // Setup the BLE and get the remote feedback device
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        BluetoothDevice feedbackDevice = btAdapter.getRemoteDevice(bleAddr);

        // Connect to the remote feedback device via a generic attribute connection
        feedbackDevice.connectGatt(this, true, mGattCallback);
    }

    /**
     * Initializes the DOT SDK and starts a scan.
     */
    private void initXsensSdk() {
        // Set global SDK options
        XsensDotSdk.setDebugEnabled(true);  // Todo: remove debugger when application is finished.
        XsensDotSdk.setReconnectEnabled(true);

        // Initialize scanner object and start scan
        mDotScanner = new XsensDotScanner(getApplicationContext(), this);
        mDotScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        mDotScanner.startScan();

        // Notify user
        Snackbar.make(mMeasuringLayout, "Connecting DOTs...",
                Snackbar.LENGTH_INDEFINITE).show();
    }

    /**
     * Called when a menu button is pressed.
     *
     * @param item - the item that is pressed.
     * @return the state of the method execution.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check if the up button is pressed
        if (item.getItemId() == android.R.id.home) {
            // Cleanup the BLE sensors
            cleanupBLE();

            // Destroy the activity to prevent data leakage
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback function which triggers when a DOT is scanned.
     *
     * @param bluetoothDevice - the currently scanned DOT.
     */
    @Override
    public void onXsensDotScanned(BluetoothDevice bluetoothDevice) {
        // Get the address, return if all or the current device is already initialized
        String address = bluetoothDevice.getAddress();
        if (mAddressTagMap.size() == ALL_DOTS || mAddressTagMap.containsKey(address))
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
            case "D4:CA:6E:F1:63:C4":   // Yaw
                mAddressTagMap.put(address, "Yaw DOT");
                break;
            default:
                return;
        }

        // Initialize address with a base quaternion
        mTagQuatMap.put(mAddressTagMap.get(address), new float[]{1f, 0f, 0f, 0f});

        // Create a new DOT object and add it to the list of DOT objects
        XsensDotDevice dot = new XsensDotDevice(getApplicationContext(),
                bluetoothDevice, this);
        dot.connect();
        mDotList.add(dot);
    }

    /**
     * Callback function which is triggered after the device is successfully connected.
     *
     * @param address - the address of the current device.
     */
    @Override
    public void onXsensDotInitDone(String address) {
        // Up the iteration after a successful connection
        mIteration++;

        // Stop the scan if all dots are initialized successfully set the flag to true
        if (mIteration == ALL_DOTS) {
            mIteration = 0;
            mDotScanner.stopScan();

            mAllConnected = true;

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
        if (!mAllConnected)
            return;

        // If not measuring, start measuring
        if (!mMeasuring) {
            // Set measuring to true and change the icon of the view
            mMeasuring = true;
            ((ImageButton) view).setImageResource(R.drawable.ic_stop);

            // Start the clocks and the chronometer
            mStartTime = Instant.now();
            mStartBalanceTime = Instant.now();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();

            // Add an initial point to the measurement data
            mBalanceData.add("0,0,0");

            // Initialize every dot with the quaternion measurement mode and start measuring
            for (XsensDotDevice dot : mDotList) {
                dot.setMeasurementMode(XsensDotPayload.PAYLOAD_TYPE_ORIENTATION_QUATERNION);
                dot.startMeasuring();
            }

            // Notify user
            Snackbar.make(mMeasuringLayout, "Started measuring.",
                    Snackbar.LENGTH_LONG).show();
        }

        // If measuring, stop measuring
        else {
            // Set measuring to false and change the icon of the view
            mMeasuring = false;
            ((ImageButton) view).setImageResource(R.drawable.ic_play);

            // Stop the clock and the chronometer
            mEndTime = Instant.now();
            mChronometer.stop();

            // Get the instant of stop/start balance depending on last balance state
            if (!mBalanced)
                mStartBalanceTime = Instant.now();
            if (mBalanced)
                mEndBalanceTime = Instant.now();

            // Cleanup the BLE sensors
            cleanupBLE();

            // Only save when testing mode is not activated
            if (!mParticipantNumber.equals("0")) {
                // Finalize the DVs and format to a string, then save it to the rides file
                String dataDVS = finalizeDVS();
                mFileHelper.appendToFile("rides", dataDVS, this);

                // Save balance data to an unique file for post-hoc application
                mFileHelper.saveArrayData(mStartTime.toString(), mBalanceData, this);

                // Notify user
                Snackbar.make(mMeasuringLayout, "Stopped measuring and saved data.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                // Notify user
                Snackbar.make(mMeasuringLayout, "Stopped testing mode.",
                        Snackbar.LENGTH_LONG).show();
            }

            // Return to MainActivity after waiting for 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    // Destroy the activity to prevent data leakage
                    finish();
                }
            }, 3000);
        }
    }

    /**
     * Cleans up all the BLE sensors upon activity destruction.
     */
    private void cleanupBLE() {
        // Cleanup the BLE instance if one exists
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        // Stop the scan if one is running
        mDotScanner.stopScan();

        // Stop the measuring for every DOT and disconnect it
        for (XsensDotDevice dot : mDotList) {
            dot.stopMeasuring();
            dot.disconnect();
        }

        // Clear the DOT list and set the connection state to false
        mDotList.clear();
        mAllConnected = false;
    }

    /**
     * Finalizes the dependent variables and turns the data into a string format.
     *
     * @return The string format denoting the following variables, in this order:
     * - General Variables
     * 1. Start time;
     * 2. Participant number;
     * 3. Feedback method;
     * 4. DateTime;
     * - DVS
     * 5. Balance performance;
     * 6. Balance deviation;
     * 7. Response time;
     * 8. Completion time.
     */
    private String finalizeDVS() {
        // Get the dateTime using a formatter
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault());
        String dateTime = formatter.format(mStartTime);

        // Calculate the completion time
        float completionTime = Duration.between(mStartTime, mEndTime).toMillis();

        // Update DVs depending on final balance state
        if (!mBalanced) {
            float currResponseTime = Duration.between
                    (mEndBalanceTime, mStartBalanceTime).toMillis();
            mResponseTime = getCMA(mResponseTime, currResponseTime);
        }
        if (mBalanced) {
            float currBalanceTime = Duration.between
                    (mStartBalanceTime, mEndBalanceTime).toMillis();
            mBalancePerformance += currBalanceTime;
        }

        // Turn the balance performance into a percentage and completion time to seconds
        mBalancePerformance = mBalancePerformance / completionTime * 100f;
        completionTime = completionTime * 0.001f;

        // Build the data string according to the javadoc
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                mStartTime.toString(), mParticipantNumber,
                mFeedbackMethod, dateTime,
                mBalancePerformance, mBalanceDeviation,
                mResponseTime, completionTime);
    }

    /**
     * Starts the calibration of the DOTs.
     *
     * @param view - the button calling the function.
     */
    public void startCalibration(View view) {
        // Return if not all the DOTs are initialized or not currently measuring.
        if (!mAllConnected || !mMeasuring)
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
        // Get the yaw correction matrix to correct the sensors to a shared local frame
        float[][] yawCorrMatrix = mVecHelper.yawCorrectionMatrix(
                Objects.requireNonNull(mTagQuatMap.get("Yaw DOT")));

        // Calculate the bike vector and mirror it to get the optimal balance direction
        float[] bikeVector = mVecHelper.quatRotation(yawCorrMatrix,
                Objects.requireNonNull(mTagQuatMap.get("Bike DOT")), 1000f);
        bikeVector = mVecHelper.mirrorVector(bikeVector, false, 0f);

        // Calculate the ankle vector and knee vector
        float[] ankleVector = mVecHelper.quatRotation(yawCorrMatrix,
                Objects.requireNonNull(mTagQuatMap.get("Ankle DOT")), mAnkleLength);
        float[] kneeVector = mVecHelper.quatRotation(yawCorrMatrix,
                Objects.requireNonNull(mTagQuatMap.get("Knee DOT")), mKneeLength);

        // Calculate the position of the current balance (end effector)
        float[] endEffector = mVecHelper.getEndEffector(
                mSensorOffset, ankleVector, kneeVector, mHipOffset);

        // Get the intersection between current and optimal balance
        float[] intersection = mVecHelper.getIntersection(bikeVector, endEffector);

        // Get the distance between the intersection and end effector
        float distance = mVecHelper.getDistance(endEffector, intersection);

        // Get the flattened balance difference between the current and optimal balance
        float[] balanceDifference = mVecHelper.getBalanceDifference(endEffector, intersection);

        // Update the real-time feedback if it is used
        if (!mFeedbackMethod.equals("0"))
            updateFeedback(distance, balanceDifference);

        // Update the DVs
        updateDVS(distance);

        // Update the balance data for the post-hoc application
        updateBalanceData(balanceDifference);

        // Update the iteration
        mIteration++;
    }

    /**
     * Updates the real-time feedback given the current distance and balance difference.
     *
     * @param distance          - the current distance from the current to the optimal balance point.
     * @param balanceDifference - the difference in balance, calibrated to origin and in 2d.
     */
    private void updateFeedback(float distance, float[] balanceDifference) {
        // Stop providing feedback if the user is within the balance threshold
        if (distance <= mThresholdLeniency) {
            writeFeedback(mFeedbackMethod + "x" + ",");
            return;
        }

        // Calculate the angle between the balance vector and a vertical reference vector
        // If x is negative, flip the sign of the angle + add 180 to set reference downwards
        float[] dirVec = {balanceDifference[0], balanceDifference[1], 0f};
        float angle = mVecHelper.getAngle(new float[]{0f, 1f, 0f}, dirVec);
        if (dirVec[0] < 0)
            angle = -angle;
        angle += 180f;

        // get the direction vector based on the angle
        // starting down, clockward rotation
        int direction = Math.round(angle / 45f);

        // Direction 8 is equal to direction 0 (full circle)
        if (direction == 8)
            direction = 0;

        // Write the feedback to the connected BLE device
        writeFeedback(mFeedbackMethod + direction + ",");
    }

    /**
     * Writes the output string to the connected BLE device.
     *
     * @param output - the output to pass to the BLE, as a string.
     */
    private void writeFeedback(String output) {
        // Only send the data if it updates the current feedback direction
        if (!mFeedbackString.equals(output)) {
            mFeedbackString = output;
            mCharacteristic.setValue(mFeedbackString);
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
        }
    }

    /**
     * Updates the dependent variables.
     *
     * @param distance - the current distance from the current to the optimal balance point.
     */
    private void updateDVS(float distance) {
        // Update the average balance deviation
        float currDeviation = (distance - mThresholdLeniency > 0) ?
                distance - mThresholdLeniency : 0f;
        mBalanceDeviation = getCMA(mBalanceDeviation, currDeviation);

        // Execute if the previous state was unbalanced and the user is currently balanced
        if (!mBalanced && distance <= mThresholdLeniency) {
            // Set the balance flag to true, and start the balance time
            mBalanced = true;
            mStartBalanceTime = Instant.now();

            // Calculate the current response time and update the average response time
            float currResponseTime = Duration.between
                    (mEndBalanceTime, mStartBalanceTime).toMillis();
            mResponseTime = getCMA(mResponseTime, currResponseTime);
        }

        // Execute if the previous state was balanced and the user is currently unbalanced
        if (mBalanced && distance > mThresholdLeniency) {
            // Set the balance flag to false, and start the end balance time
            mBalanced = false;
            mEndBalanceTime = Instant.now();

            // Calculate the current balance time and update the total balance time.
            float currBalanceTime = Duration.between
                    (mStartBalanceTime, mEndBalanceTime).toMillis();
            mBalancePerformance += currBalanceTime;
        }
    }

    /**
     * Calculates the cumulative moving average.
     *
     * @param currAverage - the current average.
     * @param val         - the new value to add to the average.
     * @return an updated moving average.
     */
    private float getCMA(float currAverage, float val) {
        // Calculates the cumulative moving average - https://en.wikipedia.org/wiki/Moving_average
        float numerator = mIteration * currAverage + val;
        return numerator / (mIteration + 1);
    }

    /**
     * Updates the balance data for the post-hoc application.
     *
     * @param balanceDifference - the difference in balance, calibrated to origin and in 2d.
     */
    private void updateBalanceData(float[] balanceDifference) {
        // Get the current time and update the stepTime
        Instant now = Instant.now();
        long elapsedTime = Duration.between(mStartTime, now).toMillis();

        // Add the current elapsedTime and balanceDifference to the list
        float elapsedTimeSeconds = elapsedTime * 0.001f;
        mBalanceData.add(String.format("%s,%s,%s", elapsedTimeSeconds,
                balanceDifference[0], balanceDifference[1]));
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
    public void onXsensDotPowerSavingTriggered(String s) {
    }
    // endregion
}