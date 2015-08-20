package uk.co.wehive.hive.persistence;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import uk.co.wehive.hive.entities.response.HiveResponse;

public class PersistenceHelper {

    private static final String jsonFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    private final String TAG = "PersistenceHelper";

    public static void saveResponse(HiveResponse response, String fileName) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        try {
            FileWriter Filewriter = new FileWriter(jsonFilePath + fileName);
            Filewriter.write(json);
            Filewriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    public static <T> T readResponse(String fileName, Class<T> type) {
        Gson gson = new Gson();
        T response = null;
        try {
            FileReader fileReader = new FileReader(jsonFilePath + fileName);
            BufferedReader buffered = new BufferedReader(fileReader);
            response = gson.fromJson(buffered, type);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static void saveResponseFormatted(Context context, HiveResponse response, String fileName) {

        Gson gson = new Gson();
        String json = gson.toJson(response);

        String UTF8 = "utf8";
        int BUFFER_SIZE = 80192;

        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput(fileName, Context.MODE_PRIVATE), UTF8), BUFFER_SIZE);
            bw.write(json);
            bw.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    public static void saveResponse(Context context, HiveResponse response, String fileName) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        File filesDir = context.getFilesDir();

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    public static <T> T readResponse(Context context, String fileName, Class<T> type) {
        Gson gson = new Gson();
        T response = null;

        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = context.openFileInput(fileName);
            try {
                while ((ch = fis.read()) != -1)
                    fileContent.append((char) ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response = gson.fromJson(String.valueOf(fileContent), type);
            Log.i("PersistenceHelper", String.valueOf(fileContent));

            System.out.println(response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }
}