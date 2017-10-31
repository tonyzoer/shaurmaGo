package com.unicyb.shaurmago.activity.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.ImageUtil;
import com.unicyb.shaurmago.Utils.ImagesArraySingleton;
import com.unicyb.shaurmago.Utils.Utility;
import com.unicyb.shaurmago.adapters.CommentsAdapter;
import com.unicyb.shaurmago.models.CommentModel;
import com.unicyb.shaurmago.models.PointInfoModel;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PointInfoActivity extends Activity {
    private static final String TAG = "PointInfoActivity";
    ListView commentsList = null;
    String id;
    String title;
    //    RatingBar rate;
    CommentsAdapter adapter;
    ArrayList<CommentModel> commentsArrayList;
    FirebaseDatabase mDatabase;
    DatabaseReference mPointInfo;
    DatabaseReference mComments;
    FirebaseStorage mStorage;
    private Animator mCurrentAnimatorEffect;
    private int mShortAnimationDurationEffect;
    PointInfoModel pim;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_info);
        final ImageView img = (ImageView) findViewById(R.id.photo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView txt = (TextView) findViewById(R.id.desc);
        Bundle b = getIntent().getExtras();
        id = null; // or other values
        Button responseActivityBtn = (Button) findViewById(R.id.point_info_response_activity_btn);
        commentsArrayList = new ArrayList<>();
        commentsList = (ListView) findViewById(R.id.commentsList);
        Utility.setListViewHeight(commentsList);
        adapter = new CommentsAdapter(commentsArrayList, getApplicationContext());
        commentsList.setAdapter(adapter);
        if (b != null) {
            id = b.getString("id");
            title = b.getString("name");
            txt.setText(title);
        } else {
            finish();
        }

        responseActivityBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                startActivity(new Intent(PointInfoActivity.this, ResponseActivity.class).putExtra("id", id));
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mPointInfo = mDatabase.getReference("pointInfo").child(id);
        mComments = mDatabase.getReference("comments");
        mStorage = FirebaseStorage.getInstance();
        mPointInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pim = dataSnapshot.getValue(PointInfoModel.class);
                img.setImageBitmap(ImageUtil.convert(pim.getPhoto()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mComments.orderByChild("pointInfoId").equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                CommentModel cm = dataSnapshot.getValue(CommentModel.class);
                commentsArrayList.add(cm);
                final int idItem = commentsArrayList.size() - 1;
                Log.d(TAG, "onDataChange: Single Value Event " + cm.getId());
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CommentModel cm = dataSnapshot.getValue(CommentModel.class);
                for (int i = 0; i < commentsArrayList.size(); i++) {
                    if (commentsArrayList.get(i).getId().equals(cm.getId())) {
                        commentsArrayList.set(i, cm);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CommentModel cm = dataSnapshot.getValue(CommentModel.class);
                for (int i = 0; i < commentsArrayList.size(); i++) {
                    if (commentsArrayList.get(i).getId().equals(cm.getId())) {
                        commentsArrayList.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(img);
            }
        });
        mShortAnimationDurationEffect = getResources().getInteger(R.integer.config_shortAnimTime);

        commentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentsAdapter ad = (CommentsAdapter) parent.getAdapter();
                ImagesArraySingleton.getInstance().setImages(ad.getItem(position).getImages());
                startActivity(new Intent(PointInfoActivity.this, FullScreenViewActivity.class));
            }
        });
    }


    private void zoomImageFromThumb(final ImageView thumbView) {
        if (mCurrentAnimatorEffect != null) {
            mCurrentAnimatorEffect.cancel();
        }

        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageBitmap(ImageUtil.convert(pim.getPhoto())); //TODO (!!)
//        expandedImageView.setBackgroundColor(0xFFFAFAFA);
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDurationEffect);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimatorEffect = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimatorEffect = null;
            }
        });
        set.start();
        mCurrentAnimatorEffect = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimatorEffect != null) {
                    mCurrentAnimatorEffect.cancel();
                }
//                expandedImageView.setBackgroundColor(0xFF000000);
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDurationEffect);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimatorEffect = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimatorEffect = null;
                    }
                });
                set.start();
                mCurrentAnimatorEffect = set;
            }
        });
    }
}
