package com.example.zoer.shaurmago;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zoer.shaurmago.Utils.ImageUtil;
import com.example.zoer.shaurmago.Utils.Utility;
import com.example.zoer.shaurmago.exceptions.NoInternetConnectionException;
import com.example.zoer.shaurmago.exceptions.ServerTerminatedException;
import com.example.zoer.shaurmago.services.ServerConncection;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class AddNewPoint extends AppCompatActivity {
    private static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView ivImage;
    private EditText name;
    private EditText desc;
    private String userChoosenTask;
    LatLng pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_point);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivImage = (ImageView) findViewById(R.id.prewiewaddphoto);
        name = (EditText) findViewById(R.id.edit_name);
        desc = (EditText) findViewById(R.id.Desc);
        Bundle b = getIntent().getExtras();
        if (b != null)
            pos = (LatLng) b.get("latlng");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> map=new HashMap<String, String>();
                map.put("name",name.getText().toString());
                map.put("desc",desc.getText().toString());
                new SendData().execute(new Pair<HashMap<String, String>, Bitmap>(map,ivImage.getDrawingCache()));
            }
        });
        Button addphotobtn = (Button) findViewById(R.id.addPhoto);
        addphotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewPoint.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//                boolean result= Utility.checkPermission(AddNewPoint.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
//                    if(result)
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
//                    if(result)
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
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    private class SendData extends AsyncTask<Pair<HashMap<String,String>,Bitmap>,Integer,Void> {
        ProgressDialog prgd=null;
        @Override
        protected Void doInBackground(Pair<HashMap<String,String>,Bitmap>... params) {
            HashMap<String,String> map=params[0].first;
            try {
                publishProgress(10);
                String id= ServerConncection.postData(getString(R.string.add_new_point),
                        new Pair<String, String>("name", map.get("name")),
                        new Pair<String, String>("Lat", String.valueOf(pos.latitude)),
                        new Pair<String, String>("Lng", String.valueOf(pos.longitude)));
                publishProgress(40);
                ServerConncection.postData(getString(R.string.add_new_point_info),
                        new Pair<String, String>("id", id),
                        new Pair<String, String>("desc",map.get("desc")),
                        new Pair<String, String>("base64", ImageUtil.convert(params[0].second)));
                publishProgress(95);
            } catch (NoInternetConnectionException e) {
                e.printStackTrace();
            } catch (ServerTerminatedException e) {
                e.printStackTrace();
            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          prgd=ProgressDialog.show(AddNewPoint.this,"Uploading...","Please wait...",false,false);
            prgd.setMax(100);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            prgd.setProgress(values[0]);
        }
    }

}
