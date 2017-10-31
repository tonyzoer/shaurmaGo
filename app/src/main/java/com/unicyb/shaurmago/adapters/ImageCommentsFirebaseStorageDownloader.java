package com.unicyb.shaurmago.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.unicyb.shaurmago.models.CommentModel;

/**
 * Created by mafio on 02.09.2017.
 */

public class ImageCommentsFirebaseStorageDownloader {
    FirebaseStorage storage;
    CommentsAdapter adapter;


    private String TAG = "ImageDownloader";

    public ImageCommentsFirebaseStorageDownloader(CommentsAdapter adapter) {
        this.adapter = adapter;
        storage = FirebaseStorage.getInstance();
    }

    private Integer checkAllImagesDownloaded(CommentModel cm) {
        Integer result = 2;
        for (Integer b : cm.getDownloaded()
                ) {
            if (b == 0) {
                return 0;
            }
            if (b == 1) {
                result = 1;
            }
        }
        return result;
    }

    public void download(int i) {
        CommentModel cm = adapter.commentsSet.get(i);

        if (checkAllImagesDownloaded(cm) == 2) {
            //images already downloaded
            return;
        }

        final int ii = i;
        for (String path : cm.getNotDownloadedImagesPath()
                ) {

            final String fPath = path;
            final String filename = fPath.split("/")[2];
            int filenameInt = Integer.valueOf(filename);
                if (cm.getDownloaded().get(filenameInt - 1) == 0) {
                    cm.setStartedDownloading(filenameInt);
                    storage.getReference().child(path).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                            adapter.commentsSet.get(ii).addImage(scaled, Integer.valueOf(filename));
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "onSuccess: Image added to Comment " + ii);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //TODO create custom image with text "Image was deleted";
                            Log.d(TAG, "onFailure: No images for this comment");
                        }
                    });
                    i++;
                }
            }
        }

}
