package com.example.moviezpoint.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.moviezpoint.Models.ApiInterface;
import com.example.moviezpoint.Models.NowPlayingAdapter;
import com.example.moviezpoint.Models.NowPlayingData;
import com.example.moviezpoint.Models.URLs;
import com.example.moviezpoint.R;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NowPlayingFragment extends Fragment {

    GridView listView;
    MKLoader loader;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URLs.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    public NowPlayingFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing,container,false);

        loader = rootView.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        listView = rootView.findViewById(R.id.listView);

        if (isConnected()) {
            initializeAdapter();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Internet")
                    .setCancelable(false)
                    .setMessage("Please Check Your Internet Connection... Connection not Available")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getActivity().finish();
                        }
                    });

            builder.show();
            loader.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void initializeAdapter() {

        Call<NowPlayingData> nowPlayingDataCall = apiInterface.NOW_PLAYING_DATA_CALL();
        nowPlayingDataCall.enqueue(new Callback<NowPlayingData>() {
            @Override
            public void onResponse(Call<NowPlayingData> call, Response<NowPlayingData> response) {
                if (response.isSuccessful()) {
                    NowPlayingData nowPlayingData = response.body();

                    NowPlayingAdapter adapter = new NowPlayingAdapter(getContext(),nowPlayingData);
                    listView.setAdapter(adapter);
                    loader.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<NowPlayingData> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }
        });
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