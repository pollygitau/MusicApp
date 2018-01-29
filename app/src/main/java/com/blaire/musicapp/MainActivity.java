package com.blaire.musicapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //declare variables for the widgets
    TextView artistnm, start, end ,sngtitle, artistname, songtitle;
    ImageView play,forward,rewind,cover;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Uri mediaUri,imageUri;
    public boolean isNotPlaying;

    DatabaseReference myRef ;

    private int forwardSeekTime = 1*1000;
    private int rewindSeekTime = 1*1000;
    private int currentTime,durationTime;
    String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        artistname = findViewById(R.id.textViewartistName);
        songtitle = findViewById(R.id.textViewSongTitle);
        artistnm = findViewById(R.id.textViewartist);
        start = findViewById(R.id.textViewstart);
        end = findViewById(R.id.textViewend);
        sngtitle = findViewById(R.id.textViewsongtitle);
        forward = findViewById(R.id.imageViewforward);
        play = findViewById(R.id.imageViewplay);
        rewind = findViewById(R.id.imageViewrewind);
        cover  = findViewById(R.id.imageViewalbumcover);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        isNotPlaying = true;
        myRef = FirebaseDatabase.getInstance().getReference().child("01");


        //retrieve media from Firebase cloud storage
        try{
            mediaUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/music-app-e4f38.appspot.com/o/10%20-%20New%20Rules.mp3?alt=media&token=9d95ebcd-ec0d-47ca-9aaa-f40566c49d39");
            mediaPlayer.setDataSource(MainActivity.this,mediaUri);
            mediaPlayer.prepare();
        } catch(IOException e){
            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        //retrieve image from Firebase cloud storage
        imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/music-app-e4f38.appspot.com/o/New_Rules_(Official_Single_Cover)_by_Dua_Lipa.png?alt=media&token=fd4245ea-e404-41d8-a170-51f4eec1165b");
        Picasso.with(this).load(imageUri).placeholder(R.drawable.ic_action_default).into(cover);

        //retrieve values from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //read data from the database
                String artist = dataSnapshot.child("artist").getValue().toString();
                String title = dataSnapshot.child("title").getValue().toString();
                artistname.setText(artist);
                songtitle.setText(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rewind();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forward();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                durationTime = mediaPlayer.getDuration()/1000;
                duration = String.format("%02d:%02d", durationTime/60,durationTime%60);
                end.setText(duration);
            }
        });
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void playPause(){

        if(isNotPlaying){
            mediaPlayer.start();
            new MusicProgress().execute();
            isNotPlaying = false;
            play.setImageResource(R.drawable.ic_action_pause);
        }else{
            mediaPlayer.pause();
            isNotPlaying = true;
            play.setImageResource(R.drawable.ic_action_play);
        }
    }

    public class MusicProgress extends AsyncTask<Void, Integer,Void > {

        @Override
        protected Void doInBackground(Void... voids) {
            do {
                currentTime = mediaPlayer.getCurrentPosition()/1000;
                publishProgress(currentTime);
            }while(seekBar.getProgress()<=mediaPlayer.getDuration()/1000);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try{
                seekBar.setProgress(values[0]);
                String currentString = String.format("%02d:%02d",values[0]/60,values[0]%60);
                start.setText(currentString);
            }catch (Exception e){
                Toast.makeText(MainActivity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void rewind(){
        if(mediaPlayer!=null) {
            int songTime = mediaPlayer.getCurrentPosition();
            if (songTime-rewindSeekTime>=0) {
                mediaPlayer.seekTo(songTime-rewindSeekTime);
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }

    public void forward(){
        if(mediaPlayer!=null){
            int songTime = mediaPlayer.getCurrentPosition();
            if(songTime+forwardSeekTime>=mediaPlayer.getDuration()){
                mediaPlayer.seekTo(songTime+forwardSeekTime);
            }else{
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        isNotPlaying = true;
    }
}
