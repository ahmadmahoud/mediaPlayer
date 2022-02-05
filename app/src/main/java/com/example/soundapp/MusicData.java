package com.example.soundapp;

import android.net.Uri;

public class MusicData {

    private String title , artist , duration ;
    private boolean isPlaying;
    Uri uri;

    public MusicData(String title, String artist, String duration, boolean isPlaying , Uri uri) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Uri getUri() {
        return uri;
    }


}
