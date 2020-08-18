package com.thesis.mtbalance;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCb;
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCb;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.utils.XsensDotScanner;

import java.util.ArrayList;
import java.util.HashMap;

public class MeasuringActivity extends AppCompatActivity
        implements XsensDotDeviceCb, XsensDotScannerCb {

    /* Variables */
    // Finals
    private final int ALL_DOTS = 3;

    // Xsens
    private XsensDotScanner mDotScanner;
    private ArrayList<XsensDotDevice> mDotList = new ArrayList<>();

    // Hashmaps
    private HashMap<String, String> mAddressTagMap = new HashMap<>();
    private HashMap<String, float[]> mTagQuatMap = new HashMap<>();

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState - state holding saved info from onPause callback.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Initialize the SDK
        initXsensSdk();
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
            Toast.makeText(getApplicationContext(), "Connected all DOTs!",
                    Toast.LENGTH_SHORT).show();
        }
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
    public void onXsensDotDataChanged(String s, XsensDotData xsensDotData) {

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