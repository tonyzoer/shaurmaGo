package com.example.zoer.shaurmago.services;

import android.util.Pair;

import com.example.zoer.shaurmago.exceptions.NoInternetConnectionException;
import com.example.zoer.shaurmago.exceptions.ServerTerminatedException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerConnection {
    public static String getResponse(String urls, Pair<String, String>... pairs) throws NoInternetConnectionException, ServerTerminatedException {
        return makeRequest(urls, "GET", pairs);
    }

    public static String postData(String urls, Pair<String, String>... pairs) throws NoInternetConnectionException, ServerTerminatedException {
        return makeRequest(urls, "POST", pairs);
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private static String makeRequest(String urls, String type, Pair<String, String>... pairs)
            throws NoInternetConnectionException, ServerTerminatedException {
        if (!isOnline()) {
            throw new NoInternetConnectionException();
        }
        URL url = null;
        StringBuilder urlBuilder = new StringBuilder(urls);
        try {
            int i = 0;
            for (Pair<String, String> pair : pairs
                    ) {
                if (i == 0) {
                    urlBuilder.append("?");
                }
                urlBuilder.append(pair.first);
                urlBuilder.append("=");
                urlBuilder.append(pair.second);
                i++;
                if (i < pairs.length) {
                    urlBuilder.append("&");
                }
            }
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn;
        String response;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            throw new ServerTerminatedException("Server has stoped");
        }
        return response;
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
