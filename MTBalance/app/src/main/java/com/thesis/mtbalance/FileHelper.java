package com.thesis.mtbalance;

/**
 * Helper class for file related methods.
 */
public class FileHelper {

    public FileHelper() {
    }

    /**
     * Converts a comma separated string to an array of floats.
     *
     * @param input - the string to convert.
     * @return the converted float array.
     */
    public float[] stringToFloatArray(String input) {
        // Split the input into separate elements
        String[] inputArray = input.split(",");

        // Convert every element in the string array to the float array
        float[] result = new float[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
            result[i] = Float.parseFloat(inputArray[i]);

        return result;
    }
}
