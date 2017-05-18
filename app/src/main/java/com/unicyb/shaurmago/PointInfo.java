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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.unicyb.shaurmago.Utils.SharedPreferencesUtil;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.adapters.CommentsAdapter;
import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;
import com.unicyb.shaurmago.models.CommentModel;
import com.unicyb.shaurmago.services.Request;
import com.unicyb.shaurmago.services.ServerConnection;
import com.unicyb.shaurmago.services.StringHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PointInfo extends Activity {
    ListView commentsList = null;
    String id;
    RatingBar rate;
    CommentsAdapter adapter;
    ArrayList<CommentModel> arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        ImageView img = (ImageView) findViewById(R.id.photo);
        TextView txt = (TextView) findViewById(R.id.desc);
        Bundle b = getIntent().getExtras();
        rate= (RatingBar) findViewById(R.id.new_rate);
        id= null; // or other values
        Button sendBtn= (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.comment_edit);
        //TODO add realisation
        arrayList = new ArrayList<>();
        commentsList = (ListView) findViewById(R.id.commentsList);
        Utility.setListViewHeight(commentsList);
        adapter = new CommentsAdapter(arrayList, getApplicationContext());
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
                new CommentPoint().execute(editText.getText().toString(), String.valueOf(rate.getRating()));
            }
        });
        new DownloadComments().execute();
    }

    private class DownloadComments extends AsyncTask<Void,CommentModel,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String,String> map=new HashMap<>();
            map.put("id",id);
             JSONArray arr=null;
            try {
            arr=new JSONArray(Request.post(getString(R.string.get_comments),Request.hashMapToUrl(map)));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    publishProgress(new CommentModel(obj.getString("email"),obj.getString("text"),obj.getDouble("stars")));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(CommentModel... values) {
            for (CommentModel cm:values
                 ) {
                arrayList.add(cm);
            }
            super.onProgressUpdate(values);
        }
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
            String email= SharedPreferencesUtil.getDefaults("email",getApplicationContext());
            if (email.equals(null)){
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
            posthashmap.put("comment",params[0]);
            posthashmap.put("userId",SharedPreferencesUtil.getDefaults("id",getApplicationContext()));
            posthashmap.put("stars", params[1]);
            try {
                 ans= Request.post(getString(R.string.add_new_comment), Request.hashMapToUrl(posthashmap));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return ans;
        }

    }
}
