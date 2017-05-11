package com.unicyb.shaurmago.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.models.CommentModel;

import java.util.ArrayList;

/**
 * Created by Zoer on 10.05.2017.
 */

public class CommentsAdapter extends ArrayAdapter<CommentModel> implements DialogInterface.OnClickListener {
    private ArrayList<CommentModel> commentsSet;
    Context mContext;
    private static class ViewHolder {
        TextView user_name;
        TextView comment;
        RatingBar rate;
    }
    public CommentsAdapter(ArrayList<CommentModel> data, @NonNull Context context, @LayoutRes int resource) {
        super(context, R.layout.comments_item, data);
        mContext=context;
        commentsSet=data;
    }

    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        CommentModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comments_item, parent, false);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.comment= (TextView) convertView.findViewById(R.id.comment_text);
            viewHolder.rate = (RatingBar) convertView.findViewById(R.id.comment_rate);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.user_name.setText(dataModel.getUser_name());
        viewHolder.comment.setText(dataModel.getComment());
        viewHolder.rate.setNumStars(dataModel.getRate().intValue());
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }
}
