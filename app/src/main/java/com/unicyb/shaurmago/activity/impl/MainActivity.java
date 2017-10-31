package com.unicyb.shaurmago.activity.impl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.FirebaseDatabaseUtil;
import com.unicyb.shaurmago.activity.barcode_reader.BarcodeActivity;
import com.unicyb.shaurmago.models.UserModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    private DatabaseReference mUser;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout header = (LinearLayout) navigationView.getHeaderView(0);
        TextView usereName = (TextView) header.findViewById(R.id.user_name);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabaseUtil.getDatabase();
        mUser = mDatabase.getReference().child("users");
        if (auth.getCurrentUser() != null) {
            if (usereName != null) {
                usereName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            }
            Menu menu = navigationView.getMenu();
            MenuItem login_item = menu.findItem(R.id.nav_login);
            login_item.setVisible(false);
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        new UpdateUser().execute(auth.getCurrentUser());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_barcode) {
            startActivity(new Intent(MainActivity.this, BarcodeActivity.class));
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_map) {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
        if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        if (id == R.id.nav_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class UpdateUser extends AsyncTask<FirebaseUser, Void, String> {
        @Override
        protected String doInBackground(FirebaseUser... params) {
            FirebaseUser user = params[0];
            UserModel userModel = new UserModel(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
            mUser.child(user.getUid()).push();
            mUser.child(user.getUid()).setValue(userModel);
            return null;
        }
    }
}
