package com.example.zoer.shaurmago;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Zoer on 11.04.2017.
 */

public class ServerConnection {
    URL url=new URL("https://shaurma-go-server-okamanahi.c9users.io/getallpoints.php");
    private static ServerConnection singltone;

    static {
        try {
            singltone = new ServerConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private ServerConnection() throws MalformedURLException {
    }
    public static ServerConnection getInstance() {
        return singltone;
    }
    public JSONObject getAllPoints() throws IOException, JSONException {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return new JSONObject(result.toString());
    }
}
