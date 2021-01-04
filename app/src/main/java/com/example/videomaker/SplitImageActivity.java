package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.videomaker.util.BitmapHelper;
import com.example.videomaker.util.CustomImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplitImageActivity extends AppCompatActivity {
    String uri;
    private final String TAG = "SplitActivity";
    CustomImageView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = getIntent().getStringExtra("uri");
        setContentView(R.layout.activity_split_image);
        myView = findViewById(R.id.display);
        myView.setImageWithGlide(this, uri);

    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void splitImage(View view) {
        myView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) myView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        List<Integer> splitY = myView.getAllY();
        if (splitY.size() > 0) {
            int original=bitmap.getHeight();
            bitmap = Bitmap.createScaledBitmap(bitmap, myView.getWidth(), myView.getHeight(), false);
            Collections.sort(splitY);
            int count = splitY.size();
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            Log.i(TAG, bitmap.getHeight() + "");
            for (int i = 0; i <= count; ++i) {
                Bitmap temp;
                if (i == 0)
                    temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), splitY.get(i));
                else if (i == count)
                    temp = Bitmap.createBitmap(bitmap, 0, splitY.get(count - 1), bitmap.getWidth(), bitmap.getHeight() - (splitY.get(count - 1)));
                else {
                    int height = splitY.get(i) - splitY.get(i - 1);
                    temp = Bitmap.createBitmap(bitmap, 0, splitY.get(i - 1), bitmap.getWidth(), height);
                }
                temp = Bitmap.createScaledBitmap(temp, bitmap.getWidth(),original/2, false);
                bitmaps.add(temp);
            }
            BitmapHelper helper = BitmapHelper.getInstance();
            helper.setBitmapArrayList(bitmaps);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), myView.getHeight()/2, false);
            BitmapHelper helper = BitmapHelper.getInstance();
            helper.setBitmapArrayList(new ArrayList<>(Collections.singletonList(bitmap)));
        }
        Intent intent = new Intent(SplitImageActivity.this, SegmentListActivity.class);
        startActivity(intent);
    }


}