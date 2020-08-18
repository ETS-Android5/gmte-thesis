package com.thesis.mtbalance;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Objects;

/**
 * Helper class for 3D vector math.
 */
public class VecHelper {

    /* Variables */
    private float[] mSensorOffset, mHipOffset;

    /**
     * Creates helper and retrieves the used shared preferences.
     *
     * @param context - the current application context.
     */
    public VecHelper(Context context) {
        // Create a fileHelper object for helper methods
        FileHelper fileHelper = new FileHelper();

        // Create a shared preferences object and get the offsets
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mSensorOffset = fileHelper.stringToFloatArray(Objects.requireNonNull
                (sharedPref.getString(SettingsActivity.KEY_OFFSET_DIMENSION, "0,0,0")));
        mHipOffset = fileHelper.stringToFloatArray(Objects.requireNonNull
                (sharedPref.getString(SettingsActivity.KEY_HIP_DIMENSION, "0,0,0")));
    }

    /**
     * Rotates a 3D vector given a quaternion.
     *
     * @param quat      - the quaternion to rotate with.
     * @param dimension - the length of the vector to rotate (rotation around point).
     */
    public float[] quatRotation(float[] quat, float dimension) {
        // Make a 4-dimensional point and the conjugate needed for rotation
        float[] point = {0f, dimension, 0f, 0f};
        float[] conj = {quat[0], -quat[1], -quat[2], -quat[3]};

        // Calculate the result and copy it to the output vector
        float[] result = hamiltonProduct(hamiltonProduct(quat, point), conj);
        return new float[]{result[1], result[2], result[3]};
    }

    /**
     * Returns the hamilton product of two 4-dimensional vectors.
     *
     * @param p - the first vector.
     * @param q - the second vector.
     * @return the product vector of the hamilton product.
     */
    private float[] hamiltonProduct(float[] p, float[] q) {
        // Returns the Hamilton product - https://en.wikipedia.org/wiki/Quaternion#Hamilton_product
        return new float[]
                {p[0] * q[0] - p[1] * q[1] - p[2] * q[2] - p[3] * q[3],
                        p[0] * q[1] + p[1] * q[0] + p[2] * q[3] - p[3] * q[2],  // i
                        p[0] * q[2] - p[1] * q[3] + p[2] * q[0] + p[3] * q[1],  // j
                        p[0] * q[3] + p[1] * q[2] - p[2] * q[1] + p[3] * q[0]}; // k
    }

    /**
     * Mirrors a vector in the z-axis, optionally correcting the slope.
     *
     * @param vec        - the vector to mirror.
     * @param correction - whether to correct the slope or not.
     * @param magnitude  - the magnitude of the correction.
     * @return a mirrored, possibly corrected vector.
     */
    public float[] mirrorVector(float[] vec, boolean correction, float magnitude) {
        // Correct the x- and y-axes with the magnitude if correction is enabled
        if (correction) {
            vec[0] *= magnitude;
            vec[1] *= magnitude;
        }

        // Invert the x- and y-axes to mirror the vector through the z-axis
        vec[0] = -vec[0];
        vec[1] = -vec[1];

        return vec;
    }

    /**
     * Returns the current balance (end effector) given the ankle and knee vector.
     *
     * @param ankleVec - the current ankle vector.
     * @param kneeVec  - the current knee vector.
     * @return the position of the end effector, which is the current balance.
     */
    public float[] getEndEffector(float[] ankleVec, float[] kneeVec) {
        // Create a new float array and pairwise add all the other vectors
        float[] endEffector = new float[3];
        for (int i = 0; i < 3; i++)
            endEffector[i] = mSensorOffset[i] + ankleVec[i] + kneeVec[i] + mHipOffset[i];

        return endEffector;
    }
}
