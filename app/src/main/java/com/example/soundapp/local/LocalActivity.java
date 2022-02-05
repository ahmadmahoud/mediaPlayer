package com.example.soundapp.local;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soundapp.R;
import com.example.soundapp.databinding.ActivityLocalBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class LocalActivity extends AppCompatActivity implements LocalInterface {
    ActivityLocalBinding binding;
    AdapterLocal adapter;
    List<LocalData> list;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false;
    Timer timer;
    Handler handler;
    int currentMusicListPosition = 0;
    private static final String TAG = "Cannot invoke method length() on null object";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local);
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        list = new ArrayList<>();
        binding.playerSeekBar.setMax(100);
        binding.playerSeekBar.setProgress(0);

        int index = getIntent().getIntExtra("name", 0);
        if (index == 0) {
            getMusicFiles();
        } else if (index == 1) {
            getMusicFiles2();
        }

        binding.playerSeekBar.setOnTouchListener((v, event) -> {
            SeekBar seekBar = (SeekBar) v;
            int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
            mediaPlayer.seekTo(playPosition);
            binding.startTime.setText(milliSecondsToTime(mediaPlayer.getCurrentPosition()));
            return false;
        });


    }

    private void getMusicFiles() {
        list.add(new LocalData("ابا الانبياء", R.raw.aba, "0:42"));
        list.add(new LocalData("اتدري من يزيل الهم", R.raw.aba2, "1:04"));
        list.add(new LocalData("ابوح بشوق", R.raw.aba3, "0:32"));
        adapter = new AdapterLocal(list, this, this);
        binding.musicRecycler.setAdapter(adapter);
        binding.musicRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.nextBtn.setOnClickListener(v -> next(list));
        binding.previousBtn.setOnClickListener(v -> previous(list));
        binding.playPauseImg.setOnClickListener(v -> play());
    }

    private void getMusicFiles2() {
        list.add(new LocalData("ابا الانبياء", R.raw.aba, "12:00"));
        list.add(new LocalData("ابا الانبياء", R.raw.aba2, "12:00"));
        adapter = new AdapterLocal(list, this, this);
        binding.musicRecycler.setAdapter(adapter);
        binding.musicRecycler.setLayoutManager(new LinearLayoutManager(this));
        next(list);
        previous(list);
        play();

    }

    private void next(List<LocalData> list) {
        binding.nextBtn.setOnClickListener(view -> {
            int nextMusicListenerPosition = currentMusicListPosition + 1;

            if (nextMusicListenerPosition >= list.size()) {
                nextMusicListenerPosition = 0;
            }

            list.get(currentMusicListPosition).setPlaying(false);
            list.get(nextMusicListenerPosition).setPlaying(true);
            adapter.updateList(list);
            binding.musicRecycler.scrollToPosition(nextMusicListenerPosition);
            onClick(nextMusicListenerPosition);
        });
    }

    private void previous(List<LocalData> list) {
        int perMusicListenerPosition = currentMusicListPosition - 1;
        if (perMusicListenerPosition < 0) {
            perMusicListenerPosition = list.size() - 1; // play last song .
        }
        list.get(currentMusicListPosition).setPlaying(false);
        list.get(perMusicListenerPosition).setPlaying(true);
        adapter.updateList(list);
        binding.musicRecycler.scrollToPosition(perMusicListenerPosition);
        LocalActivity.this.onClick(perMusicListenerPosition);


    }

    private void play() {
        if (isPlaying) {
            isPlaying = false;
            mediaPlayer.pause();
            binding.playPauseImg.setImageResource(R.drawable.ic_play);
        } else {
            isPlaying = true;
            mediaPlayer.start();
            binding.playPauseImg.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
        }
    }

    // update Progress
    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            binding.playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(update, 1000);
        }
    }

    // convert time.
    private String milliSecondsToTime(long milliSeconds) {
        String timeString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
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

    // delays
    private final Runnable update = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.startTime.setText(milliSecondsToTime(currentDuration));
        }
    };

    @Override
    public void onClick(int position) {
        currentMusicListPosition = position;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(this, list.get(position).getId());
        mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(mp -> {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.startTime.setText(milliSecondsToTime(currentDuration));
            binding.endTime.setText(milliSecondsToTime(mediaPlayer.getDuration()));
            isPlaying = true;
            binding.playPauseImg.setImageResource(R.drawable.ic_pause);
        });

        timer = new Timer();
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.reset();
            timer.purge();
            timer.cancel();
            isPlaying = false;
            binding.playPauseImg.setImageResource(R.drawable.ic_play);
            binding.playerSeekBar.setProgress(0);

            if (position >= list.size()) {
                Log.i(TAG, "onClick: "+ position + list.size());
                next(list);
            } else {
                previous(list);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            isPlaying = false;
            mediaPlayer.pause();
            binding.playPauseImg.setImageResource(R.drawable.ic_play);
        }
        updateSeekBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}