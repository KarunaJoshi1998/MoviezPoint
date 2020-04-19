package com.example.moviezpoint.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviezpoint.R;

import java.util.List;

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationsViewHolder> {
    Context context;
    List<GetNotificationsModel> modelList;

    public NotificationsRecyclerAdapter(Context context, List<GetNotificationsModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_notification_card,parent,false);

        NotificationsViewHolder viewHolder = new NotificationsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        GetNotificationsModel notificationsModel = modelList.get(position);
        holder.textViewTitle.setText(notificationsModel.getTitle());
        holder.textViewDescription.setText(notificationsModel.getDescription());
        holder.textViewAmount.setText("RS "+notificationsModel.getAmountPaid());
        holder.textViewStatus.setText("Status : "+notificationsModel.getStatus());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewAmount, textViewStatus;
        public NotificationsViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_notification_title);
            textViewDescription = itemView.findViewById(R.id.text_notification_description);
            textViewAmount = itemView.findViewById(R.id.text_notification_amount);
            textViewStatus = itemView.findViewById(R.id.text_notification_status);
        }
    }
}
