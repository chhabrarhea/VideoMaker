package com.example.videomaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.videomaker.util.BitmapHelper;
import com.github.squti.androidwaverecorder.WaveRecorder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class RecordAudioActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
//    private static MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private RelativeLayout root1;
    private static String audioFilePath;
    private  ImageButton stopButton;
    private  Button playButton;
    private  Button pauseButton;
    private  Button saveButton;
    private Chronometer chronometer;
    private  ImageButton recordButton;
    private boolean isRecording = false;
    private boolean isStarted = false;
    private int pos;
    private WaveRecorder recorder;
    private final int microphone=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        pauseButton=findViewById(R.id.pauseButton);
        stopButton =  findViewById(R.id.stopButton);
        saveButton=findViewById(R.id.save);
        root1=findViewById(R.id.root);
        chronometer=findViewById(R.id.timer);
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        if (!hasMicrophone()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
            Toast.makeText(this, "No microphone found!", Toast.LENGTH_SHORT).show();
        }else if(ActivityCompat.checkSelfPermission(RecordAudioActivity.this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RecordAudioActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},microphone);
        }
        else if (ActivityCompat.checkSelfPermission(RecordAudioActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(RecordAudioActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
            recordButton.setEnabled(false);
            saveButton.setEnabled(false);
            Toast.makeText(this, "Grant Storage Permission from App Settings", Toast.LENGTH_SHORT).show();
        }
        else {
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            saveButton.setEnabled(false);
        }



         pos=getIntent().getIntExtra("position",-1);
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"VideoMaker11");
        if (!file.exists())
            file.mkdir();
        audioFilePath = file.getPath()+"/audio"+pos+".wav";
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE); }
    public void recordAudio (View view) throws IOException {
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        try {
//            mediaRecorder = new MediaRecorder();
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mediaRecorder.setOutputFile(audioFilePath);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.prepare();
//            mediaRecorder.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            recorder=new WaveRecorder(audioFilePath);
            recorder.startRecording();
            chronometer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void pauseAudio(View view) {
        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }
    public void stopAudio (View view) {

        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        saveButton.setEnabled(true);

        if (isRecording)
        {
            chronometer.stop();
            recorder.stopRecording();
            recordButton.setEnabled(false);
//            mediaRecorder.stop();
//            mediaRecorder.reset();
//            mediaRecorder.release();
//            mediaRecorder = null;
            isRecording = false;
        }
    }
    public void playAudio (View view) throws IOException {
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        if(!isStarted){
            File file = new File(audioFilePath);
            file.setReadable(true,false);
            FileInputStream inputStream = new FileInputStream(file);
            mediaPlayer.setDataSource(inputStream.getFD());
            inputStream.close();
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                isStarted=true;
            });
        }
        else
        {
            mediaPlayer.start();
        }


        Log.i("record",""+mediaPlayer.getDuration());
    }
    public void saveAudio(View view) {
    storeURI(new File(audioFilePath));
}
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==microphone && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
            recordButton.setEnabled(false);
            saveButton.setEnabled(false);
        }
        else
        {
            Snackbar.make(root1, "Permission required to continue.", Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).show();
        }
    }
    @Override public void onDestroy() {
        super.onDestroy();
    }


    public void storeURI(File file)
    {
        BitmapHelper helper=BitmapHelper.getInstance();

        helper.addAudio(file.getAbsolutePath(),pos,file.getName(),getDuration(file));
        Log.i("Selected",file.getAbsolutePath()+" "+file.getPath());
        Intent intent=new Intent(RecordAudioActivity.this,SegmentDetailActivity.class);
        intent.putExtra("position",pos);
        startActivity(intent);
        finish();
    }
    private static Integer getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr)/1000;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

         pauseButton.setEnabled(false);
         playButton.setEnabled(true);

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RecordAudioActivity.this,SegmentDetailActivity.class);
        intent.putExtra("position",pos);
        startActivity(intent);
        finish();
    }
}