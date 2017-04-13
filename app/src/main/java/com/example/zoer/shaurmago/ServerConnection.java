package com.example.zoer.shaurmago;




import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.example.zoer.shaurmago.R.*;

/**
 * Created by Zoer on 11.04.2017.
 */

public class ServerConnection {
    private static ServerConnection instance;

    static {
        try {
            instance = new ServerConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private ServerConnection() throws MalformedURLException {
    }

    public static ServerConnection getInstance() {
        return instance;
    }

    public String getAllPoints(String urls) {
        URL url = null;
        try {
            url = new URL(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        String response = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public void addNewPoint(){

    }

    private String convertStreamToString(InputStream is) {
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
    //TODO remove this. its just a test
    public static void main(String[] args) throws JSONException {
        JSONArray arr=null;
        String s=ServerConnection.getInstance().getAllPoints("https://shaurma-go-server-okamanahi.c9users.io/getallpoints.php");
        System.out.println(s);
        try {
            arr = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arr.length();
    }
}
