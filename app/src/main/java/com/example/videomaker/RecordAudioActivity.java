package com.example.videomaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class RecordAudioActivity extends AppCompatActivity {
//    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private RelativeLayout root1;
    private static String audioFilePath;
    private  ImageButton stopButton;
    private  Button playButton;
    private  Button pauseButton;
    private Chronometer chronometer;
    private  ImageButton recordButton;
    private boolean isRecording = false;
    private boolean isStarted = false;
    private int pos;
    private WaveRecorder recorder;
    private final int permission=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        pauseButton=findViewById(R.id.pauseButton);
        stopButton =  findViewById(R.id.stopButton);
        root1=findViewById(R.id.root);
        chronometer=findViewById(R.id.timer);

        if (!hasMicrophone()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
            Toast.makeText(this, "No microphone found!", Toast.LENGTH_SHORT).show();
        }else if(ActivityCompat.checkSelfPermission(RecordAudioActivity.this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RecordAudioActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},permission);
        }
        else {
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
        }



         pos=getIntent().getIntExtra("position",-1);
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio"+pos+".wav";
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
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
//            mediaRecorder.setOutputFile(audioFilePath);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.prepare();
            recorder=new WaveRecorder(audioFilePath);
            recorder.startRecording();
            chronometer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        mediaRecorder.start();
    }
    public void pauseAudio(View view) {
        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }
    public void stopAudio (View view) {

        stopButton.setEnabled(false);
        playButton.setEnabled(true);

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
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }
    public void playAudio (View view) throws IOException {
        if(!isStarted){
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);

            mediaPlayer = new MediaPlayer();
            File file = new File(audioFilePath);
            file.setReadable(true,false);
            FileInputStream inputStream = new FileInputStream(file);
            mediaPlayer.setDataSource(inputStream.getFD());

            inputStream.close();
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isStarted=true;
                }


            });
        }
        else
        {
            mediaPlayer.start();
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }

        Log.i("record",""+mediaPlayer.getDuration());
    }
    public void saveAudio(View view) {
    convertFile(new File(audioFilePath));
}
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==permission && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
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
    private void convertFile(File file) {
        if(file == null || !file.exists()){
            Toast.makeText(this, "File does not exists", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!file.canRead()){
            Log.i("MyActivity","Can't read the file. Missing permission?");
            return;
        }
        File convert=getConvertedFilePath(file,"mp3");
        int rc = FFmpeg.execute("-y -i "+file.getPath()+" "+convert.getPath());
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            storeURI(convert);
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }
    private static File getConvertedFilePath(File originalFile, String format){
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format);
        return new File(filePath);
    }

    public void storeURI(File file)
    {
        BitmapHelper helper=BitmapHelper.getInstance();
        helper.addAudio(file.getAbsolutePath(),pos,file.getName());
        Log.i("Selected",file.getAbsolutePath()+" "+file.getPath());
        Intent intent=new Intent(RecordAudioActivity.this,SegmentDetailActivity.class);
        intent.putExtra("position",pos);
        startActivity(intent);
        finish();
    }
}