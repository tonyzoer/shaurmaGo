package com.unicyb.shaurmago;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.adapters.CommentsAdapter;
import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;
import com.unicyb.shaurmago.models.CommentModel;
import com.unicyb.shaurmago.services.Request;
import com.unicyb.shaurmago.services.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PointInfo extends Activity {
    ListView commentsList = null;
    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        ImageView img = (ImageView) findViewById(R.id.photo);
        TextView txt = (TextView) findViewById(R.id.desc);
        Bundle b = getIntent().getExtras();
        id= null; // or other values
        Button sendBtn= (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.comment_edit);
        //TODO add realisation
        ArrayList<CommentModel> arrayList = new ArrayList<>();
        arrayList.add(new CommentModel("First User", "LAldasldlasldlasfdsdl", 4.5));
        arrayList.add(new CommentModel("Second User", "LAldasfdsfdsfdsfldlasldlasdl", 3.5));
        arrayList.add(new CommentModel("Thid User", "LAdsfsdfldasldlasldlasdl", 3.5));
        arrayList.add(new CommentModel("First User", "LAldasldlasldladsfsfhgsdfgsdl", 1.5));
        arrayList.add(new CommentModel("First User", "LAldasldlasldladsfsfhgsdfgsdl", 0.5));
        arrayList.add(new CommentModel("First User", "LAldasldlasldladsfsfhgsdfgsdl", 0.49));
        commentsList = (ListView) findViewById(R.id.commentsList);
        Utility.setListViewHeight(commentsList);
        CommentsAdapter adapter = new CommentsAdapter(arrayList, getApplicationContext());
        commentsList.setAdapter(adapter);

        if (b != null) {
            id = b.getString("id");
        } else {
            finish();
        }
        JSONArray arr = null;
        try {
            arr = new DownloadPointInfoTask().execute(id).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (arr != null && arr.length() == 1) {
            try {
                new DownloadImageTask(img).execute(
                        getString(R.string.server) + arr.getJSONObject(0).getString("photo"));
                txt.setText(arr.getJSONObject(0).getString("desc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommentPoint().execute(editText.getText().toString());
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class DownloadPointInfoTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray arr = null;
            try {
                arr = new JSONArray(
                        ServerConnection.getResponse(getString(R.string.get_point_info),
                                new Pair<>("id", params[0])));
            } catch (JSONException | ServerTerminatedException | NoInternetConnectionException e) {
                e.printStackTrace();
            }
            return arr;
        }
    }

    private class CommentPoint extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ans = null;
            HashMap<String, String> posthashmap = new HashMap<String, String>();
            //TODO finish this after creating shared prefernce for user log
            SharedPreferences pref = getApplicationContext().getSharedPreferences("user", 0);
            String userId=pref.getString("userId","0");
            if (!userId.equals("0")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Ви не ввійшли", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            posthashmap.put("id",id);
            posthashmap.put("omment",params[0]);
            posthashmap.put("userId",userId);
            posthashmap.put("hashpass",pref.getString("hashpass","0"));
            try {
                 ans= Request.post(getString(R.string.add_new_comment), Request.hashMapToUrl(posthashmap));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return ans;
        }
    }
}
