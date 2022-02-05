package com.example.soundapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.soundapp.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MusicChangeListener {
    ActivityMainBinding binding;
    List<MusicData> list;
    MusicAdapter musicAdapter;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false ;
    Timer timer;
    int currentMusicListPosition = 0 ;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // show data + full screen and hide NAVIGATION System bar .
        View decodeView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decodeView.setSystemUiVisibility(options);

        // remove states bar .
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);


        list = new ArrayList<>();
        mediaPlayer= new MediaPlayer();

        // check Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) {
            // is true .
            getMusicFiles();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 11);
            } else {
                getMusicFiles();
            }
        }

        binding.nextBtn.setOnClickListener(view -> {
            int nextMusicListenerPosition = currentMusicListPosition+1;

            if (nextMusicListenerPosition >= list.size()) {
                nextMusicListenerPosition = 0 ;
            }

            list.get(currentMusicListPosition).setPlaying(false);
            list.get(nextMusicListenerPosition).setPlaying(true);
            musicAdapter.updateList(list);
            binding.musicRecycler.scrollToPosition(nextMusicListenerPosition);
            onChange(nextMusicListenerPosition);
        });

        binding.previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int perMusicListenerPosition = currentMusicListPosition-1;

                if (perMusicListenerPosition < 0) {
                    perMusicListenerPosition = list.size()-1 ; // play last song .
                }

                list.get(currentMusicListPosition).setPlaying(false);
                list.get(perMusicListenerPosition).setPlaying(true);
                musicAdapter.updateList(list);
                binding.musicRecycler.scrollToPosition(perMusicListenerPosition);
                onChange(perMusicListenerPosition);
            }
        });

        binding.playPauseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    isPlaying = false;
                    mediaPlayer.pause();
                    binding.playPauseImg.setImageResource(R.drawable.ic_play);
                }
                else {
                    isPlaying = true;
                    mediaPlayer.start();
                    binding.playPauseImg.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        binding.playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    if (isPlaying) {
                        mediaPlayer.seekTo(i);
                    }
                    else {
                        mediaPlayer.seekTo(0);
                    }
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

    // get sound from mobile and featch to list .
    @SuppressLint("Range")
    private void getMusicFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor cursor = contentResolver.query(uri , null , MediaStore.Audio.Media.DATA+" LIKE?" , new String[]{"%.mp3%"},null);

        if (cursor == null) {
            Toast.makeText(this, "something went wrong !", Toast.LENGTH_SHORT).show();
        } else if(!cursor.moveToNext()) {
            Toast.makeText(this, "no music found !", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") final String getMusicName= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("Range") final String getMusicFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") long cursorId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cursorId);
                String getDuration = "00:00";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                }
                final MusicData musicData = new MusicData(getMusicFileName,getMusicName,getDuration , false , musicFileUri);
                list.add(musicData);
            }
            musicAdapter = new MusicAdapter(list , MainActivity.this);
            binding.musicRecycler.setAdapter(musicAdapter);
            binding.musicRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        assert cursor != null;
        cursor.close();
    }

    // Result Permission .
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();
        } else {
            Toast.makeText(this, "Permissions Declined by User", Toast.LENGTH_SHORT).show();
        }
    }

    // show data + full screen and hide NAVIGATION System bar .
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            View decodeView = getWindow().getDecorView();
            int options = View.SYSTEM_UI_FLAG_FULLSCREEN| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decodeView.setSystemUiVisibility(options);

        }
    }

    @Override
    public void onChange(int position) {
        currentMusicListPosition = position;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Thread(() -> {
            try {
                mediaPlayer.setDataSource(MainActivity.this , list.get(position).getUri());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Unable to play track", Toast.LENGTH_SHORT).show();
            }
        }).start();

        mediaPlayer.setOnPreparedListener(mp -> {
            final int getTotalDuration = mp.getDuration();

            String generateDuration = String.format(Locale.getDefault() , "%02d:%02d"
                    ,TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)
                    ,TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)
                    ,TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));

            binding.endTime.setText(generateDuration);
            isPlaying = true;
            mp.start();
            binding.playerSeekBar.setMax(getTotalDuration);
            binding.playPauseImg.setImageResource(R.drawable.ic_pause);
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    // covert time to second
                    final int getCurrentDuration = mediaPlayer.getCurrentPosition();
                    String generateDuration = String.format(Locale.getDefault() , "%02d:%02d"
                            ,TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)
                            ,TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)
                            ,TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                    // send time to seekbar
                    binding.playerSeekBar.setProgress(getCurrentDuration);
                    // send start time to seekbar
                    binding.startTime.setText(generateDuration);
                });

            }
        },1000,1000);
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.reset();
                timer.purge();
                timer.cancel();
                isPlaying = false;
                binding.playPauseImg.setImageResource(R.drawable.ic_play);
                binding.playerSeekBar.setProgress(0);
            });
    }
}