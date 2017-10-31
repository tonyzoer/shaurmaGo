package com.unicyb.shaurmago.activity.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.unicyb.shaurmago.R;
import com.unicyb.shaurmago.Utils.ImagesArraySingleton;
import com.unicyb.shaurmago.adapters.FullScreenImageAdapter;
import com.unicyb.shaurmago.models.CommentModel;

public class FullScreenViewActivity extends Activity{


    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreein_imgs_view);

        viewPager = (ViewPager) findViewById(R.id.pager);


        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
                ImagesArraySingleton.getInstance().getImages()
                );

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagesArraySingleton.getInstance().clear();
    }
}
