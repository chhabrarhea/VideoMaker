package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.videomaker.util.BitmapHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class VideoActivity extends AppCompatActivity {
BitmapHelper helper;
    File file1;
    File file2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        helper=BitmapHelper.getInstance();
        generateTextFiles();
        generateVideo();

    }

    private void generateTextFiles() {
        try{
            File root = new File(Environment.getExternalStorageDirectory(), "VideoMaker");
            if (!root.exists()) {
                root.mkdirs();
            }
         file1=new File(root,"audios.txt");
         file2=new File(root,"images.txt");
        FileOutputStream fos=new FileOutputStream(file1,false);
        FileOutputStream fos1=new FileOutputStream(file2,false);
        String [] a=helper.getAudioName();
        ArrayList<String> b=helper.getImageName();
        String audio="";
        String image="";
        for (int i=0;i<a.length;i++)
        {
            audio+="file '"+a[i]+"'\n";
            image+="file '"+b.get(i)+"'\n";
            Log.i("MyActivity",audio+" "+image);
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
    private void generateVideo(){
        String outputPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/VideoMaker/outputVideo.mp4";
        int rc = FFmpeg.execute(" -r 24 -f concat -safe 0 -i "+file2.getPath()+" -f concat -safe 0 -i "+file1.getPath()+" -c:a aac -pix_fmt yuv420p -crf 23 -r 24 -shortest -y "+outputPath);
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i("MyActivity", "Command execution completed successfully.");
            Toast.makeText(this, "Video Generated", Toast.LENGTH_SHORT).show();
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i("MyActivity", "Command execution cancelled by user.");
        } else {
            Log.i("MyActivity", String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }


}