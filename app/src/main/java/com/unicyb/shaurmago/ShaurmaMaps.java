package com.unicyb.shaurmago;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unicyb.shaurmago.Utils.StringHelper;
import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;
import com.unicyb.shaurmago.services.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;


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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addPoint);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Marker mk=mMap.addMarker(new MarkerOptions().position(latLng));
                        startActivity(new Intent(ShaurmaMaps.this, AddNewPoint.class).putExtra("latlng", latLng));
                        mMap.setOnMapClickListener(null);
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new PointGetter(mMap).execute();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(ShaurmaMaps.this, PointInfo.class).putExtra("id",
                        marker.getSnippet()));
                return false;
            }
        });
    }

    private class PointGetter extends AsyncTask<Void, Pair<LatLng, Pair<String, String>>, JSONArray> {
        GoogleMap mMap = null;
        private String urlGet = "https://shaurma-go-server-okamanahi.c9users.io/getallpoints.php";

        PointGetter(GoogleMap googleMap) {
            mMap = googleMap;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray arr = null;
            ObjectInputStream in;
            Integer countOnServer;
            Integer countOnLocal = 0;
            //If you have already saved points, it takes info from cache
            try {
                in = new ObjectInputStream(new FileInputStream(new File(
                        new File(getCacheDir(), "") + "shaurmaPoints.srl")));
                arr = new JSONArray((String) in.readObject());
                in.close();
                countOnLocal = arr.length();
            } catch (IOException e) {
                Log.d("Nofile", "File not found in cache");
            } catch (ClassNotFoundException | JSONException e) {
                e.printStackTrace();
            }
            //if nothing was cached it downloads points from server
            try {
                boolean downloaded = false;
                if (arr == null) {
                    arr = new JSONArray(Request.post(
                            getString(R.string.get_all_points),""));
                    countOnLocal = arr.length();
                    countOnServer = arr.length();
                    downloaded = true;
                }
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);
                    LatLng pos = new LatLng(obj.getDouble("Lat"),
                            obj.getDouble("Lng"));
                    publishProgress(new Pair<>(
                            pos, new Pair<>(obj.getString("name"), obj.getString("id"))));
                }
                if (!downloaded) {
                    countOnServer = new JSONArray(Request.post(
                            getString(R.string.get_points_count),"")).getJSONObject(0).getInt("COUNT(id)");
                    //if count on server is different , we download it from net if it wasnt downloaded earlier
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();
                        }
                    });

                    arr = new JSONArray(Request.post(
                            getString(R.string.get_all_points),""));
                    countOnServer = arr.length();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        LatLng pos = new LatLng(obj.getDouble("Lat"),
                                obj.getDouble("Lng"));
                        publishProgress(new Pair<>(
                                pos, new Pair<>(obj.getString("name"),
                                obj.getString("id"))));
                    }
                }
                //Write to cache
                ObjectOutput out = new ObjectOutputStream(
                        new FileOutputStream(new File(getCacheDir(), "") + "shaurmaPoints.srl"));
                out.writeObject(arr.toString());
                out.close();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } catch (ServerTerminatedException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Sorry our server is out of run", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            } catch (NoInternetConnectionException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                });
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

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(Pair<LatLng, Pair<String, String>>... values) {
            for (Pair<LatLng, Pair<String, String>> pair : values) {
                mMap.addMarker(new MarkerOptions().position(pair.first).
                        title(StringHelper.fromUtfToRus(pair.second.first)).
                        snippet(pair.second.second));
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