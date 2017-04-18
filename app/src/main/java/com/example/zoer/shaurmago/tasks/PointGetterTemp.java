//package com.example.zoer.shaurmago.tasks;
//
//import android.os.AsyncTask;
//
//import com.example.zoer.shaurmago.ShaurmaMaps;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.ref.WeakReference;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * Created by Zoer on 18.04.2017.
// */
//
//public class PointGetterTemp extends AsyncTask<Void, LatLng, JSONArray> {
//    private static String urlGet="https://shaurma-go-server-okamanahi.c9users.io/getallpoints.php";
//    GoogleMap mMap=null;
//    public PointGetterTemp(GoogleMap googleMap) {
//        mMap=googleMap;
//    }
//
//    @Override
//    protected JSONArray doInBackground(Void... params) {
//
//        try {
//            JSONArray arr= new JSONArray(getJsonResponse(urlGet));
//
//            for (int i = 0; i <arr.length() ; i++) {
//                try {
//                    JSONObject obj=arr.getJSONObject(i);
//                    LatLng pos=new LatLng(obj.getDouble("Lat"),
//                            obj.getDouble("Lng"));
//                    publishProgress(pos);
//
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected void onPostExecute(JSONArray jsonArray) {
//        super.onPostExecute(jsonArray);
//    }
//
//    @Override
//    protected void onProgressUpdate(LatLng... values) {
//mMap.addMarker(new MarkerOptions().position(pos).title(obj.getString("name")));
//        super.onProgressUpdate(values);
//    }
//
//    @Override
//    protected void onCancelled(JSONArray jsonArray) {
//        super.onCancelled(jsonArray);
//    }
//
//    @Override
//    protected void onCancelled() {
//        super.onCancelled();
//    }
//
//    private String getJsonResponse(String urls) {
//        URL url = null;
//        try {
//            url = new URL(urls);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection conn = null;
//        String response = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            InputStream in= new BufferedInputStream(conn.getInputStream());
//            response = convertStreamToString(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//    private String convertStreamToString(InputStream is) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append('\n');
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return sb.toString();
//    }
//}
