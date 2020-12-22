package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;

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
}