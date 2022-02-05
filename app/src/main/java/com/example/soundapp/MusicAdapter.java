package com.example.soundapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundapp.databinding.MusicAdapterLayoutBinding;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    List<MusicData> list;
    Context context;
    int playingPosition = 0 ;
    MusicChangeListener musicChangeListener;

    public MusicAdapter(List<MusicData> list, Context context ) {
        this.list = list;
        this.context = context;
        this.musicChangeListener = ((MusicChangeListener)context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.music_adapter_layout , parent , false));
    }



    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        MusicData data = list.get(position);

        String generateDuration = String.format(Locale.getDefault() , "%02d:%02d"
        ,TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getDuration()))
        ,TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getDuration()))
        ,TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getDuration()))));

        holder.binding.musicArtist.setText(data.getArtist());
        holder.binding.musicTitle.setText(data.getTitle());
        holder.binding.musicDuration.setText(generateDuration);

        if(data.isPlaying()) {
            playingPosition = position;
            holder.binding.rootLayout.setBackgroundResource(R.drawable.round_back_blue_10);
        } else {
            holder.binding.rootLayout.setBackgroundResource(R.drawable.rounded_back_10);
        }

        holder.binding.rootLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                list.get(playingPosition).setPlaying(false);
                data.setPlaying(true);
                musicChangeListener.onChange(position);
                notifyDataSetChanged();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<MusicData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        MusicAdapterLayoutBinding binding;
        public Holder(@NonNull MusicAdapterLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
