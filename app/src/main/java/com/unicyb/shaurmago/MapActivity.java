package com.unicyb.shaurmago;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicyb.shaurmago.Utils.FirebaseDatabaseUtil;
import com.unicyb.shaurmago.barcode_reader.BarcodeActivity;
import com.unicyb.shaurmago.models.MarkerModel;

import java.util.HashMap;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback ,NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMarker;
    HashMap<String, Marker> markers = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaurma_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        NavigationView navView= (NavigationView) findViewById(R.id.maps_nav_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addPoint);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Marker mk = mMap.addMarker(new MarkerOptions().position(latLng));
                        startActivity(new Intent(MapActivity.this, AddNewPointActivity.class).putExtra("latlng", latLng));
                        mMap.setOnMapClickListener(null);
                    }
                });
            }
        });
        if (mDatabase==null){
        mDatabase = FirebaseDatabaseUtil.getDatabase();
        }
        mMarker = mDatabase.getReference("marker");
        mMarker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    MarkerModel mk = post.getValue(MarkerModel.class);
                    addMarker(mk);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled:" + databaseError.getMessage());
            }
        });
        mMarker.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MarkerModel mk = dataSnapshot.getValue(MarkerModel.class);
                addMarker(mk);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MarkerModel md = dataSnapshot.getValue(MarkerModel.class);
                markers.get(md.getId()).remove();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(MapActivity.this, PointInfoActivity.class).putExtra("id",
                        marker.getSnippet()).putExtra("name", marker.getTitle()));
                return false;
            }
        });
    }


    public void addMarker(MarkerModel mk) {
        BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker);
        MarkerOptions mo = new MarkerOptions().position(new LatLng(mk.getLat(), mk.getLng())).title(mk.getName()).snippet(mk.getId()).icon(icon);
        markers.put(mk.getId(), mMap.addMarker(mo));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_barcode) {
            startActivity(new Intent(MapActivity.this, BarcodeActivity.class));
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_map) {
            startActivity(new Intent(MapActivity.this, MapActivity.class));
        }
        if (id == R.id.nav_login) {
            startActivity(new Intent(MapActivity.this, LoginActivity.class));
        }
        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MapActivity.this, LoginActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}