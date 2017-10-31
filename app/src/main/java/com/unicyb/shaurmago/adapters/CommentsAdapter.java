package com.unicyb.shaurmago.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.ImagesArraySingleton;
import com.unicyb.shaurmago.activity.impl.FullScreenViewActivity;
import com.unicyb.shaurmago.models.CommentModel;

import java.util.ArrayList;

/**
 * Created by Zoer on 10.05.2017.
 */

public class CommentsAdapter extends ArrayAdapter<CommentModel> implements DialogInterface.OnClickListener {
    private static final String TAG = "CommentsAdapter";
    ArrayList<CommentModel> commentsSet;
    Context mContext;
    ImageCommentsFirebaseStorageDownloader imgDownloder;
    private boolean anim = true;

    private static class ViewHolder {
        TextView user_name;
        TextView comment;
        RatingBar rate;
        LinearLayout photoesLinearLayout;
    }

    public CommentsAdapter(ArrayList<CommentModel> data, @NonNull Context context) {
        super(context, R.layout.comments_item, data);
        mContext = context;
        commentsSet = data;
        imgDownloder = new ImageCommentsFirebaseStorageDownloader(this);
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        CommentModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        imgDownloder.download(position);
        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comments_item, parent, false);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment_text);
            viewHolder.rate = (RatingBar) convertView.findViewById(R.id.comment_rate);
            viewHolder.photoesLinearLayout = (LinearLayout) convertView.findViewById(R.id.comment_photo_linear_layout);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                result.startAnimation(animation);
            }
        }


        lastPosition = position;


        viewHolder.user_name.setText(dataModel.getUser_id());
        viewHolder.comment.setText(dataModel.getComment());
        viewHolder.rate.setRating(dataModel.getRate().floatValue());
        int i = 0;
        for (Bitmap bmp : dataModel.getImages()
                ) {
            if (viewHolder.photoesLinearLayout.findViewById(i) == null) {
                ImageView image = new ImageView(viewHolder.photoesLinearLayout.getContext());
                image.setImageBitmap(bmp);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                params.setMargins(0, 0, 0, 0);
                image.setLayoutParams(params);
                image.setId(i);
                viewHolder.photoesLinearLayout.addView(image);
                i++;
            } else {
                i++;
            }
        }

        // Return the completed view to render on screen
        Log.d(TAG, "getView: View returned: " + dataModel.getId());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        lastPosition = -1;
        anim = false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
//        ImagesArraySingleton.getInstance().setImages(commentsSet.get(which).getImages());
//        getContext().
//                startActivity(new Intent(getContext(), FullScreenViewActivity.class));
    }


}
