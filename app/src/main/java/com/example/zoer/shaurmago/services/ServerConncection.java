package com.example.zoer.shaurmago.services;

import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Zoer on 18.04.2017.
 */

public class ServerConncection {
    public static String getResponse(String urls, Pair<String,String>...pairs) {
    return makeRequest(urls,"GET",pairs);
    }

    public static String postData(String urls, Pair<String,String>...pairs){
    return makeRequest(urls,"POST",pairs);
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
    private static String makeRequest(String urls,String type, Pair<String,String>...pairs){
        URL url = null;
        StringBuilder urlbuilder=new StringBuilder(urls);
        try {
            int i=0;
            for (Pair<String,String> pair:pairs
                    ) {
                if (i==0){
                    urlbuilder.append("?");
                }
                urlbuilder.append(pair.first+"="+pair.second);
                i++;
                if (i<pairs.length){
                    urlbuilder.append("&");
                }
            }
            url = new URL(urlbuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        String response = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
