package com.unicyb.shaurmago.activity.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.ImagesArraySingleton;
import com.unicyb.shaurmago.Utils.StringHelper;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.models.CommentModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResponseActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mComments;

    private RatingBar tasteBar;
    private RatingBar textureBar;
    private RatingBar pointBar;
    private EditText responseText;
    private Button addPhotoButton;
    private Button sendBtn;
    private String pointId;
    private String userChosenTask;
    private ImageView imagePointPreview;
    private List<Bitmap> imageBitmapsList;
    private Bitmap imageBitmap;
    private TextView imageCounter;
    private static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private  ProgressDialog progress ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tasteBar = (RatingBar) findViewById(R.id.content_response_taste_rating_bar);
        textureBar = (RatingBar) findViewById(R.id.content_response_texture_rating_bar);
        pointBar = (RatingBar) findViewById(R.id.content_response_point_rate_rating_bar);
        responseText = (EditText) findViewById(R.id.content_response_coment_text);
        imagePointPreview =(ImageView) findViewById(R.id.content_response_image_preview_2);
        imageCounter= (TextView) findViewById(R.id.content_response_photo_counter);
        addPhotoButton= (Button) findViewById(R.id.content_response_add_photo_btn);
        imageBitmapsList=new LinkedList<>();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            pointId = b.getString("id");
        }

        addPhotoButton.setOnClickListener(this);
        imagePointPreview.setOnClickListener(this);
        mComments = FirebaseDatabase.getInstance().getReference("comments");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.response_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.response_menu_send_btn: {
                new CommentPointTask(this).execute(responseText.getText().toString(),
                        String.valueOf(tasteBar.getRating()),
                        String.valueOf(textureBar.getRating()),
                        String.valueOf(pointBar.getRating())
                );

            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_response_add_photo_btn:selectImage();break;
            case R.id.content_response_image_preview_2:openImageSlider();
        }
    }

    private void openImageSlider(){
        ImagesArraySingleton.getInstance().clear();
        ImagesArraySingleton.getInstance().setImages(imageBitmapsList);

        startActivity(new Intent(ResponseActivity.this,FullScreenViewActivity.class));
    }

    //image add
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ResponseActivity.this);
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
        imageBitmapsList.add(imageBitmap);
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
        increaseImageCounter();
        int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
        imagePointPreview.setImageBitmap(scaled);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), data.getData());
                imageBitmap=thumbnail;
                imageBitmapsList.add(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        increaseImageCounter();
        int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
        imagePointPreview.setImageBitmap(scaled);

    }
    private void increaseImageCounter(){
        imageCounter.setText(String.valueOf(imageBitmapsList.size()));
    }

    private class CommentPointTask extends AsyncTask<String, Void, String> {
        Context mContext;


        public CommentPointTask(Context mContext) {
            this.mContext = mContext;

        }


        @Override
        protected String doInBackground(String... params) {


            String commentId = mComments.push().getKey();
            ArrayList<String> imagesPath=new ArrayList<>();

            //saving images to storage
            FirebaseStorage storage =FirebaseStorage.getInstance();

            StorageReference storageCommentThisRef=storage.getReference().child("commentImage").child(commentId);
            int i=1;
            for (Bitmap bmp:imageBitmapsList) {
                String path="commentImage/"+commentId+"/"+ String.valueOf(i);
                StorageReference currentImageRef= storageCommentThisRef.child(String.valueOf(i));
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] data=baos.toByteArray();

                UploadTask uploadTask=currentImageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ((Activity)mContext).finish();
                        Toast.makeText(ResponseActivity.this,"One or more photoes didnt uploaded",Toast.LENGTH_LONG);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ((Activity)mContext).finish();
                        Toast.makeText(ResponseActivity.this,"One or more photoes uploaded",Toast.LENGTH_LONG);
                    }
                });
                i++;
                imagesPath.add(path);
            }
            CommentModel cm = new CommentModel(commentId,
                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                    , pointId, params[0]//comment
                    , Double.valueOf(params[1])//tasteRate
                    , Double.valueOf(params[2])//textureRate
                    , Double.valueOf(params[3]),//pointRate
                    imagesPath);

            mComments.child(cm.getId()).setValue(cm);


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }


    }
}
