package com.thesis.mtbalance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

public class TestingActivity extends AppCompatActivity {

    /* Finals */
    // UUIDs
    private final UUID SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private final UUID CHAR_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");

    // Feedback
    private String mFeedbackMethod;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattCharacteristic mCharacteristic = null;

    /**
     * Callback variable for BLE connections.
     * Handles various callbacks during the connection phase.
     */
    private final BluetoothGattCallback GATT_CALLBACK = new BluetoothGattCallback() {
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

    /**
     * Called on activity creation.
     *
     * @param savedInstanceState - state holding saved info from onPause callback.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        // Create a shared preferences object and get the chosen feedback method
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mFeedbackMethod = sharedPref.getString(SettingsActivity.KEY_PREFERRED_FEEDBACK, "0");

        // Set the text to reflect the current real-time feedback mode
        TextView testingTV = findViewById(R.id.testing_textview);
        testingTV.setText(getResources().getStringArray(R.array.feedback_method_entries)
                [Integer.parseInt(mFeedbackMethod)]);

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
        feedbackDevice.connectGatt(this, true, GATT_CALLBACK);
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
            // "Shut down" the real-time feedback by sending a neutral command and cleanup BLE
            if (mBluetoothGatt != null) {
                writeFeedback("x");
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }

            // Destroy the activity to prevent data leakage
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Writes the output string to the connected BLE device.
     *
     * @param direction - the feedback direction to pass to the BLE, as a string.
     */
    private void writeFeedback(String direction) {
        String output = mFeedbackMethod + direction + ",";
        mCharacteristic.setValue(output);
        mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    // region Directions
    public void feedbackCenter(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("x");
    }

    public void feedbackFront(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("0");
    }

    public void feedbackFrontRight(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("1");
    }

    public void feedbackRight(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("2");
    }

    public void feedbackBackRight(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("3");
    }

    public void feedbackBack(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("4");
    }

    public void feedbackBackLeft(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("5");
    }

    public void feedbackLeft(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("6");
    }

    public void feedbackFrontLeft(View view) {
        if (mBluetoothGatt != null)
            writeFeedback("7");
    }
    // endregion
}
