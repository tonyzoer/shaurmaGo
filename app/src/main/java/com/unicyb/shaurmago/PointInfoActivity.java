package com.unicyb.shaurmago;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicyb.shaurmago.Utils.ImageUtil;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.adapters.CommentsAdapter;
import com.unicyb.shaurmago.models.CommentModel;
import com.unicyb.shaurmago.models.PointInfoModel;

import java.util.ArrayList;

public class PointInfoActivity extends Activity {
    ListView commentsList = null;
    String id;
    String title;
    RatingBar rate;
    CommentsAdapter adapter;
    ArrayList<CommentModel> commnetsArrayList;
    FirebaseDatabase mDatabase;
    DatabaseReference mPointInfo;
    DatabaseReference mComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        final ImageView img = (ImageView) findViewById(R.id.photo);

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

        sendBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                editText.clearFocus();
                new CommentPointTask().execute(editText.getText().toString(), String.valueOf(rate.getRating()));
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mPointInfo = mDatabase.getReference("pointInfo");
        mPointInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()
                        ) {
                    PointInfoModel pim = ds.getValue(PointInfoModel.class);
                    img.setImageBitmap(ImageUtil.convert(pim.getPhoto()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mComments = mDatabase.getReference("comments");
        mComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()
                        ) {
                    CommentModel cm = ds.getValue(CommentModel.class);
                    commnetsArrayList.add(cm);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private class CommentPointTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String commentId = mComments.push().getKey();
            CommentModel cm = new CommentModel();
            cm.setId(commentId);
            cm.setComment(params[0]);
            cm.setPointInfoId(id);
            cm.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
            cm.setRate(Double.valueOf(params[1]));
            //Todo end this after creating signin with firebase auth
            mComments.child(cm.getId()).setValue(cm);

            return null;
        }

    }
}
