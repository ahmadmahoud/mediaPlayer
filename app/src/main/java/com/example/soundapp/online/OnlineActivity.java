package com.example.soundapp.online;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.soundapp.R;
import com.example.soundapp.databinding.ActivityOnlineBinding;

public class OnlineActivity extends AppCompatActivity {
    ActivityOnlineBinding binding;
    MediaPlayer mediaPlayer;
    Handler handler;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_online);

        binding.seekbarr.setMax(100);
        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        prepareMediaPlayer();

        // play sound .
        binding.play.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                handler.removeCallbacks(update);
                mediaPlayer.pause();
                binding.play.setImageResource(R.drawable.ic_pause);
            } else {
                mediaPlayer.start();
                binding.play.setImageResource(R.drawable.ic_play);
                updateSeekBar();
            }
        });

        // On Touch click on SeekBar
        binding.seekbarr.setOnTouchListener((v, event) -> {
            SeekBar seekBar = (SeekBar) v;
            int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
            mediaPlayer.seekTo(playPosition);
            binding.startText.setText(milliSecondsToTime(mediaPlayer.getCurrentPosition()));
            return false;
        });

        // download song immediately , before click.
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) ->
                binding.seekbarr.setSecondaryProgress(percent));

        // after Completion
        mediaPlayer.setOnCompletionListener(mp -> {
            binding.seekbarr.setProgress(0);
            binding.play.setImageResource(R.drawable.ic_play);
            binding.startText.setText("0:0");
            binding.endText.setText("0:0");
            mediaPlayer.reset();
            prepareMediaPlayer();
        });

    }

    // link amd data .
    @SuppressLint("LongLogTag")
    private void prepareMediaPlayer() {
        try {

            // upload google drive then ( https://www.wonderplugin.com/ ) to convert link to direct .
            mediaPlayer.setDataSource("https://drive.google.com/uc?export=download&id=1qfkJWxAua0ynBJwcIMbmmTy7S2u1i1nk");
            mediaPlayer.prepare();
            binding.endText.setText(milliSecondsToTime(mediaPlayer.getDuration()));
            binding.seekbarr.setProgress(mediaPlayer.getCurrentPosition());
        } catch (Exception e) {
            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // thread ( delay )
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.startText.setText(milliSecondsToTime(currentDuration));
        }
    };

    // update SeekBar through playing.
    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            binding.seekbarr.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(update , 1000);
        }
    }

    // convert time.
    private String milliSecondsToTime(long milliSeconds) {
        String timeString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours >0) {
            timeString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        timeString = timeString + minutes + ":" + secondsString;
        return timeString;
    }

}