package com.example.moviezpoint.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CastViewHolder> {

    MovieDetailsModel movieDetailsModel;
    Context context;

    public RecyclerViewAdapter(MovieDetailsModel movieDetailsModel,Context context) {
        this.movieDetailsModel = movieDetailsModel;
        this.context = context;
    }
    public static class CastViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewName;
        ImageView imageView;

          CastViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewLayout);
            textViewName = itemView.findViewById(R.id.actor_name);
            imageView = itemView.findViewById(R.id.image_cast);
        }
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cast_list_layout,parent,false);
        CastViewHolder holder = new CastViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        holder.textViewName.setText(movieDetailsModel.getProductionCompanies().get(position).getName());
        String posterPath = movieDetailsModel.getProductionCompanies().get(position).getLogoPath();

       if (posterPath==null) {
           holder.imageView.setImageResource(R.drawable.no_poster);
       }
       else {
           Glide.with(context)
                   .load(URLs.IMAGE_BASE_URL+movieDetailsModel.getProductionCompanies().get(position).getLogoPath())
                   .into(holder.imageView);
       }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view) {
        super.onAttachedToRecyclerView(view);
    }

    @Override
    public int getItemCount() {
        return movieDetailsModel.getProductionCompanies().size();
    }

}
