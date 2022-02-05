package com.example.soundapp.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundapp.MusicData;
import com.example.soundapp.R;
import com.example.soundapp.databinding.MusicAdapterLayoutBinding;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdapterLocal extends RecyclerView.Adapter<AdapterLocal.Holder> {
    List<LocalData> list;
    Context context;
    int playingPosition = 0 ;
    LocalInterface localInterface;

    public AdapterLocal(List<LocalData> list, Context context, LocalInterface localInterface) {
        this.list = list;
        this.context = context;
        this.localInterface = localInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.music_adapter_layout , parent , false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        LocalData data = list.get(position);

//        String generateDuration = String.format(Locale.getDefault() , "%02d:%02d"
//                ,TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getTime()))
//                ,TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getTime()))
//                ,TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(data.getTime()))));

        holder.binding.musicArtist.setText(data.getName());
//        holder.binding.musicTitle.setText(data.getTime());
        holder.binding.musicDuration.setText(data.getTime());

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
                localInterface.onClick(position);
                notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<LocalData> list) {
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
