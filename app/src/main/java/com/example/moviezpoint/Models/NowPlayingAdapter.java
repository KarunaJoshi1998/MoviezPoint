package com.example.moviezpoint.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.Activities.PreviewActivity;
import com.example.moviezpoint.Models.NowPlayingData;
import com.example.moviezpoint.Models.URLs;
import com.example.moviezpoint.R;

public class NowPlayingAdapter extends BaseAdapter {

    private Context context;
    private final NowPlayingData nowPlayingData;

    public NowPlayingAdapter(Context context, NowPlayingData nowPlayingData) {
        this.context = context;
        this.nowPlayingData = nowPlayingData;
    }

    @Override
    public int getCount() {
        return nowPlayingData.getResults().size();
    }

    @Override
    public Object getItem(int position) {
        return nowPlayingData.getResults().get(position);
    }

    @Override
    public long getItemId(int position) {
        return nowPlayingData.getResults().get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.recycler_layout,parent,false);

        ImageView imageView = convertView.findViewById(R.id.imageprofile);
        TextView textView = convertView.findViewById(R.id.textname);
        textView.setText(nowPlayingData.getResults().get(position).getTitle());
        String posterPath = nowPlayingData.getResults().get(position).getPosterPath();
        if (posterPath==null) {
            imageView.setImageResource(R.drawable.no_poster);
        }
        else {
            Glide.with(convertView)
                    .load(URLs.IMAGE_BASE_URL+nowPlayingData.getResults().get(position).getPosterPath())
                    .into(imageView);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra("movie_id",getItemId(position));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
