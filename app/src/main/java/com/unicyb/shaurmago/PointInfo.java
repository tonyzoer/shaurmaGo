package com.unicyb.shaurmago;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.exceptions.NoInternetConnectionException;
import com.unicyb.shaurmago.exceptions.ServerTerminatedException;
import com.unicyb.shaurmago.services.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class PointInfo extends Activity {
    ListView commentsList = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        ImageView img = (ImageView) findViewById(R.id.photo);
        TextView txt = (TextView) findViewById(R.id.desc);
        Bundle b = getIntent().getExtras();
        String value = null; // or other values

        String[] comments = {"nice", "wow", "so good!", "nice", "wow", "so good!", "nice", "wow", "so good!"};
        commentsList = (ListView) findViewById(R.id.commentsList);
        Utility.setListViewHeight(commentsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, comments);
        commentsList.setAdapter(adapter);

        if (b != null)
            value = b.getString("id");
        JSONArray arr = null;
        try {
            arr = new DownloadPointInfoTask().execute(value).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (arr != null && arr.length() == 1) {
            try {
                new DownloadImageTask(img).execute(
                        getString(R.string.server) + arr.getJSONObject(0).getString("photo"));
                txt.append(arr.getJSONObject(0).getString("desc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
}
