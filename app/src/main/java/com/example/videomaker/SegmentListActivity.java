package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.videomaker.util.BitmapHelper;
import com.example.videomaker.util.SegmentAdapter;

import java.util.ArrayList;

public class SegmentListActivity extends AppCompatActivity {
ArrayList<Bitmap> mBitmaps;
RecyclerView recyclerView;
SegmentAdapter segmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_list);
        BitmapHelper helper=BitmapHelper.getInstance();
        mBitmaps=helper.getBitmapArrayList();
        recyclerView=findViewById(R.id.myRecycler);
        segmentAdapter=new SegmentAdapter(this,mBitmaps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(segmentAdapter);

    }

    public void generateVideo(View view)
    {
        BitmapHelper helper=BitmapHelper.getInstance();
        if (helper.allAudioAdded()){
         helper.saveTempBitmap();
        Intent intent=new Intent(SegmentListActivity.this,VideoActivity.class);
        startActivity(intent);
        }
        else
            Toast.makeText(this, "Select Audio for all Images", Toast.LENGTH_SHORT).show();
    }

}