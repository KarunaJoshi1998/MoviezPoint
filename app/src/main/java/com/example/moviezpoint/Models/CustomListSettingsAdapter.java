package com.example.moviezpoint.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moviezpoint.R;

public class CustomListSettingsAdapter extends ArrayAdapter<String> {

    private Context context;
    private final String title[];
    private final int drawables[];
    public CustomListSettingsAdapter(@NonNull Context context, String title[], int drawables[]) {
        super(context, R.layout.custom_list_settings,title);
        this.context = context;
        this.title = title;
        this.drawables = drawables;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_list_settings,null,true);
        TextView textView = convertView.findViewById(R.id.item_settings_title);
        textView.setText(title[position]);
        textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(drawables[position]),null,null,null);
        return convertView;
    }
}
