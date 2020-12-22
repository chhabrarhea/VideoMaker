package com.example.videomaker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.videomaker.util.BitmapHelper;

import java.io.IOException;

public class SegmentDetailActivity extends AppCompatActivity {
int pos;
ImageButton play;
ImageButton stop;
BitmapHelper helper;
LinearLayout linearLayout;
MediaPlayer mediaPlayer;
boolean started=false;
String audioURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_detail);
        pos=getIntent().getIntExtra("position",-1);
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
    }
    public void chooseAudio(View view){}
}