package com.unicyb.shaurmago.services;

import android.util.Log;

import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * class to make Http Request to the web
 */
public class Request {

    private static final String TAG = Request.class.getSimpleName();

    public static String post(String serverUrl, String dataToSend) throws NoInternetConnectionException, ServerTerminatedException {
        try {
            if (!isOnline()) {
                throw new NoInternetConnectionException();
            }
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //set timeout of 30 seconds
            con.setConnectTimeout(1000 * 30);
            con.setReadTimeout(1000 * 30);
            //method
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            //make request
            writer.write(dataToSend);
            writer.flush();
            writer.close();
            os.close();

            //get the response
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //read the response
                StringBuilder sb = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String line;

                //loop through the response from the server
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                //return the response
                return sb.toString();
            } else {
                Log.e(TAG, "ERROR - Invalid response code from server " + responseCode);
                throw new ServerTerminatedException("Server out of run");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR " + e);
            throw new ServerTerminatedException("Server out of run");

        }
    }

    public static String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //TODO Change this, it works unexpeted on diferent devices
    private static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}