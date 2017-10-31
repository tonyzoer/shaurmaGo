package com.unicyb.shaurmago.activity.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.FirebaseDatabaseUtil;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.models.MarkerModel;
import com.unicyb.shaurmago.models.PointInfoModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddNewPointActivity extends AppCompatActivity {

    private static final String TAG = AddNewPointActivity.class.getSimpleName();
    private static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    LatLng pos;
    private ImageView ivImage;
    private EditText name;
    private EditText desc;
    private String userChosenTask;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMarkerRef;
    private DatabaseReference mPointInfoRef;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_point);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_point_toolbar);
        setSupportActionBar(toolbar);

        ivImage = (ImageView) findViewById(R.id.previewAddPhoto);
        name = (EditText) findViewById(R.id.Name);
        desc = (EditText) findViewById(R.id.Desc);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            pos = (LatLng) b.get("latlng");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.Add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivImage.buildDrawingCache();
                new Upload(imageBitmap, name.getText().toString(),
                        desc.getText().toString()).execute();
            }
        });
        Button addPhotoBtn = (Button) findViewById(R.id.addPhoto);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mDatabase = FirebaseDatabaseUtil.getDatabase();
        mMarkerRef = mDatabase.getReference().child("marker");
        mPointInfoRef = mDatabase.getReference().child("pointInfo");

    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewPointActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals("Зробити фото"))
                        cameraIntent();
                    else if (userChosenTask.equals("Вибрати з галереї"))
                        galleryIntent();
                } else {
                    Toast.makeText(getApplicationContext(), "Нет доступа", Toast.LENGTH_LONG);

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        imageBitmap=thumbnail;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        }

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String s = destination.getAbsolutePath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            }
        });
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), data.getData());
                imageBitmap=bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);

    }

    private class Upload extends AsyncTask<Void, Void, String> {
        private Bitmap image;
        private String name;
        private String desc;
        private LatLng latlng;

        Upload(Bitmap image, String name, String desc) {
            this.image = image;
            this.name = name;
            this.desc = desc;
            this.latlng = pos;
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            String id = mMarkerRef.push().getKey();
            MarkerModel markerModel = new MarkerModel(id, latlng.latitude, latlng.longitude, name);
            PointInfoModel pointInfoModel = new PointInfoModel(id, encodeImage, desc);
            mPointInfoRef.child(id).setValue(pointInfoModel);
            mMarkerRef.child(id).setValue(markerModel);
            return name;
        }


        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(), "Точка создана " + s, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddNewPointActivity.this, MapActivity.class));
        }

    }
}
