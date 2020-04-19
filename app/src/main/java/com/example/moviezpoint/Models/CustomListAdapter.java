package com.example.moviezpoint.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moviezpoint.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] halls;
    private String[] locations;
    private int[] image_logo;

    public CustomListAdapter(Context context, String[] halls, String[] locations, int[] image_logo) {
        super(context, R.layout.custom_list,halls);

        this.context = context;
        this.halls = halls;
        this.image_logo = image_logo;
        this.locations = locations;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_list,null,true);
        TextView textHalls = view.findViewById(R.id.text_hall_name);
        ImageView imageLogo = view.findViewById(R.id.image_logo);
        TextView textLocations = view.findViewById(R.id.address);
        textHalls.setText(halls[position]);
        textLocations.setText(locations[position]);
        imageLogo.setImageResource(image_logo[position]);
        return view;
    }
}
