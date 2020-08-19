package com.thesis.mtbalance;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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

    /**
     * Saves an arrayList to the files directory, given the filename.
     *
     * @param fileName - the name of the new file.
     * @param data     - the data to write to the file.
     * @param context  - the application context.
     */
    public void saveArrayData(String fileName, ArrayList<String> data, Context context) {
        try {
            // Get the directory of the file
            File fileDir = new File(context.getExternalFilesDir
                    (null) + "/" + fileName + ".txt");

            // Open new output streams and write the array to the file
            FileOutputStream fos = new FileOutputStream(fileDir);
            ObjectOutputStream oot = new ObjectOutputStream(fos);
            oot.writeObject(data);
            oot.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an arrayList from the files directory, given the filename.
     *
     * @param fileName - the name of the file to load.
     * @param context  - the application context.
     * @return an arrayList converted from the files directory.
     */
    public ArrayList<String> loadArrayData(String fileName, Context context) {
        // Initialize a new arrayList
        ArrayList<String> data = null;

        try {
            // Get the directory of the file
            File fileDir = new File(context.getExternalFilesDir
                    (null) + "/" + fileName + ".txt");

            // Open new input streams and write the file to the array
            FileInputStream fis = new FileInputStream(fileDir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
}
