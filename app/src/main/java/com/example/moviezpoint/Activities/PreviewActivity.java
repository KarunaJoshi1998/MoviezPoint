package com.example.moviezpoint.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.Models.ApiInterface;
import com.example.moviezpoint.Models.Config;
import com.example.moviezpoint.Models.MovieDetailsModel;
import com.example.moviezpoint.Models.RecyclerViewAdapter;
import com.example.moviezpoint.Models.URLs;
import com.example.moviezpoint.Models.YoutubeRecyclerAdapter;
import com.example.moviezpoint.Models.YoutubeVideoDataModel;
import com.example.moviezpoint.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tuyenmonkey.mkloader.MKLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PreviewActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    Long movie_id;
    String trailer;
    TextView textDescription,textTitle, textDuration, textDate, textTrailer,textGenre,textTitleTrailer,textViewProduction;
    private YouTubePlayerView youTubeView;
    MKLoader loader;
    ScrollView scrollView;
    private YouTubePlayer youTubePlayer;


    RecyclerView recyclerViewYoutube;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerView.LayoutManager RecyclerViewLayoutManagerYoutube;
    RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout;
    LinearLayoutManager HorizontalLayoutYoutube;

    private static final int RECOVERY_REQUEST = 1;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URLs.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        loader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollViewLayout);
        loader.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(PreviewActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);

        recyclerViewYoutube = findViewById(R.id.recyclerViewTrailer);
        RecyclerViewLayoutManagerYoutube = new LinearLayoutManager(getApplicationContext());
        recyclerViewYoutube.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayoutYoutube = new LinearLayoutManager(PreviewActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewYoutube.setLayoutManager(HorizontalLayoutYoutube);


        Intent intent = getIntent();
        movie_id = intent.getLongExtra("movie_id",0);
        textDescription = findViewById(R.id.description);
        textDate = findViewById(R.id.release_date);
        textDuration = findViewById(R.id.duration);
        textTitle = findViewById(R.id.title);
        textTrailer = findViewById(R.id.trailerText);
        textGenre = findViewById(R.id.genre);
        textTitleTrailer = findViewById(R.id.textTrailer);
        textViewProduction = findViewById(R.id.textproduction);

        if (isConnected()) {
            getMovieDetails(movie_id);
        }

        else {
            Toast.makeText(this, "Please check your internet connection... Connection not Available", Toast.LENGTH_SHORT).show();
            loader.setVisibility(View.GONE);
            finish();
        }

        youTubeView = findViewById(R.id.youtube_view);
    }

    private void getYoutubeData(Long movie_id, final String posterPath) {
        Call<YoutubeVideoDataModel> youtubeVideoDataModelCall = apiInterface.YOUTUBE_VIDEO_DATA_MODEL_CALL(movie_id);
        youtubeVideoDataModelCall.enqueue(new Callback<YoutubeVideoDataModel>() {
            @Override
            public void onResponse(Call<YoutubeVideoDataModel> call, Response<YoutubeVideoDataModel> response) {
                if (response.isSuccessful()) {
                    final YoutubeVideoDataModel youtubeVideoDataModel = response.body();

                    int size = youtubeVideoDataModel.getResults().size();
                    if (size == 0) {
                        textTitleTrailer.setVisibility(View.GONE);
                        youTubeView.setVisibility(View.GONE);
                    }
                    startYoutubeData();
                    YoutubeRecyclerAdapter youtubeRecyclerAdapter= new YoutubeRecyclerAdapter(PreviewActivity.this,youtubeVideoDataModel,posterPath);
                    recyclerViewYoutube.setAdapter(youtubeRecyclerAdapter);

                    recyclerViewYoutube.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                        GestureDetector detector = new GestureDetector(PreviewActivity.this, new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return true;
                            }
                        });
                        @Override
                        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            View ChildView = rv.findChildViewUnder(e.getX(),e.getY());
                            if ((ChildView!=null) && detector.onTouchEvent(e)) {
                                int Position = rv.getChildAdapterPosition(ChildView);
                                String videoPath = youtubeVideoDataModel.getResults().get(Position).getKey();
                                textTrailer.setText(videoPath);
                                playVideo(videoPath);
//                                Toast.makeText(PreviewActivity.this, videoPath, Toast.LENGTH_SHORT).show();
                                startYoutubeData();
                            }
                            return false;
                        }

                        @Override
                        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });
                }

                else {
                    Toast.makeText(PreviewActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<YoutubeVideoDataModel> call, Throwable t) {

            }
        });
    }

    private void startYoutubeData() {
        youTubeView.initialize(Config.YOUTUBE_API_KEY,PreviewActivity.this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer = player;
            trailer = textTrailer.getText().toString();
//            Toast.makeText(this, trailer, Toast.LENGTH_SHORT).show();
            if (!trailer.equals("")) {
                player.cueVideo(trailer);
            }else {
                player.pause();
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    public void getMovieDetails(final Long movie_id) {

        Call<MovieDetailsModel> movieDetailsModelCall = apiInterface.MOVIE_DETAILS_MODEL_CALL(movie_id);
        movieDetailsModelCall.enqueue(new Callback<MovieDetailsModel>() {
            @Override
            public void onResponse(Call<MovieDetailsModel> call, Response<MovieDetailsModel> response) {
                if (response.isSuccessful()) {
                    MovieDetailsModel movieDetailsModel = response.body();
                    String title = movieDetailsModel.getTitle();
                    String date = movieDetailsModel.getReleaseDate();
                    String description = movieDetailsModel.getOverview();
                    if (TextUtils.isEmpty(title)) {
                        textTitle.setText("N/A");
                    }
                    else {
                        textTitle.setText(movieDetailsModel.getTitle());
                    }
                    if (TextUtils.isEmpty(date)) {
                        textDate.setText("N/A");
                    }
                    else {
                        textDate.setText(movieDetailsModel.getReleaseDate());
                    }
                    if (TextUtils.isEmpty(description)) {
                        textDescription.setText("N/A");
                    }
                    else {
                        textDescription.setText(movieDetailsModel.getOverview());
                    }
                    String runTime = String.valueOf(movieDetailsModel.getRuntime());
                    if (!runTime.equals("null")) {
                        String length = getDuration(movieDetailsModel.getRuntime());
                        textDuration.setText(length);
                    }
                    else {
                        textDuration.setText("N/A");
                    }
                    int movieGenre = movieDetailsModel.getGenres().size();
                    if (movieGenre == 0) {
                        textGenre.setText("N/A");
                    }
                    else {
                        int i;
                        String genre = "";
                        int size = movieDetailsModel.getGenres().size();
                        for (i=0;i<size;i++) {
                            if (i==(size-1)) {
                                genre = genre+movieDetailsModel.getGenres().get(i).getName();
                            }
                            else {
                                genre = genre+movieDetailsModel.getGenres().get(i).getName()+", ";
                            }
                            textGenre.setText(genre);
                        }
                    }
                    int production = movieDetailsModel.getProductionCompanies().size();
                    if (production == 0) {
                        recyclerView.setVisibility(View.GONE);
                        textViewProduction.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                    else {
                        getMovieProductionData(movieDetailsModel);
                    }
                    getYoutubeData(movie_id,movieDetailsModel.getPosterPath());
                    loader.setVisibility(View.GONE);
                }

                else {
                    Toast.makeText(PreviewActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieDetailsModel> call, Throwable t) {
                Toast.makeText(PreviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void btnProceed(View view) {
        Intent intent = new Intent(PreviewActivity.this,TheatreActivity.class);
        intent.putExtra("movie_id",movie_id);
        intent.putExtra("movie_name",textTitle.getText().toString());
        startActivity(intent);
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void getMovieProductionData(MovieDetailsModel movieDetailsModel) {

        RecyclerViewHorizontalAdapter = new RecyclerViewAdapter(movieDetailsModel,PreviewActivity.this);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        scrollView.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    public void playVideo(String key) {
        youTubePlayer.loadVideo(key);
    }

    public String getDuration(int time) {
        if (time == 0) {
            return "N/A";
        }
        else {
            int hours = time / 60;
            int minutes = time % 60;
            String length = hours + " hr " + minutes + " min";
            return length;
        }
    }

    public void imageBack(View view) {
        onBackPressed();
    }
}
