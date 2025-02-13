package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.videomaker.util.BitmapHelper;

import java.io.File;
import java.io.IOException;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class SegmentDetailActivity extends AppCompatActivity {
int pos;
ImageButton play;
ImageButton stop;
BitmapHelper helper;
LinearLayout linearLayout;
MediaPlayer mediaPlayer;
boolean started=false;
String audioURI;
String audioFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_detail);
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"VideoMaker11");
        if (!file.exists())
        file.mkdir();
        pos=getIntent().getIntExtra("position",-1);
        audioFilePath = file.getPath()+"/audio"+pos+".wav";
        helper=BitmapHelper.getInstance();
        ImageView display=findViewById(R.id.image);
        mediaPlayer = new MediaPlayer();
        linearLayout=findViewById(R.id.linear);
        play=findViewById(R.id.play);
        stop=findViewById(R.id.stop);
        display.setImageBitmap(helper.getBitmap(pos));
        audioURI=helper.getAudio(pos);
        if (audioURI==null)
            linearLayout.setVisibility(View.GONE);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                  play.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_24));
            }
        });
    }
    public void playAudio(View view) throws IOException {
        if (!started)
        {
            mediaPlayer.setDataSource(audioURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            play.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
            started=true;
        }
        else if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            play.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_24));
        }
        else
        {
            mediaPlayer.start();
            play.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        }
    }
    public void stopAudio(View view)
    {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void recordAudio(View view){
        Intent intent=new Intent(SegmentDetailActivity.this,RecordAudioActivity.class);
        intent.putExtra("position",pos);
        startActivity(intent);
        finish();
    }
    public void chooseAudio(View view){
        Intent intent_upload = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent_upload,1);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                String path = getRealPathFromURI(SegmentDetailActivity.this, uri);
                File audio = new File(path);
                    convertFile(audio);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void convertFile(File file) {
        if(file == null || !file.exists()){
            Toast.makeText(this, "File does not exists", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!file.canRead()){
            Log.i("MyActivity","Can't read the file. Missing permission?");
            return;
        }


        int rc = FFmpeg.execute("-y -i '"+file.getPath()+"' "+audioFilePath);
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            File convert=new File(audioFilePath);
            storeURI(convert);
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
        }



    public void storeURI(File file) {
        BitmapHelper helper=BitmapHelper.getInstance();
        helper.addAudio(file.getAbsolutePath(),pos,file.getName(),getDuration(file));
        Log.i("MyActivity",pos+"");
        audioURI=file.getAbsolutePath();
        linearLayout.setVisibility(View.VISIBLE);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);}

    private static Integer getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr)/1000;
    }
}