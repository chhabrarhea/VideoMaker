package com.example.videomaker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.videomaker.util.BitmapHelper;
import com.example.videomaker.util.SegmentAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class SegmentListActivity extends AppCompatActivity {
ArrayList<Bitmap> mBitmaps;
RecyclerView recyclerView;
SegmentAdapter segmentAdapter;
File file1;
File file2;
BitmapHelper helper;
String videoPath="";
String finalVideoPath="";
androidx.appcompat.app.AlertDialog progressDialog;

int t_duration=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_list);
        helper=BitmapHelper.getInstance();
        mBitmaps=helper.getBitmapArrayList();
        recyclerView=findViewById(R.id.myRecycler);
        segmentAdapter=new SegmentAdapter(this,mBitmaps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(segmentAdapter);
        progressDialog=new androidx.appcompat.app.AlertDialog.Builder(SegmentListActivity.this).create();
                progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Generating Video...");
        progressDialog.create();
        progressDialog.setCancelable(false);
       ImageButton go=findViewById(R.id.go);
       go.setOnClickListener(v -> {
           if (helper.allAudioAdded()){
           progressDialog.show();
           Thread gen=new Thread(() -> {
               helper.saveTempBitmap();
               generateTextFiles();
               if (makeVideo())
               {
                   Intent intent=new Intent(SegmentListActivity.this,VideoActivity.class);
                   intent.putExtra("videoPath",finalVideoPath);
                   String [] temp=new String[]{videoPath,file1.getPath(),file2.getPath()};
                   helper.setTempFiles(temp);
                   helper.deleteTempFiles();
                   progressDialog.dismiss();
                   startActivity(intent);
               }
           }
           );
           gen.start();}
           else
               Toast.makeText(this, "Add audio for all images!", Toast.LENGTH_SHORT).show();
       });

    }
    private void generateTextFiles() {
        try{
            File root = new File(Environment.getExternalStorageDirectory(), "VideoMaker11");
            if (!root.exists()) {
                root.mkdirs();
            }
            file1=new File(root,"audios.txt");
            file2=new File(root,"images.txt");
            FileOutputStream fos=new FileOutputStream(file1,false);
            FileOutputStream fos1=new FileOutputStream(file2,false);
            String [] a=helper.getAudioName();
            ArrayList<String> b=helper.getImageName();
            int[] duration=helper.getAudioDuration();
            String audio="";
            String image="";
            for (int i=0;i<a.length;i++)
            {
                audio+="file '"+a[i]+"'\noutpoint "+duration[i]+"\n";
                image+="file '"+b.get(i)+"'\nduration "+duration[i]+"\n";
                t_duration+=duration[i];
            }
            image+="file '"+b.get(b.size()-1)+"'";
            Log.i("MyFiles","Files Created");
            byte[] bytes=audio.getBytes();
            byte[] bytes1=image.getBytes();
            fos.write(bytes);
            fos.flush();
            fos1.write(bytes1);
            fos1.flush();}catch (IOException e)
        {
            Log.i("VideoActivity","Exception: "+e.getMessage());
        }
    }
    private boolean makeVideo(){
        final boolean[] j = {false};
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        videoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/VideoMaker11/"+timeStamp+"tempVideo.mp4";
        finalVideoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/VideoMaker11/"+timeStamp+"outputVideo.mp4";
      int rc= FFmpeg.execute("-f concat -i " + file2.getPath() + " -f concat -i " + file1.getPath() + " -r 25 -pix_fmt yuv420p " + videoPath);
               if (rc == RETURN_CODE_SUCCESS) {
                   Log.i("MyActivity", "Command execution completed successfully.");
                   j[0] =trimVideo(videoPath,t_duration);
               } else if (rc == RETURN_CODE_CANCEL) {
                   Log.i("MyActivity", "Command execution cancelled by user.");
               } else {
                   Log.i("MyActivity", String.format("Command execution failed with rc=%d and the output below.", rc));
                   Config.printLastCommandOutput(Log.INFO);
               }
        return j[0];
    }

    public void goBack(View view)
    {
        super.onBackPressed();

    }
    private boolean trimVideo(String videoPath,int t)
    {
        AtomicBoolean j= new AtomicBoolean(false);
//        int rc = FFmpeg.execute(" -i "+videoPath+" -ss 3 -i "+videoPath+" -c copy -map 1:0 -map 0 -shortest -f nut - | ffmpeg -f nut -i - -map 0 -map -0:0 -c copy "+videoPath);
        int rc=FFmpeg.execute(" -ss 0 -i " + videoPath + " -t " + t + "  -c copy " +finalVideoPath);
            if (rc == RETURN_CODE_SUCCESS) {
                Log.i("MyActivity", "Video Trimming completed successfully.");
               j.set(true);
            } else if (rc == RETURN_CODE_CANCEL) {
                Log.i("MyActivity", "Command execution cancelled by user.");
            } else {
                Log.i("MyActivity", String.format("Command execution failed with rc=%d and the output below.", rc));
                Config.printLastCommandOutput(Log.INFO);
            }
        return j.get();
    }
}