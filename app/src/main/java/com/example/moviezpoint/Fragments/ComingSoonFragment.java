package com.example.moviezpoint.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.Activities.ComingSoonPreviewActivity;
import com.example.moviezpoint.Activities.PreviewActivity;
import com.example.moviezpoint.Models.ApiInterface;
import com.example.moviezpoint.Models.ComingSoonAdapter;
import com.example.moviezpoint.Models.ComingSoonData;
import com.example.moviezpoint.Models.MoviesHomeAdapter;
import com.example.moviezpoint.Models.URLs;
import com.example.moviezpoint.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ComingSoonFragment extends Fragment {

    GridView listView;
    MKLoader loader;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URLs.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    public ComingSoonFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon,container,false);

        listView = rootView.findViewById(R.id.listView);
        loader = rootView.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        if (isConnected()) {
            initializeAdapter();
        }
        else {
            loader.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void initializeAdapter() {

        Call<ComingSoonData> comingSoonDataCall = apiInterface.COMING_SOON_DATA_CALL();
        comingSoonDataCall.enqueue(new Callback<ComingSoonData>() {
            @Override
            public void onResponse(Call<ComingSoonData> call, Response<ComingSoonData> response) {
                if (response.isSuccessful()) {
                    ComingSoonData comingSoonData = response.body();
                    ComingSoonAdapter adapter = new ComingSoonAdapter(getContext(),comingSoonData);
                    listView.setAdapter(adapter);
                    loader.setVisibility(View.GONE);
                }

                else {
                    Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ComingSoonData> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loader.setVisibility(View.GONE);
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null!=activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
