package com.example.moviezpoint.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.R;

public class YoutubeRecyclerAdapter extends RecyclerView.Adapter<YoutubeRecyclerAdapter.YoutubeViewHolder> {

    private Context context;
    private YoutubeVideoDataModel youtubeVideoDataModel;
    private String imagePath;

    public YoutubeRecyclerAdapter(Context context, YoutubeVideoDataModel youtubeVideoDataModel, String imagePath) {
        this.context = context;
        this.youtubeVideoDataModel = youtubeVideoDataModel;
        this.imagePath = imagePath;
    }

    @NonNull
    @Override
    public YoutubeRecyclerAdapter.YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_recycler_layout,parent,false);
        YoutubeViewHolder holder = new YoutubeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeRecyclerAdapter.YoutubeViewHolder holder, int position) {
        holder.textViewName.setText(youtubeVideoDataModel.getResults().get(position).getName());
        if (imagePath==null) {
            holder.imageView.setImageResource(R.drawable.no_poster);
        }
        else {
            Glide.with(context)
                    .load(URLs.IMAGE_BASE_URL+imagePath)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return youtubeVideoDataModel.getResults().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view) {
        super.onAttachedToRecyclerView(view);
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewName;
        ImageView imageView;

        public YoutubeViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardViewLayout);
            textViewName = itemView.findViewById(R.id.actor_name);
            imageView = itemView.findViewById(R.id.image_cast);

        }
    }
}
