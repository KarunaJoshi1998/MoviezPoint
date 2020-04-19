package com.example.moviezpoint.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviezpoint.Models.GetNotificationsModel;
import com.example.moviezpoint.Models.NotificationsRecyclerAdapter;
import com.example.moviezpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerViewNotifications;
    MKLoader loader;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    String uID;

    List<GetNotificationsModel> modelList = new ArrayList<>();

    public NotificationsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications,container,false);

        recyclerViewNotifications = rootView.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNotifications.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        uID = firebaseUser.getUid();

        loader = rootView.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        getNotifications();

        return rootView;
    }

    private void getNotifications() {

        reference.child("Notifications").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GetNotificationsModel getNotificationsModel = snapshot.getValue(GetNotificationsModel.class);
                    modelList.add(getNotificationsModel);
                }
                NotificationsRecyclerAdapter adapter = new NotificationsRecyclerAdapter(getContext(),modelList);
                recyclerViewNotifications.setAdapter(adapter);
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
