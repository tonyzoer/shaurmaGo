package com.example.zoer.shaurmago;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.example.zoer.shaurmago.services.ServerConncection;
import com.example.zoer.shaurmago.services.StringHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.net.URL;


public class ShaurmaMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaurma_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on thmethode device, the user will be prompted to install
     * it inside the SupportMapFragment. This  will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//         Add a marker in Sydney and move the camer
        new PointGetter(mMap).execute();

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
            startActivity(new Intent(ShaurmaMaps.this,PointInfo.class).putExtra("id",marker.getSnippet()));
                return false;
            }
        });
    }


    class PointGetter extends AsyncTask<Void, Pair<LatLng,Pair<String,String>>, JSONArray> {
        private String urlGet = "https://shaurma-go-server-okamanahi.c9users.io/getallpoints.php";
        GoogleMap mMap = null;

        public PointGetter(GoogleMap googleMap) {
            mMap = googleMap;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            try {
                JSONArray arr = new JSONArray(ServerConncection.getJsonResponse(urlGet));

                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject obj = arr.getJSONObject(i);
                        LatLng pos = new LatLng(obj.getDouble("Lat"),
                                obj.getDouble("Lng"));
                        publishProgress(new Pair<LatLng, Pair<String,String>>(pos,new Pair<String, String>(obj.getString("name"),obj.getString("id"))));
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        }

        @Override
        protected void onProgressUpdate(Pair<LatLng,Pair<String,String>>... values) {
            for (Pair<LatLng,Pair<String,String>> pair:values
                 ) {
                mMap.addMarker(new MarkerOptions().position(pair.first).title(StringHelper.fromUtfToRus(pair.second.first)).snippet(pair.second.second));
             }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(JSONArray jsonArray) {
            super.onCancelled(jsonArray);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


    }
}