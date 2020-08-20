package com.thesis.mtbalance;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
     * Appends a line of data to a file in the files directory.
     * Creates a file when the filename does not yes exist.
     *
     * @param fileName - the name of the file.
     * @param data     - the data to append.
     * @param context  - the application context.
     */
    public void appendToFile(String fileName, String data, Context context) {
        try {
            // Get the directory of the file
            File fileDir = new File(context.getExternalFilesDir
                    (null) + "/" + fileName + ".txt");

            // Open a new output stream and append the data
            FileOutputStream fos = new FileOutputStream(fileDir, true);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a file into an arrayList from the files directory.
     *
     * @param fileName - the filename to load.
     * @param context  - the application context.
     * @return An arrayList build from the strings present in the file.
     */
    public ArrayList<String> loadFromFile(String fileName, Context context) {
        // Initialize a new arrayList
        ArrayList<String> data = new ArrayList<>();

        try {
            // Get the directory of the file
            File fileDir = new File(context.getExternalFilesDir
                    (null) + "/" + fileName + ".txt");

            // Open a new input stream and readers
            FileInputStream fis = new FileInputStream(fileDir);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            // While there are lines of data, append to the arrayList
            String line;
            while ((line = br.readLine()) != null)
                data.add(line);

            // Close the input stream and readers
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
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
        ArrayList<String> data = new ArrayList<>();

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
