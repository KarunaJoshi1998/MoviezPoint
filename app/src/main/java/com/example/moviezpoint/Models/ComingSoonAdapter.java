package com.example.moviezpoint.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.Activities.ComingSoonPreviewActivity;
import com.example.moviezpoint.R;

public class ComingSoonAdapter extends BaseAdapter {

    private Context context;
    private final ComingSoonData comingSoonData;

    public ComingSoonAdapter(Context context, ComingSoonData comingSoonData) {
        this.context = context;
        this.comingSoonData = comingSoonData;
    }

    @Override
    public int getCount() {
        return comingSoonData.getResults().size();
    }

    @Override
    public Object getItem(int position) {
        return comingSoonData.getResults().get(position);
    }

    @Override
    public long getItemId(int position) {
        return comingSoonData.getResults().get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.recycler_layout,parent,false);

        TextView textView = convertView.findViewById(R.id.textname);
        ImageView imageView = convertView.findViewById(R.id.imageprofile);

        textView.setText(comingSoonData.getResults().get(position).getTitle());

        String posterPath = comingSoonData.getResults().get(position).getPosterPath();
        if (posterPath==null) {
            imageView.setImageResource(R.drawable.no_poster);
        }
        else {
            Glide.with(convertView)
                    .load(URLs.IMAGE_BASE_URL+comingSoonData.getResults().get(position).getPosterPath())
                    .into(imageView);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComingSoonPreviewActivity.class);
                intent.putExtra("movie_id",getItemId(position));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
