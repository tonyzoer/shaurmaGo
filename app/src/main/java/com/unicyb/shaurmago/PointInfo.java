package com.unicyb.shaurmago;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unicyb.shaurmago.Utils.SharedPreferencesUtil;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.adapters.CommentsAdapter;
import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;
import com.unicyb.shaurmago.models.CommentModel;
import com.unicyb.shaurmago.services.Request;

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
    String title;
    RatingBar rate;
    CommentsAdapter adapter;
    ArrayList<CommentModel> commnetsArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        ImageView img = (ImageView) findViewById(R.id.photo);

        TextView txt = (TextView) findViewById(R.id.desc);
        Bundle b = getIntent().getExtras();
        rate = (RatingBar) findViewById(R.id.new_rate);
        id = null; // or other values
        Button sendBtn = (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.comment_edit);
        //TODO add realisation
        commnetsArrayList = new ArrayList<>();
        commentsList = (ListView) findViewById(R.id.commentsList);
        Utility.setListViewHeight(commentsList);
        adapter = new CommentsAdapter(commnetsArrayList, getApplicationContext());
        commentsList.setAdapter(adapter);
        if (b != null) {
            id = b.getString("id");
            title = b.getString("name");
            txt.setText(title);
        } else {
            finish();
        }
        JSONArray arr = null;
        try {
            arr = new DownloadPointInfoTask(img).execute(id).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                editText.clearFocus();
                new CommentPointTask().execute(editText.getText().toString(), String.valueOf(rate.getRating()));
            }
        });
        new DownloadCommentsTask().execute();
    }

    private class DownloadCommentsTask extends AsyncTask<Void, CommentModel, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", id);
            JSONArray arr = null;
            try {
                arr = new JSONArray(Request.post(getString(R.string.get_comments), Request.hashMapToUrl(map)));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    publishProgress(new CommentModel(obj.getString("email"), obj.getString("text"), obj.getDouble("stars")));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NoInternetConnectionException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ServerTerminatedException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Server out of run", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(CommentModel... values) {
            for (CommentModel cm : values
                    ) {
                commnetsArrayList.add(cm);
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
        ImageView img;
        JSONArray arr;
        public DownloadPointInfoTask(ImageView img) {
            this.img = img;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            arr = null;
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", params[0]);
                arr = new JSONArray(
                        Request.post(getString(R.string.get_point_info),
                                Request.hashMapToUrl(map)));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ServerTerminatedException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Server out of run", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            } catch (NoInternetConnectionException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No internet Connection", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return arr;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            try {
                new DownloadImageTask(img).execute(
                        getString(R.string.server) + jsonArray.getJSONObject(0).getString("photo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CommentPointTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ans = null;
            HashMap<String, String> posthashmap = new HashMap<String, String>();
            //TODO finish this after creating shared prefernce for user log
            String email = SharedPreferencesUtil.getDefaults("email", getApplicationContext());
            if (email.equals(null)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Ви не ввійшли", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            posthashmap.put("id", id);
            posthashmap.put("comment", params[0]);
            posthashmap.put("userId", SharedPreferencesUtil.getDefaults("id", getApplicationContext()));
            posthashmap.put("stars", params[1]);
            try {
                ans = Request.post(getString(R.string.add_new_comment), Request.hashMapToUrl(posthashmap));
                commnetsArrayList.add(new CommentModel(email, params[0], Double.valueOf(params[1])));
            } catch (UnsupportedEncodingException e) {
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
            } catch (ServerTerminatedException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Server out of run", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }
            return ans;
        }

    }
}
