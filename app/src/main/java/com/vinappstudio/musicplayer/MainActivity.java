package com.vinappstudio.musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
        seekBar();

    }

    public void setUp() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.game);


        artistImage = findViewById(R.id.artist_image_id);
        leftTime = findViewById(R.id.left_time_id);
        rightTime = findViewById(R.id.right_time_id);
        seekBar = findViewById(R.id.seekBar_id);
        prevButton = findViewById(R.id.prev_btn_id);
        playButton = findViewById(R.id.play_btn_id);
        nextButton = findViewById(R.id.next_btn_id);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev_btn_id:
                backMusic();
                break;
            case R.id.play_btn_id:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else
                    startMusic();
                break;
            case R.id.next_btn_id:
                nextMusic();
                break;

        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
        }

    }

    public void seekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // this event only happen if event come from user
                if (b) {
                    mediaPlayer.seekTo(i);
                }
                // we need to make sure  the left/right time is in min and sec   so
                // there is already a class  we use it
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration - currentPos)));
                Log.d("TAG", "current position3:" + currentPos + "/n duration" + duration);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null & mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);
                                // update the text

                                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                // old  way
//                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss" )
//                                        .format(new Date(mediaPlayer.getCurrentPosition()))));

                                leftTime.setText(dateFormat.format(new Date(mediaPlayer.getCurrentPosition())));


                                rightTime.setText(dateFormat.format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())));


                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}