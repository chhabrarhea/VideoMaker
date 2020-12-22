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
private final String TAG="SplitActivity";
CustomImageView myView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri=getIntent().getStringExtra("uri");
        setContentView(R.layout.activity_split_image);
        myView=findViewById(R.id.display);
        myView.setImageWithGlide(this,uri);

    }

    public void goBack(View view)
    {
        super.onBackPressed();
        finish();
    }

    public void splitImage(View view)
    {
        myView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) myView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        bitmap=Bitmap.createScaledBitmap(bitmap,myView.getWidth(),myView.getHeight(),false);
        List<Integer> splitY=myView.getAllY();
        Collections.sort(splitY);
        int count=splitY.size();
        ArrayList<Bitmap> bitmaps=new ArrayList<>();
        Log.i(TAG,bitmap.getHeight()+"");
         for(int i=0;i<=count;++i)
         {
            Bitmap temp;
             if (i==0)
                 temp=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),splitY.get(i));
             else if (i==count)
                temp=Bitmap.createBitmap(bitmap,0,splitY.get(count-1),bitmap.getWidth(),bitmap.getHeight()-(splitY.get(count-1)));
             else {
                 int height=splitY.get(i) - splitY.get(i - 1);
                 temp = Bitmap.createBitmap(bitmap, 0, splitY.get(i - 1), bitmap.getWidth(),height);
             }
             temp=Bitmap.createScaledBitmap(temp,bitmap.getWidth(),bitmap.getHeight()/2,false);
             bitmaps.add(temp);
         }
         BitmapHelper helper=BitmapHelper.getInstance();
         helper.setBitmapArrayList(bitmaps);
         Intent intent=new Intent(SplitImageActivity.this, SegmentListActivity.class);
         startActivity(intent);
    }


}